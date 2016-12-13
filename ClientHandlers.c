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
#include <errno.h>
#include <fcntl.h>

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
#ifdef ENABLE_LOG
    printf("First line: ");
    for (int i = 0; socketInfo->buffer->data[i] != '\r'; ++i)
    {
        printf("%c", socketInfo->buffer->data[i]);
    }
    printf("\n");
#endif
    if (strpos(socketInfo->buffer->data, "GET") != 0)
    {
#ifdef ENABLE_LOG
        printf("Not GET\n");
#endif
        sendError(storage, socketInfo, ERROR_501);
        return EXIT_FAILURE;
    }
#ifdef ENABLE_LOG
    printf("Method: GET\n");
#endif

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
#ifdef ENABLE_LOG
    printf("URL: %s\n", socketInfo->url);
#endif

    if (strpos(socketInfo->url, "http://") != 0)
    {
#ifdef ENABLE_LOG
        printf("Not http\n");
#endif
        sendError(storage, socketInfo, ERROR_505);
        return EXIT_FAILURE;
    }

    if (strpos(socketInfo->buffer->data + secondSpace + 2, "HTTP/1.0") != 0)
    {
#ifdef ENABLE_LOG
        printf("Version http isn't 1.0\n");
#endif
        sendError(storage, socketInfo, ERROR_505);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int setNonBlock(int socket)
{
    int flags = fcntl(socket, F_GETFD, 0);
    if(flags == ERROR)
    {
        perror("Can't get flags");
        return EXIT_FAILURE;
    }

    flags = flags | O_NONBLOCK;

    int code = fcntl(socket, F_SETFL, flags);
    if(code != EXIT_SUCCESS)
    {
        perror("Can't set non block");
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
#ifdef ENABLE_LOG
    printf("Domain: %s\n", domain);
#endif

    struct addrinfo *connectionAddr = getAddrInfo(domain, "http");
    free(domain);
    if (connectionAddr == NULL)
    {
        sendError(storage, socketInfo, ERROR_404);
        return EXIT_FAILURE;
    }

    *downloader = socket(connectionAddr->ai_family, connectionAddr->ai_socktype, connectionAddr->ai_protocol);
    if (*downloader == ERROR)
    {
        perror("Can't create downloader socket");
        freeaddrinfo(connectionAddr);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    int code = setNonBlock(*downloader);
    if(code != EXIT_SUCCESS)
    {
        freeaddrinfo(connectionAddr);
        close(*downloader);
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_FAILURE;
    }

    code = connect(*downloader, connectionAddr->ai_addr, connectionAddr->ai_addrlen);
    freeaddrinfo(connectionAddr);
    if (code != EXIT_SUCCESS && errno != EINPROGRESS)
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

#ifdef ENABLE_LOG
    printf("Header(%d): %s\n", downloaderSocketInfo->buffer->currentSize, downloaderSocketInfo->buffer->data);
    printf("Added output socket %d\n", downloaderSocketInfo->socket);
#endif

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
#ifdef ENABLE_LOG
        printf("Bad header\n");
#endif
        return EXIT_FAILURE;
    }

    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if (cacheRecord == NULL)
    {
#ifdef ENABLE_LOG
        printf("Create new cache record\n");
#endif
        return createNewCache(storage, socketInfo, cacheManager);
    }

#ifdef ENABLE_LOG
    printf("There is cache record\n");
#endif
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
#ifdef ENABLE_LOG
    printf("Socket status: wait header\n");
#endif

    char buffer[BUFFER_SIZE];
    ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
    if (size <= 0)
    {
#ifdef ENABLE_LOG
        printf("Client go away\n");
#endif
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
#ifdef ENABLE_LOG
    printf("Direct transfer\n");
#endif

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
#ifdef ENABLE_LOG
        printf("All data from buffer sent (%d)\n",
               socketInfo->relatedSockets[DOWNLOADER_SOCKET]->buffer->totalBytesCount);
#endif
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

    return EXIT_SUCCESS;
}

int clientCacheTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager,
                    struct CacheRecord *cache)
{
#ifdef ENABLE_LOG
    printf("Cache transfer\n");
#endif

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
#ifdef ENABLE_LOG
        printf("Sleep clientCacheTransfer\n");
#endif
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
#ifdef ENABLE_LOG
        printf("All data from cache send\n");
#endif
        disconnectClient(storage, cacheManager, socketInfo);
        return EXIT_SUCCESS;
    }

#ifdef ENABLE_LOG
    printf("It's a big file. Detach from cache record\n");
#endif
    stopReadCacheRecord(cacheManager, socketInfo);
    addRelatedSocket(cache->downloader, socketInfo);
    addRelatedSocket(socketInfo, cache->downloader);
    if (cache->clientsCount == 0)
    {
#ifdef ENABLE_LOG
        printf("Del cache\n");
#endif
        resumePollSocket(cache->downloader);
        delCacheRecord(cacheManager, socketInfo->url);
    }
    pausePollSocket(socketInfo);
    return EXIT_SUCCESS;
}

int clientBufferTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Buffer transfer\n");
#endif

    if (socketInfo->buffer->currentSize == 0)
    {
#ifdef ENABLE_LOG
        printf("End transfer from buffer\n");
#endif
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
#ifdef ENABLE_LOG
    printf("Socket status: send from socket\n");
#endif

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

#ifdef ENABLE_LOG
    printf("\nHandler Client for %d\n", socketInfo->socket);
#endif

    return subHandlers[socketInfo->status](storage, socketInfo, cacheManager);
}
