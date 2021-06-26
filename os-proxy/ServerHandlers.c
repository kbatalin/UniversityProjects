//
// Created by kir55rus on 05.12.16.
//

#include <netdb.h>
#include <memory.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netdb.h>
#include <zconf.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdbool.h>

#include "ServerHandlers.h"
#include "header.h"
#include "SocketsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"

int createDirectConnection(struct SocketInfo *socketInfo, struct CacheManager *cacheManager, struct CacheRecord *cache)
{
    for (int i = 0; i < cache->clientsCount; ++i)
    {
        addRelatedSocket(socketInfo, cache->clients[i]);
        addRelatedSocket(cache->clients[i], socketInfo);
        resumePollSocket(cache->clients[i]);
    }

    delCacheRecord(cacheManager, socketInfo->url);

    socketInfo->status = RECEIVE_TO_SOCKET;
    return EXIT_SUCCESS;
}

void resumeRelatedSockets(struct SocketInfo * socketInfo)
{
    if(socketInfo == NULL)
    {
        return;
    }

    for(int i = 0; i < socketInfo->countRelatedSockets; ++i)
    {
        resumePollSocket(socketInfo->relatedSockets[i]);
    }
}

int handlerServerWaitHeader(struct SocketsStorage *storage, struct SocketInfo *socketInfo,
                            struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Socket status: wait header\n");
#endif

    char buffer[BUFFER_SIZE];
    ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
    if (size <= 0)
    {
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
    {
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    int endFirstLinePos = strpos(socketInfo->buffer->data, "\r\n");
    if (endFirstLinePos == NPOS)
    {
        return EXIT_SUCCESS;
    }

    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    if (cache == NULL)
    {
        resumeRelatedSockets(socketInfo);
        socketInfo->status = RECEIVE_TO_SOCKET;
        return EXIT_SUCCESS;
    }

    int goodStatusPos = strpos(socketInfo->buffer->data, "200 OK");
    if (goodStatusPos == NPOS || goodStatusPos > endFirstLinePos)
    {
        return createDirectConnection(socketInfo, cacheManager, cache);
    }

    if (addCharsToCacheRecord(cache, cacheManager, socketInfo->buffer->data, socketInfo->buffer->currentSize) !=
        EXIT_SUCCESS)
    {
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    popCharsFromBuffer(socketInfo->buffer, socketInfo->buffer->currentSize);

    for (int i = 0; i < cache->clientsCount; ++i)
    {
        resumePollSocket(cache->clients[i]);
    }

    socketInfo->status = RECEIVE_TO_SOCKET;
    return EXIT_SUCCESS;
}

int directTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Direct transfer\n");
#endif
    int minBytesCount = socketInfo->buffer->totalBytesCount;

    for (int i = 0; i < socketInfo->countRelatedSockets; ++i)
    {
        minBytesCount = min(minBytesCount, socketInfo->relatedSockets[i]->buffer->totalBytesCount);
        resumePollSocket(socketInfo->relatedSockets[i]);
    }

    int bytesBefore = socketInfo->buffer->totalBytesCount - socketInfo->buffer->currentSize;
    int canThrow = max(0, minBytesCount - bytesBefore);

    popCharsFromBuffer(socketInfo->buffer, canThrow);

    int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;

    if (freeSpace == 0)
    {
        pausePollSocket(socketInfo);
        return EXIT_SUCCESS;
    }

    char *buffer = (char *) calloc(freeSpace, sizeof(char));
    if (buffer == NULL)
    {
        fprintf(stderr, "Can't allocate memory for buffer\n");
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    ssize_t size = recv(socketInfo->socket, buffer, freeSpace, 0);
    if (size <= 0)
    {
        free(buffer);
        pausePollSocket(socketInfo);
        socketInfo->status = END_WORKING;
        return EXIT_SUCCESS;
    }

    if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
    {
        free(buffer);
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }
    free(buffer);

    return EXIT_SUCCESS;
}

int cacheTransfer(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Cache transfer\n");
#endif

    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    if (cache == NULL)
    {
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    char buffer[BUFFER_SIZE];

    for (int i = 0; i < cache->clientsCount; ++i)
    {
        resumePollSocket(cache->clients[i]);
    }

    ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
    if (size <= 0)
    {
#ifdef ENABLE_LOG
        printf("Server go away\n");
#endif
        stopWriteCacheRecord(cacheManager, cache);
        disconnectServer(storage, cacheManager, socketInfo, false);

        return EXIT_SUCCESS;
    }

    if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
    {
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    if (addCharsToCacheRecord(cache, cacheManager, socketInfo->buffer->data, socketInfo->buffer->currentSize) ==
        EXIT_SUCCESS)
    {
        popCharsFromBuffer(socketInfo->buffer, socketInfo->buffer->currentSize);
        return EXIT_SUCCESS;
    }

    cache->status = BIG_FILE;
    pausePollSocket(socketInfo);

    return EXIT_SUCCESS;
}

int handlerServerReceiveToSocket(struct SocketsStorage *storage, struct SocketInfo *socketInfo,
                                 struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Socket status: receive to socket\n");
#endif

    if (socketInfo->countRelatedSockets > 0)
    {
        return directTransfer(storage, socketInfo, cacheManager);
    }

    return cacheTransfer(storage, socketInfo, cacheManager);
}

int handlerServerSendFromSocket(struct SocketsStorage *storage, struct SocketInfo *socketInfo,
                                struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("Socket status: send from socket\n");
#endif

    if (sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
    {
#ifdef ENABLE_LOG
        printf("Server go away\n");
#endif
        disconnectServer(storage, cacheManager, socketInfo, true);
        return EXIT_FAILURE;
    }

    if (isBufferEmpty(socketInfo->buffer))
    {
#ifdef ENABLE_LOG
        printf("Buffer is empty\n");
#endif
        eraseBuffer(socketInfo->buffer);
        socketInfo->status = WAIT_HEADER;
        socketInfo->pollfd->events = POLLIN;
    }

#ifdef ENABLE_LOG
    printf("End sending\n");
#endif
    return EXIT_SUCCESS;
}

int serverHandler(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    handlerType subHandlers[STATUS_COUNT];
    subHandlers[WAIT_HEADER] = handlerServerWaitHeader;
    subHandlers[RECEIVE_TO_SOCKET] = handlerServerReceiveToSocket;
    subHandlers[SEND_FROM_SOCKET] = handlerServerSendFromSocket;

#ifdef ENABLE_LOG
    printf("\nHandler Serevr for %d\n", socketInfo->socket);
#endif

    return subHandlers[socketInfo->status](storage, socketInfo, cacheManager);
}
