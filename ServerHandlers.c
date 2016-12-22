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

int getMinTotalBytes(struct SocketInfo *socketInfo)
{
    int minBytesCount = socketInfo->buffer->totalBytesCount;

    for (int i = 0; i < socketInfo->countRelatedSockets; ++i)
    {
        minBytesCount = min(minBytesCount, socketInfo->relatedSockets[i]->buffer->totalBytesCount);
    }

    return minBytesCount;
}

int waitRelatedClientsLeave(struct SocketInfo *socketInfo)
{
    while(socketInfo->countRelatedSockets > 0)
    {
#ifdef ENABLE_LOG
        printf("server %d: sleep (wait del related)\n", socketInfo->socket);
#endif
        int code = pthread_cond_broadcast(socketInfo->buffer->clientsCond);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't broadcast");
            return EXIT_FAILURE;
        }

        code = pthread_cond_wait(socketInfo->buffer->ownerCond, socketInfo->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't wait");
            return EXIT_FAILURE;
        }

#ifdef ENABLE_LOG
        printf("server %d: resume (wait del related)\n", socketInfo->socket);
#endif
    }
}

int waitDelRelated(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: wait del related\n", socketInfo->socket);
#endif

    int code = pthread_mutex_lock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    if(waitRelatedClientsLeave(socketInfo) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int setDirectTransferStatus(struct SocketInfo *socketInfo)
{
    int code = pthread_mutex_lock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    socketInfo->status = DIRECT_TRANSFER;

    code = pthread_mutex_unlock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int directTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: direct transfer\n", socketInfo->socket);
#endif

    if(setDirectTransferStatus(socketInfo) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    while(true)
    {
        int code = pthread_mutex_lock(socketInfo->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

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

        code = pthread_mutex_lock(socketInfo->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
        {
            free(buffer);
            socketInfo->status = ERROR_END;
            pthread_mutex_unlock(socketInfo->buffer->mutex);
            waitDelRelated(storage, socketInfo);
            return EXIT_FAILURE;
        }
        free(buffer);

        code = pthread_mutex_unlock(socketInfo->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        code = pthread_cond_broadcast(socketInfo->buffer->clientsCond);
        if(code != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }
}

int safeServerGetCacheRecord(struct CacheRecord **pCacheRecord, struct CacheManager *cacheManager, const char *key)
{
    int code = pthread_mutex_lock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    *pCacheRecord = getCacheRecord(cacheManager, key);

    code = pthread_mutex_unlock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int waitClientsLeave(struct CacheRecord *cacheRecord)
{
    while (cacheRecord->clientsCount > 0)
    {
        int code = pthread_cond_broadcast(cacheRecord->clientsCond);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't broadcast");
            return EXIT_FAILURE;
        }

        code = pthread_cond_wait(cacheRecord->downloaderCond, cacheRecord->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't wait");
            return EXIT_FAILURE;
        }
    }

    return EXIT_SUCCESS;
}

int waitDelCache(struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: wait del cache\n", socketInfo->socket);
#endif
    int code = pthread_mutex_lock(cacheManager->mutex);
    struct CacheRecord *cache = getCacheRecord(cacheManager, socketInfo->url);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_mutex_lock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_FAILURE;
    }

    if(waitClientsLeave(cache) != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(cache->mutex);
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unclock mutex");
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("server %d: del cache record\n", socketInfo->socket);
#endif

    delCacheRecord(cacheManager, socketInfo->url);

    code = pthread_mutex_unlock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unclock mutex");
        return EXIT_FAILURE;
    }

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

        int code = pthread_mutex_lock(cacheManager->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't lock mutex");
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);

        code = pthread_mutex_lock(cacheRecord->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't lock mutex");
            pthread_mutex_unlock(cacheManager->mutex);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

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

        code = pthread_mutex_unlock(cacheManager->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't unlock mutex");
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        if(waitClientsLeave(cacheRecord) != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        code = pthread_mutex_unlock(cacheRecord->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't unlock mutex");
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }
}

int createDirectConnection(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("server %d: create direct connection\n", socketInfo->socket);
#endif

    int code = pthread_mutex_lock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' lock mutex");
        return EXIT_FAILURE;
    }

    socketInfo->status = DIRECT_TRANSFER;

    code = pthread_mutex_unlock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' unlock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_cond_broadcast(socketInfo->buffer->clientsCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' broadcast");
        return EXIT_FAILURE;
    }

    return directTransfer(storage, socketInfo);
}

int tryDelCacheRecord(struct CacheManager *cacheManager, struct CacheRecord * cacheRecord, struct SocketInfo * socketInfo)
{
    int code = pthread_mutex_lock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' lock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_mutex_lock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' lock mutex");
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_FAILURE;
    }

    if(cacheRecord->clientsCount == 0)
    {
        pthread_mutex_unlock(cacheRecord->mutex);
        delCacheRecord(cacheManager, socketInfo->url);
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_SUCCESS;
    }

    code = pthread_mutex_unlock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' unlock mutex");
        pthread_mutex_unlock(cacheManager->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_FAILURE;
}

int safeExitFromCacheTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                              struct CacheManager *cacheManager, struct CacheRecord *cache)
{
    int code = pthread_mutex_lock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    cache->status = ERROR_CACHE;

    code = pthread_mutex_unlock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    waitDelCache(socketInfo, cacheManager);
    delThreadFromStorage(storage, socketInfo->socket);

    return EXIT_SUCCESS;
}

int afterCacheTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                       struct CacheManager *cacheManager, struct CacheRecord *cache)
{
#ifdef ENABLE_LOG
    printf("server %d: go away (cache transfer)\n", socketInfo->socket);
#endif
    int code = pthread_mutex_lock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    stopWriteCacheRecord(cacheManager, cache);

    code = pthread_mutex_unlock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    code = pthread_cond_broadcast(cache->clientsCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't broadcast");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int cacheTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: cache transfer\n", socketInfo->socket);
#endif

    struct CacheRecord *cache;
    if(safeServerGetCacheRecord(&cache, cacheManager, socketInfo->url) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

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
                safeExitFromCacheTransfer(storage, socketInfo, cacheManager, cache);
                return EXIT_FAILURE;
            }
        }

        int code = pthread_mutex_lock(cache->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't lock mutex");
            safeExitFromCacheTransfer(storage, socketInfo, cacheManager, cache);
            return EXIT_FAILURE;
        }

#ifndef ENABLE_RESUMING
        if(cache->clientsCount == 0)
        {
#ifdef ENABLE_LOG
            printf("server %d: has not clients. try del cache (cache transfer)\n", socketInfo->socket);
#endif

            pthread_mutex_unlock(cache->mutex);
            if(tryDelCacheRecord(cacheManager, cache, socketInfo) == EXIT_SUCCESS)
            {
                delThreadFromStorage(storage, socketInfo->socket);
                return EXIT_SUCCESS;
            }
            pthread_mutex_lock(cache->mutex);
        }
#endif

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

        code = pthread_mutex_unlock(cache->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't unlock mutex");
            safeExitFromCacheTransfer(storage, socketInfo, cacheManager, cache);
            return EXIT_FAILURE;
        }

        code = pthread_cond_broadcast(cache->clientsCond);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't broadcast");
            safeExitFromCacheTransfer(storage, socketInfo, cacheManager, cache);
            return EXIT_FAILURE;
        }

        popCharsFromBuffer(socketInfo->buffer, socketInfo->buffer->currentSize);
    }
    while(size > 0);

    return afterCacheTransfer(storage, socketInfo, cacheManager, cache);
}

int safeExitFromWaitHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                           struct CacheManager *cacheManager, struct CacheRecord *cache)
{
    int code = pthread_mutex_lock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    cache->status = ERROR_CACHE;

    code = pthread_mutex_unlock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    waitDelCache(socketInfo, cacheManager);
    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int serverWaitHeaderFirstLineProcess(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                                     struct CacheManager *cacheManager, struct CacheRecord *cache)
{
    int code = pthread_mutex_lock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    int endFirstLinePos = strpos(socketInfo->buffer->data, "\r\n");
    int goodStatusPos = strpos(socketInfo->buffer->data, "200 OK");
    if (goodStatusPos != NPOS && goodStatusPos <= endFirstLinePos)
    {
        socketInfo->status = CACHE_TRANSFER;
        pthread_mutex_unlock(cache->mutex);

        return cacheTransfer(storage, socketInfo, cacheManager);
    }

    socketInfo->status = DIRECT_TRANSFER;
    cache->status = BAD_ANSWER;

    code = pthread_mutex_unlock(cache->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    code = pthread_cond_broadcast(cache->clientsCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't broadcast");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    safeDelCacheRecord(storage, socketInfo, cacheManager);

    return directTransfer(storage, socketInfo);
}

int serverWaitHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                            struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: wait header\n", socketInfo->socket);
#endif

    struct CacheRecord *cache;
    if(safeServerGetCacheRecord(&cache, cacheManager, socketInfo->url) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("server %d: find cache record\n", socketInfo->socket);
#endif

    do
    {
        char buffer[BUFFER_SIZE];
        ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
        if (size <= 0)
        {
            safeExitFromWaitHeader(storage, socketInfo, cacheManager, cache);
            return EXIT_FAILURE;
        }

        if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
        {
            safeExitFromWaitHeader(storage, socketInfo, cacheManager, cache);
            return EXIT_FAILURE;
        }
    }
    while(strpos(socketInfo->buffer->data, "\r\n") == NPOS);

    return serverWaitHeaderFirstLineProcess(storage, socketInfo, cacheManager, cache);
}

int waitBufferFromSendHeader(struct SocketInfo *socketInfo)
{
    while(socketInfo->buffer->currentSize == 0)
    {
#ifdef ENABLE_LOG
        printf("server %d: wait headers from client\n", socketInfo->socket);
#endif

        int code = pthread_cond_wait(socketInfo->buffer->ownerCond, socketInfo->buffer->mutex);
        if (code != EXIT_SUCCESS)
        {
            printError(code, "Can't wait");
            return EXIT_FAILURE;
        }
    }

    return EXIT_SUCCESS;
}

int safeDelCacheFromSendHeader(struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    int code = pthread_mutex_lock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    struct CacheRecord * cacheRecord = getCacheRecord(cacheManager, socketInfo->url);

    code = pthread_mutex_unlock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    if(cacheRecord == NULL)
    {
        return EXIT_SUCCESS;
    }

    code = pthread_mutex_lock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    cacheRecord->status = ERROR_CACHE;

    code = pthread_mutex_unlock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    waitDelCache(socketInfo, cacheManager);
    return EXIT_SUCCESS;
}

int safeExitFromSendHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    if(safeDelCacheFromSendHeader(socketInfo, cacheManager) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    socketInfo->status = ERROR_END;
    int code = pthread_cond_broadcast(socketInfo->buffer->clientsCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't broadcast");
    }

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

int handlerServerSendFromSocket(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                                struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("server %d: send from socket\n", socketInfo->socket);
#endif

    pthread_mutex_lock(socketInfo->buffer->mutex);
    if(waitBufferFromSendHeader(socketInfo) != EXIT_SUCCESS)
    {
        safeExitFromSendHeader(storage, socketInfo, cacheManager);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("server %d: send headers to server\n", socketInfo->socket);
#endif

    while(socketInfo->buffer->currentSize > 0)
    {
        if (sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) > 0)
        {
            continue;
        }

#ifdef ENABLE_LOG
        printf("server %d: go away. status: %d (send from socket)\n", socketInfo->socket, socketInfo->status);
#endif
        pthread_mutex_unlock(socketInfo->buffer->mutex);

        safeExitFromSendHeader(storage, socketInfo, cacheManager);
        return EXIT_FAILURE;
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
