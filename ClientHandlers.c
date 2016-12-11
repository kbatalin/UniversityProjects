//
// Created by kir55rus on 05.12.16.
//

#include <netdb.h>
#include <memory.h>
#include <stdio.h>
#include <stdlib.h>
#include <zconf.h>
#include <stdbool.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <signal.h>

#include "ClientHandlers.h"
#include "header.h"
#include "SocketsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"
#include "ServerHandlers.h"

int checkHeader(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    printf("First line: ");
    for (int i = 0; socketInfo->buffer->data[i] != '\r'; ++i)
    {
        printf("%c", socketInfo->buffer->data[i]);
    }
    printf("\n");

    if (strpos(socketInfo->buffer->data, "GET") != 0)
    {
        printf("Not GET\n");
        sendError(storage, socketInfo, ERROR_501);
        return EXIT_FAILURE;
    }
    printf("Method: GET\n");

    int firstSpace = strpos(socketInfo->buffer->data, " ");
    int secondSpace = strpos(socketInfo->buffer->data + firstSpace + 1, " ") + firstSpace;

    int urlLength = secondSpace - firstSpace;
    char *url = (char *) calloc(urlLength + 1, sizeof(char));
    if (url == NULL)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    substr(socketInfo->buffer->data, url, firstSpace + 1, secondSpace - firstSpace);
    url[urlLength] = '\0';
    if (addUrlSocketInfo(socketInfo, url) != EXIT_SUCCESS)
    {
        free(url);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }
    free(url);

    printf("URL: %s\n", socketInfo->url);

    if (strpos(socketInfo->url, "http://") != 0)
    {
        printf("Not http\n");
        sendError(storage, socketInfo, ERROR_505);
        return EXIT_FAILURE;
    }

    if (strpos(socketInfo->buffer->data + secondSpace + 2, "HTTP/1.0") != 0)
    {
        printf("Version http isn't 1.0\n");
        sendError(storage, socketInfo, ERROR_505);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int createDownloaderSocket(int *downloader, struct SocketInfo *socketInfo, struct SocketsStorage *storage,
                           struct CacheManager *cacheManager)
{
    int httpLength = strlen("http://");
    int slashPos = strpos(socketInfo->url + httpLength, "/") + httpLength;
    if (slashPos == NPOS)
    {
        slashPos = strlen(socketInfo->url) - 1;
    }

    int domainLength = slashPos - httpLength;
    char *domain = (char *) calloc(domainLength + 1, sizeof(char));
    if (domain == NULL)
    {
        fprintf(stderr, "Can't allocate memory for domain\n");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    substr(socketInfo->url, domain, httpLength, domainLength);
    domain[domainLength] = '\0';
    printf("Domain: %s\n", domain);

    struct addrinfo *connectionAddr = getAddrInfo(domain, "http");
    free(domain);
    if (connectionAddr == NULL)
    {
        printf("Can't resolve host\n");
        sendError(storage, socketInfo, ERROR_404);
        return EXIT_FAILURE;
    }

    *downloader = socket(connectionAddr->ai_family, connectionAddr->ai_socktype, connectionAddr->ai_protocol);
    if (*downloader == ERROR)
    {
        perror("Can't create downloader socket");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    int code = connect(*downloader, connectionAddr->ai_addr, connectionAddr->ai_addrlen);
    freeaddrinfo(connectionAddr);

    if (code != EXIT_SUCCESS)
    {
        perror("Can't connect to server");
        close(*downloader);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int createNewCache(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    int downloader;
    if (createDownloaderSocket(&downloader, socketInfo, storage, cacheManager) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    struct SocketInfo *downloaderSocketInfo;
    if (addSocketToStorage(&downloaderSocketInfo, storage, downloader, POLLOUT) != EXIT_SUCCESS)
    {
        fprintf(stderr, "Can't add socket to storage\n");
        close(downloader);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }
    downloaderSocketInfo->handler = serverHandler;
    downloaderSocketInfo->status = SEND_FROM_SOCKET;

    if (addUrlSocketInfo(downloaderSocketInfo, socketInfo->url) != EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        disconnectServer(storage, cacheManager, downloaderSocketInfo, true);
        return EXIT_FAILURE;
    }

    struct CacheRecord *cacheRecord;
    if (createNewCacheRecord(&cacheRecord, cacheManager, socketInfo->url, downloaderSocketInfo, socketInfo) ==
        EXIT_SUCCESS)
    {
        cacheRecord->downloader = downloaderSocketInfo;
    }
    else if (addRelatedSocket(downloaderSocketInfo, socketInfo) != EXIT_SUCCESS ||
             addRelatedSocket(socketInfo, downloaderSocketInfo) != EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        disconnectServer(storage, cacheManager, downloaderSocketInfo, true);
        return EXIT_FAILURE;
    }

    int keepAlivePos = strpos(socketInfo->buffer->data, "Connection: keep-alive\r\n");
    if (keepAlivePos != NPOS)
    {
        int keepAliveLength = strlen("Connection: keep-alive\r\n");
        strerase(socketInfo->buffer->data, keepAlivePos, keepAliveLength);
        socketInfo->buffer->currentSize -= keepAliveLength;
    }

    if (moveCharsToBuffer(downloaderSocketInfo->buffer, socketInfo->buffer, socketInfo->buffer->currentSize) !=
        EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        disconnectServer(storage, cacheManager, downloaderSocketInfo, true);
        return EXIT_FAILURE;
    }

    printf("Header(%d): %s\n", downloaderSocketInfo->buffer->currentSize, downloaderSocketInfo->buffer->data);
    printf("Added output socket %d\n", downloaderSocketInfo->socket);

    eraseBuffer(socketInfo->buffer);
    pausePollSocket(socketInfo);
    socketInfo->pollfd->events = POLLOUT;
    socketInfo->status = SEND_FROM_SOCKET;
    return EXIT_SUCCESS;
}

int processHeader(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    if (checkHeader(storage, socketInfo, cacheManager) != EXIT_SUCCESS)
    {
        printf("Bad header\n");
        return EXIT_FAILURE;
    }

    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if (cacheRecord == NULL)
    {
        printf("Create new cache record\n");
        return createNewCache(storage, socketInfo, cacheManager);
    }

    printf("There is cache record\n");
    if (addCacheRecordReader(cacheRecord, socketInfo) != EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    eraseBuffer(socketInfo->buffer);
    socketInfo->pollfd->events = POLLOUT;
    socketInfo->status = SEND_FROM_SOCKET;
    return EXIT_SUCCESS;
}

int clientWaitHeader(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    printf("Socket status: wait header\n");

    char buffer[BUFFER_SIZE];
    ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
    if (size <= 0)
    {
        printf("Client go away\n");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    if (strpos(socketInfo->buffer->data, "\r\n\r\n") == NPOS)
    {
        return EXIT_SUCCESS;
    }

    return processHeader(storage, socketInfo, cacheManager);
}

int clientDirectTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    printf("Direct transfer\n");

    int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;
    int canTake = min(freeSpace, socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->totalBytesCount -
                                 socketInfo->buffer->totalBytesCount);
    if (canTake > 0)
    {
        resumePollSocket(socketInfo->relatedSockets[DOWNLOADER_SOCKET]);
    }

    char *from = socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->data +
                 socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->currentSize -
                 (socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->totalBytesCount -
                  socketInfo->buffer->totalBytesCount);
    if (addCharsToBuffer(socketInfo->buffer, from, canTake) != EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    if (socketInfo->buffer->currentSize == 0 && socketInfo->relatedSockets[DOWNLOADER_SOCKET]->status != END_WORKING)
    {
        pausePollSocket(socketInfo);
        return EXIT_SUCCESS;
    }

    if (socketInfo->buffer->currentSize > 0 && sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    if (socketInfo->relatedSockets[DOWNLOADER_SOCKET]->status == END_WORKING &&
        socketInfo->buffer->totalBytesCount == socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->totalBytesCount &&
        isBufferEmpty(socketInfo->buffer))
    {
        printf("All data from buffer sent (%d)\n",
               socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->totalBytesCount);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    return EXIT_SUCCESS;
}

int clientCacheTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager,
                    struct CacheRecord *cache)
{
    printf("Cache transfer\n");

    if (cache == NULL)
    {
        fprintf(stderr, "Has not cache\n");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;
    int inCache = cache->currentDataSize - socketInfo->buffer->totalBytesCount;

    int canTake = min(freeSpace, inCache);
    if (inCache == 0 && cache->status == PARTIAL && isBufferEmpty(socketInfo->buffer))
    {
        printf("Sleep clientCacheTransfer\n");
        resumePollSocket(cache->downloader);
        pausePollSocket(socketInfo);
        return EXIT_SUCCESS;
    }
    if (addCharsToBuffer(socketInfo->buffer, cache->data + socketInfo->buffer->totalBytesCount, canTake) !=
        EXIT_SUCCESS)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    if (socketInfo->buffer->currentSize > 0 && sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    if (cache->status == PARTIAL || cache->currentDataSize - socketInfo->buffer->totalBytesCount != 0)
    {
        return EXIT_SUCCESS;
    }
    if (cache->status == FULL)
    {
        printf("All data from cache send\n");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    printf("It's a big file. Detach from cache record\n");
    stopReadCacheRecord(cacheManager, socketInfo);
    addRelatedSocket(cache->downloader, socketInfo);
    addRelatedSocket(socketInfo, cache->downloader);
    if (cache->clientsCount == 0)
    {
        printf("Del cache\n");
        resumePollSocket(cache->downloader);
        delCacheRecord(cacheManager, socketInfo->url);
    }
    pausePollSocket(socketInfo);
    return EXIT_SUCCESS;
}

int clientBufferTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    printf("Buffer transfer\n");

    if (socketInfo->buffer->currentSize == 0)
    {
        printf("End transfer from buffer\n");
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    if (sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
    {
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    return EXIT_SUCCESS;
}

int clientSendFromSocket(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    printf("Socket status: send from socket\n");

    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    if (socketInfo->countRelatedSockets == 0 && cache != NULL)
    {
        return clientCacheTransfer(storage, socketInfo, cacheManager, cache);
    }
    else if (socketInfo->countRelatedSockets == 0)
    {
        return clientBufferTransfer(storage, socketInfo, cacheManager);
    }

    return clientDirectTransfer(storage, socketInfo, cacheManager);
}

int clientHandler(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    handlerType subHandlers[STATUS_COUNT];
    subHandlers[WAIT_HEADER] = clientWaitHeader;
    subHandlers[SEND_FROM_SOCKET] = clientSendFromSocket;

    printf("\nHandler Client for %d\n", socketInfo->socket);

    return subHandlers[socketInfo->status](storage, socketInfo, cacheManager);
}
