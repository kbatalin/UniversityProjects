//
// Created by kir55rus on 05.12.16.
//

#include <netdb.h>
#include <memory.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/socket.h>
#include <netdb.h>
#include <zconf.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdbool.h>

#include "ServerHandlers.h"
#include "header.h"
#include "ThreadsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"

//int createDirectConnection(struct SocketInfo *socketInfo, struct CacheManager *cacheManager, struct CacheRecord *cache)
//{
//    for (int i = 0; i < cache->clientsCount; ++i)
//    {
//        addRelatedSocket(socketInfo, cache->clients[i]);
//        addRelatedSocket(cache->clients[i], socketInfo);
////        resumePollSocket(cache->clients[i]);
//    }
//
//    delCacheRecord(cacheManager, socketInfo->url);
//
////    socketInfo->status = RECEIVE_TO_SOCKET;
//    return EXIT_SUCCESS;
//}
//
//void resumeRelatedSockets(struct SocketInfo * socketInfo)
//{
//    if(socketInfo == NULL)
//    {
//        return;
//    }
//
//    for(int i = 0; i < socketInfo->countRelatedSockets; ++i)
//    {
////        resumePollSocket(socketInfo->relatedSockets[i]);
//    }
//}
//


int getMinTotalBytes(struct SocketInfo *socketInfo)
{
    int minBytesCount = socketInfo->buffer->totalBytesCount;

    for (int i = 0; i < socketInfo->countRelatedSockets; ++i)
    {
//        pthread_mutex_lock(socketInfo->relatedSockets[i]->buffer->mutex);
        minBytesCount = min(minBytesCount, socketInfo->relatedSockets[i]->buffer->totalBytesCount);
//        pthread_mutex_unlock(socketInfo->relatedSockets[i]->buffer->mutex);
    }

    return minBytesCount;
}

int waitDelRelated(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: wait del related\n", socketInfo->socket);
#endif

    pthread_mutex_lock(socketInfo->buffer->mutex);

    while(socketInfo->countRelatedSockets > 0)
    {
#ifdef ENABLE_LOG
        printf("server %d: sleep (wait del related)\n", socketInfo->socket);
#endif
        pthread_cond_broadcast(socketInfo->buffer->clientsCond);
        pthread_cond_wait(socketInfo->buffer->ownerCond, socketInfo->buffer->mutex);
#ifdef ENABLE_LOG
        printf("server %d: resume (wait del related)\n", socketInfo->socket);
#endif
    }

    pthread_mutex_unlock(socketInfo->buffer->mutex);

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int directTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: direct transfer\n", socketInfo->socket);
#endif

    pthread_mutex_lock(socketInfo->buffer->mutex);
    socketInfo->status = DIRECT_TRANSFER;
    pthread_mutex_unlock(socketInfo->buffer->mutex);

    while(true)
    {
        pthread_mutex_lock(socketInfo->buffer->mutex);

        int bytesBefore = socketInfo->buffer->totalBytesCount - socketInfo->buffer->currentSize;
        int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;
        int canThrow;
        while(socketInfo->countRelatedSockets > 0 && (canThrow = getMinTotalBytes(socketInfo) - bytesBefore) <= 0 && freeSpace <= 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: sleep (direct transfer)\n", socketInfo->socket);
#endif
            pthread_cond_broadcast(socketInfo->buffer->clientsCond);
            pthread_cond_wait(socketInfo->buffer->ownerCond, socketInfo->buffer->mutex);

#ifdef ENABLE_LOG
            printf("server %d: resume (direct transfer)\n", socketInfo->socket);
#endif
        }

        if(socketInfo->countRelatedSockets == 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: disconnect. have not clients (direct transfer)\n", socketInfo->socket);
#endif

            pthread_mutex_unlock(socketInfo->buffer->mutex);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_SUCCESS;
        }

#ifdef ENABLE_LOG
        printf("server %d: throw %d bytes (direct transfer)\n", socketInfo->socket, canThrow);
#endif

        popCharsFromBuffer(socketInfo->buffer, canThrow);
        freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;

        char *buffer = (char *) calloc(freeSpace, sizeof(char));
        if (buffer == NULL)
        {
            fprintf(stderr, "Can't allocate memory for buffer\n");

            socketInfo->status = ERROR_END;
            pthread_mutex_unlock(socketInfo->mutex);

            waitDelRelated(storage, socketInfo);

            return EXIT_FAILURE;
        }

        pthread_mutex_unlock(socketInfo->buffer->mutex);

        ssize_t size = recv(socketInfo->socket, buffer, freeSpace, 0);
        if (size <= 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: go away (direct transfer)\n", socketInfo->socket);
#endif
            free(buffer);
            pthread_mutex_lock(socketInfo->buffer->mutex);
            socketInfo->status = OK_END;
            pthread_mutex_unlock(socketInfo->buffer->mutex);
            pthread_cond_broadcast(socketInfo->buffer->clientsCond);

            waitDelRelated(storage, socketInfo);
            return EXIT_SUCCESS;
        }

#ifdef ENABLE_LOG
        printf("server %d: recv %zd bytes (direct transfer)\n", socketInfo->socket, size);
#endif

        pthread_mutex_lock(socketInfo->buffer->mutex);

        if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
        {
            free(buffer);
            socketInfo->status = ERROR_END;
            pthread_mutex_unlock(socketInfo->buffer->mutex);
            waitDelRelated(storage, socketInfo);
            return EXIT_FAILURE;
        }
        free(buffer);

        pthread_mutex_unlock(socketInfo->buffer->mutex);
        pthread_cond_broadcast(socketInfo->buffer->clientsCond);
    }
}

int waitDelCache(struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: wait del cache\n", socketInfo->socket);
#endif
    pthread_mutex_lock(cacheManager->mutex);
    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);

    pthread_mutex_lock(cache->mutex);
    while(cache->clientsCount > 0)
    {
#ifdef ENABLE_LOG
        printf("server %d: sleep (wait del cache)\n", socketInfo->socket);
#endif
        pthread_cond_broadcast(cache->clientsCond);
        pthread_cond_wait(cache->downloaderCond, cache->mutex);

#ifdef ENABLE_LOG
        printf("server %d: resume (wait del cache)\n", socketInfo->socket);
#endif
    }
    pthread_mutex_unlock(cache->mutex);

#ifdef ENABLE_LOG
    printf("server %d: del cache record\n", socketInfo->socket);
#endif

    delCacheRecord(cacheManager, socketInfo->url);

    pthread_mutex_unlock(cacheManager->mutex);

    return EXIT_SUCCESS;
}

int safeDelCacheRecord(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: del cache record\n", socketInfo->socket);
#endif

    while(true)
    {
#ifdef ENABLE_LOG
        printf("server %d: wait clients detached from cache\n", socketInfo->socket);
#endif

        pthread_mutex_lock(cacheManager->mutex);
        struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);

        pthread_mutex_lock(cacheRecord->mutex);

        if (cacheRecord->clientsCount == 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: record without clients. delete\n", socketInfo->socket);
#endif
            pthread_mutex_unlock(cacheRecord->mutex);
            delCacheRecord(cacheManager, socketInfo->url);
            pthread_mutex_unlock(cacheManager->mutex);
            return EXIT_SUCCESS;
        }

        pthread_mutex_unlock(cacheManager->mutex);

        while (cacheRecord->clientsCount > 0)
        {
            pthread_cond_broadcast(cacheRecord->clientsCond);
            pthread_cond_wait(cacheRecord->downloaderCond, cacheRecord->mutex);
        }

        pthread_mutex_unlock(cacheRecord->mutex);
    }
}

int createDirectConnection(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: create direct connection\n", socketInfo->socket);
#endif

    pthread_mutex_lock(socketInfo->buffer->mutex);
    socketInfo->status = DIRECT_TRANSFER;
    pthread_mutex_unlock(socketInfo->buffer->mutex);

    pthread_cond_broadcast(socketInfo->buffer->clientsCond);

    return directTransfer(storage, socketInfo);
}

int cacheTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: cache transfer\n", socketInfo->socket);
#endif

    pthread_mutex_lock(cacheManager->mutex);
    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    pthread_mutex_unlock(cacheManager->mutex);

#ifdef ENABLE_LOG
    printf("server %d: find cache record\n", socketInfo->socket);
#endif

    ssize_t size;
    do
    {
        char buffer[BUFFER_SIZE];
        size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);

        if(size > 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: recv %zd bytes (cache transfer)\n", socketInfo->socket, size);
#endif

            if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
            {
                pthread_mutex_lock(cache->mutex);
                cache->status = ERROR_CACHE;
                pthread_mutex_unlock(cache->mutex);
                waitDelCache(socketInfo, cacheManager);
//            close(socketInfo->socket);
                delThreadFromStorage(storage, socketInfo->socket);
                return EXIT_FAILURE;
            }
        }

        pthread_mutex_lock(cache->mutex);

        if (addCharsToCacheRecord(cache, cacheManager, socketInfo->buffer->data, socketInfo->buffer->currentSize) !=
            EXIT_SUCCESS)
        {
#ifdef ENABLE_LOG
            printf("server %d: it's a big file\n", socketInfo->socket);
#endif
            cache->status = BIG_FILE;
            pthread_mutex_unlock(cache->mutex);
            safeDelCacheRecord(storage, socketInfo, cacheManager);
            return createDirectConnection(storage, socketInfo);
        }

        pthread_mutex_unlock(cache->mutex);
        pthread_cond_broadcast(cache->clientsCond);

        popCharsFromBuffer(socketInfo->buffer, socketInfo->buffer->currentSize);

    }
    while(size > 0);

#ifdef ENABLE_LOG
    printf("server %d: go away (cache transfer)\n", socketInfo->socket);
#endif
    pthread_mutex_lock(cache->mutex);
    stopWriteCacheRecord(cacheManager, cache);
    pthread_mutex_unlock(cache->mutex);
    pthread_cond_broadcast(cache->clientsCond);

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int serverWaitHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                            struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: wait header\n", socketInfo->socket);
#endif

    pthread_mutex_lock(cacheManager->mutex);
    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    pthread_mutex_unlock(cacheManager->mutex);

#ifdef ENABLE_LOG
    printf("server %d: find cache record\n", socketInfo->socket);
#endif

    int endFirstLinePos;
    do
    {
        char buffer[BUFFER_SIZE];
        ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
        if (size <= 0)
        {
            pthread_mutex_lock(cache->mutex);
            cache->status = ERROR_CACHE;
            pthread_mutex_unlock(cache->mutex);
            waitDelCache(socketInfo, cacheManager);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
        {
            pthread_mutex_lock(cache->mutex);
            cache->status = ERROR_CACHE;
            pthread_mutex_unlock(cache->mutex);
            waitDelCache(socketInfo, cacheManager);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }
    while((endFirstLinePos = strpos(socketInfo->buffer->data, "\r\n")) == NPOS);

    pthread_mutex_lock(cache->mutex);

    int goodStatusPos = strpos(socketInfo->buffer->data, "200 OK");
    if (goodStatusPos == NPOS || goodStatusPos > endFirstLinePos)
    {
        socketInfo->status = DIRECT_TRANSFER;
        cache->status = BAD_ANSWER;
        pthread_mutex_unlock(cache->mutex);
        pthread_cond_broadcast(cache->clientsCond);

        safeDelCacheRecord(storage, socketInfo, cacheManager);

        return directTransfer(storage, socketInfo);
    }

    socketInfo->status = CACHE_TRANSFER;
    pthread_mutex_unlock(cache->mutex);

    return cacheTransfer(storage, socketInfo, cacheManager);
}

int handlerServerSendFromSocket(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                                struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: send from socket\n", socketInfo->socket);
#endif

    pthread_mutex_lock(socketInfo->buffer->mutex);
    while(socketInfo->buffer->currentSize == 0)
    {
#ifdef ENABLE_LOG
        printf("server %d: wait headers from client\n", socketInfo->socket);
#endif

        pthread_cond_wait(socketInfo->buffer->ownerCond, socketInfo->buffer->mutex);
    }

#ifdef ENABLE_LOG
    printf("server %d: send headers to server\n", socketInfo->socket);
#endif

    while(socketInfo->buffer->currentSize > 0)
    {
        if (sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: go away. status: %d (send from socket)\n", socketInfo->socket, socketInfo->status);
#endif
            pthread_mutex_unlock(socketInfo->buffer->mutex);

            if(socketInfo->status == PREV_CACHE_TRANSFER)
            {
                pthread_mutex_lock(cacheManager->mutex);
                struct CacheRecord * cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
                pthread_mutex_unlock(cacheManager->mutex);

                if(cacheRecord != NULL)
                {
                    pthread_mutex_lock(cacheRecord->mutex);
                    cacheRecord->status = ERROR_CACHE;
                    pthread_mutex_unlock(cacheRecord->mutex);
                    waitDelCache(socketInfo, cacheManager);
                }
            }

            socketInfo->status = ERROR_END;
            pthread_cond_broadcast(socketInfo->buffer->clientsCond);
//            close(socketInfo->socket);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }
    eraseBuffer(socketInfo->buffer);
    pthread_mutex_unlock(socketInfo->buffer->mutex);

#ifdef ENABLE_LOG
    printf("server %d: end sending headers to server\n", socketInfo->socket);
#endif

    if(socketInfo->status == PREV_DIRECT_TRANSFER)
    {
        return directTransfer(storage, socketInfo);
    }

    return serverWaitHeader(storage, socketInfo, cacheManager);
}
