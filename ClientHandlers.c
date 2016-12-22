//
// Created by kir55rus on 05.12.16.
//

#include <netdb.h>
#include <memory.h>
#include <stdio.h>
#include <stdlib.h>
#include <zconf.h>
#include <pthread.h>
#include <stdbool.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>

#include "ClientHandlers.h"
#include "header.h"
#include "ThreadsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"
#include "ServerHandlers.h"



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

int safeAddCacheRecordReader(struct CacheRecord *cacheRecord, struct SocketInfo *socketInfo)
{
    int code = pthread_mutex_lock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    if(addCacheRecordReader(cacheRecord, socketInfo) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(cacheRecord->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(cacheRecord->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int waitBufferClientDirection(struct SocketInfo *downloader, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("client %d: sleep (direct transfer)\n", socketInfo->socket);
#endif

    int code = pthread_cond_signal(downloader->buffer->ownerCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't signal");
        return EXIT_FAILURE;
    }

    code = pthread_cond_wait(downloader->buffer->clientsCond, downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't wait");
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: resume (direct transfer)\n", socketInfo->socket);
#endif

    return EXIT_SUCCESS;
}

int clientDirectTransferSend(struct ThreadsStorage *storage, struct SocketInfo *downloader, struct SocketInfo *socketInfo)
{
    ssize_t size = 0;
    if(socketInfo->buffer->currentSize <= 0)
    {
        return EXIT_SUCCESS;
    }

    size = sendFromBuffer(socketInfo, socketInfo->buffer->currentSize);
    if(size > 0)
    {
#ifdef ENABLE_LOG
        printf("client %d: send %zd bytes. current size: %d (direct transfer)\n", socketInfo->socket, size, socketInfo->buffer->currentSize);
#endif

        return EXIT_SUCCESS;
    }

#ifdef ENABLE_LOG
    printf("client %d: go away (direct transfer)\n", socketInfo->socket);
#endif

    int code = pthread_mutex_lock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    delRelatedSocket(downloader, socketInfo);

    code = pthread_mutex_unlock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        pthread_cond_signal(downloader->buffer->ownerCond);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    code = pthread_cond_signal(downloader->buffer->ownerCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't signal");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_FAILURE;
}

int endClientDirectTransfer(struct ThreadsStorage *storage, struct SocketInfo *downloader, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("client %d: end sending (direct transfer)\n", socketInfo->socket);
#endif

    int code = pthread_mutex_lock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    delRelatedSocket(downloader, socketInfo);

    code = pthread_mutex_unlock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        pthread_cond_signal(downloader->buffer->ownerCond);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    code = pthread_cond_signal(downloader->buffer->ownerCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't signal");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    delThreadFromStorage(storage, socketInfo->socket);

    return EXIT_SUCCESS;
}

int clientDirectTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: direct transfer\n", socketInfo->socket);
#endif

    struct SocketInfo *downloader = socketInfo->relatedSockets[DOWNLOADER_SOCKET];
    enum SocketStatus socketStatus;
    int downloaderBufferSize;
    do
    {
        int code = pthread_mutex_lock(downloader->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't lock mutex");
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        while(downloader->status == PREV_DIRECT_TRANSFER
              || (downloader->status == DIRECT_TRANSFER
                 && downloader->buffer->totalBytesCount - socketInfo->buffer->totalBytesCount <= 0
                 && isBufferEmpty(socketInfo->buffer)))
        {
            waitBufferClientDirection(downloader, socketInfo);
        }

        if(downloader->status == ERROR_END)
        {
#ifdef ENABLE_LOG
            printf("client %d: disconnect cuz downloader error (direct transfer)\n", socketInfo->socket);
#endif
            delRelatedSocket(downloader, socketInfo);
            pthread_mutex_unlock(downloader->buffer->mutex);
            pthread_cond_signal(downloader->buffer->ownerCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        socketStatus = downloader->status;
        downloaderBufferSize = downloader->buffer->totalBytesCount;

        int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;
        int canTake = min(freeSpace, downloaderBufferSize - socketInfo->buffer->totalBytesCount);

        char *from = downloader->buffer->data + downloader->buffer->currentSize -
                     (downloaderBufferSize - socketInfo->buffer->totalBytesCount);

        if (addCharsToBuffer(socketInfo->buffer, from, canTake) != EXIT_SUCCESS)
        {
            delRelatedSocket(downloader, socketInfo);
            pthread_mutex_unlock(downloader->buffer->mutex);
            pthread_cond_signal(downloader->buffer->ownerCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        code = pthread_mutex_unlock(downloader->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't unlock");
            delRelatedSocket(downloader, socketInfo);
            pthread_cond_signal(downloader->buffer->ownerCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        if(clientDirectTransferSend(storage, downloader, socketInfo) != EXIT_SUCCESS)
        {
            return EXIT_FAILURE;
        }

        pthread_cond_signal(downloader->buffer->ownerCond);
    }
    while(socketStatus == DIRECT_TRANSFER || downloaderBufferSize - socketInfo->buffer->totalBytesCount != 0
    || !isBufferEmpty(socketInfo->buffer));

    return endClientDirectTransfer(storage, downloader, socketInfo);
}

int safeGetCacheRecord(struct CacheRecord **pCacheRecord, struct CacheManager *cacheManager, const char *key)
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

int waitCacheReady(struct CacheRecord *cacheRecord, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("client %d: sleep (cache transfer)\n", socketInfo->socket);
#endif

    int code = pthread_cond_signal(cacheRecord->downloaderCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't signal");
        return EXIT_FAILURE;
    }

    code = pthread_cond_wait(cacheRecord->clientsCond, cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't wait");
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: resume (cache transfer)\n", socketInfo->socket);
#endif

    return EXIT_SUCCESS;
}

int createBigFileClientConnection(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                                  struct CacheManager *cacheManager, struct CacheRecord *cacheRecord)
{
#ifdef ENABLE_LOG
    printf("client %d: big file (cache transfer)\n", socketInfo->socket);
#endif

    int code = pthread_mutex_lock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    stopReadCacheRecord(cacheRecord, socketInfo);

    code = pthread_mutex_lock(cacheRecord->downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(cacheRecord->mutex);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    if(addRelatedSocket(cacheRecord->downloader, socketInfo) != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(cacheRecord->downloader->buffer->mutex);
        pthread_mutex_unlock(cacheRecord->mutex);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    if(addRelatedSocket(socketInfo, cacheRecord->downloader) != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(cacheRecord->downloader->buffer->mutex);
        pthread_mutex_unlock(cacheRecord->mutex);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    struct SocketInfo *downloader = socketInfo->relatedSockets[DOWNLOADER_SOCKET];

    code = pthread_mutex_unlock(cacheRecord->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        pthread_mutex_unlock(downloader->buffer->mutex);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    while(downloader->status == CACHE_TRANSFER)
    {
        code = pthread_cond_signal(cacheRecord->downloaderCond);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't signal");
            pthread_mutex_unlock(downloader->buffer->mutex);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        code = pthread_cond_wait(downloader->buffer->clientsCond, downloader->buffer->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't signal");
            pthread_mutex_unlock(downloader->buffer->mutex);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }

    code = pthread_mutex_unlock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    return clientDirectTransfer(storage, socketInfo, cacheManager);
}

int clientCacheTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: cache transfer\n", socketInfo->socket);
#endif

    struct CacheRecord *cacheRecord;
    if(safeGetCacheRecord(&cacheRecord, cacheManager, socketInfo->url) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    enum CacheStatus cacheStatus;
    int cacheSize;
    do
    {
        int code = pthread_mutex_lock(cacheRecord->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't lock mutex");
            stopReadCacheRecord(cacheRecord, socketInfo);
            pthread_cond_signal(cacheRecord->downloaderCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        while(cacheRecord->status == PARTIAL && cacheRecord->currentDataSize - socketInfo->buffer->totalBytesCount <= 0)
        {
            waitCacheReady(cacheRecord, socketInfo);
        }

        if(cacheRecord->status == ERROR_CACHE)
        {
#ifdef ENABLE_LOG
            printf("client %d: disconnect cuz cache error (cache transfer)\n", socketInfo->socket);
#endif
            stopReadCacheRecord(cacheRecord, socketInfo);
            pthread_mutex_unlock(cacheRecord->mutex);
            pthread_cond_signal(cacheRecord->downloaderCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        if(cacheRecord->status == BAD_ANSWER)
        {
#ifdef ENABLE_LOG
            printf("client %d: bad answer in cache. create direct connection (cache transfer)\n", socketInfo->socket);
#endif
            stopReadCacheRecord(cacheRecord, socketInfo);
            addRelatedSocket(cacheRecord->downloader, socketInfo);
            addRelatedSocket(socketInfo, cacheRecord->downloader);
            pthread_mutex_unlock(cacheRecord->mutex);
            pthread_cond_signal(cacheRecord->downloaderCond);
            return clientDirectTransfer(storage, socketInfo, cacheManager);
        }

        cacheStatus = cacheRecord->status;
        cacheSize = cacheRecord->currentDataSize;

        int freeSpace = socketInfo->buffer->allocatedSize - socketInfo->buffer->currentSize;
        int inCache = cacheSize - socketInfo->buffer->totalBytesCount;
        int canTake = min(freeSpace, inCache);

        if (addCharsToBuffer(socketInfo->buffer, cacheRecord->data + socketInfo->buffer->totalBytesCount, canTake) !=
            EXIT_SUCCESS)
        {
            stopReadCacheRecord(cacheRecord, socketInfo);
            pthread_mutex_unlock(cacheRecord->mutex);
            pthread_cond_signal(cacheRecord->downloaderCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        code = pthread_mutex_unlock(cacheRecord->mutex);
        if(code != EXIT_SUCCESS)
        {
            printError(code, "Can't unlock mutex");
            stopReadCacheRecord(cacheRecord, socketInfo);
            pthread_cond_signal(cacheRecord->downloaderCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        ssize_t size = 0;
        if (socketInfo->buffer->currentSize > 0 && (size = sendFromBuffer(socketInfo, socketInfo->buffer->currentSize)) <= 0)
        {
#ifdef ENABLE_LOG
            printf("client %d: go away (cache transfer)\n", socketInfo->socket);
#endif
            pthread_mutex_lock(cacheRecord->mutex);
            stopReadCacheRecord(cacheRecord, socketInfo);
            pthread_mutex_unlock(cacheRecord->mutex);

            pthread_cond_signal(cacheRecord->downloaderCond);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_SUCCESS;
        }

#ifdef ENABLE_LOG
        printf("client %d: send %zd bytes (cache transfer)\n", socketInfo->socket, size);
#endif
    }
    while(cacheStatus == PARTIAL || cacheSize - socketInfo->buffer->totalBytesCount != 0 || !isBufferEmpty(socketInfo->buffer));

#ifdef ENABLE_LOG
    printf("client %d: end sending (cache transfer)\n", socketInfo->socket);
#endif

    if (cacheStatus == FULL)
    {
#ifdef ENABLE_LOG
        printf("client %d: all data sent (cache transfer)\n", socketInfo->socket);
#endif
        pthread_mutex_lock(cacheRecord->mutex);
        stopReadCacheRecord(cacheRecord, socketInfo);
        pthread_mutex_unlock(cacheRecord->mutex);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_SUCCESS;
    }

    return createBigFileClientConnection(storage, socketInfo, cacheManager, cacheRecord);
}

int moveHeadersToServer(struct SocketInfo *socketInfo, struct SocketInfo *downloader)
{
#ifdef ENABLE_LOG
    printf("client %d: move headers to server. Raw: \"%.*s\"\n", socketInfo->socket, socketInfo->buffer->currentSize, socketInfo->buffer->data);
#endif
    int keepAlivePos = strpos(socketInfo->buffer->data, "Connection: keep-alive\r\n");
    if (keepAlivePos != NPOS)
    {
        int keepAliveLength = strlen("Connection: keep-alive\r\n");
        strerase(socketInfo->buffer->data, keepAlivePos, keepAliveLength);
        socketInfo->buffer->currentSize -= keepAliveLength;
    }

#ifdef ENABLE_LOG
    printf("client %d: move header: \"%.*s\"\n", socketInfo->socket, socketInfo->buffer->currentSize, socketInfo->buffer->data);
#endif

    int code = pthread_mutex_lock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_mutex_lock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(downloader->buffer->mutex);
        return EXIT_FAILURE;
    }

    if (moveCharsToBuffer(downloader->buffer, socketInfo->buffer, socketInfo->buffer->currentSize) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(socketInfo->buffer->mutex);
        pthread_mutex_unlock(downloader->buffer->mutex);
        return EXIT_FAILURE;
    }
    eraseBuffer(socketInfo->buffer);

    code = pthread_mutex_unlock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        pthread_mutex_unlock(downloader->buffer->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(downloader->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_cond_signal(downloader->buffer->ownerCond);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't signal");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int clientBufferTransfer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo)
{
#ifdef ENABLE_LOG
    printf("Buffer transfer\n");
#endif

    while(socketInfo->buffer->currentSize > 0)
    {
        if (sendFromBuffer(socketInfo, socketInfo->buffer->currentSize) <= 0)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }

    delThreadFromStorage(storage, socketInfo->socket);
    return EXIT_SUCCESS;
}

void sendError(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, const char *errorMsg)
{
    eraseBuffer(socketInfo->buffer);
    addCharsToBuffer(socketInfo->buffer, errorMsg, strlen(errorMsg));
    clientBufferTransfer(storage, socketInfo);
}

int createDownloaderSocket(int *downloader, struct SocketInfo *socketInfo, struct ThreadsStorage *storage)
{
#ifdef ENABLE_LOG
    printf("client %d: creating downloader socket\n", socketInfo->socket);
#endif
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
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    substr(socketInfo->url, domain, httpLength, domainLength);
    domain[domainLength] = '\0';
#ifdef ENABLE_LOG
    printf("client %d: start resolving domain: %s\n", socketInfo->socket, domain);
#endif

    struct addrinfo *connectionAddr = getAddrInfo(domain, "http");
    free(domain);
    if (connectionAddr == NULL)
    {
        sendError(storage, socketInfo, ERROR_404_STR);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: end resolving\n", socketInfo->socket);
#endif

    *downloader = socket(connectionAddr->ai_family, connectionAddr->ai_socktype, connectionAddr->ai_protocol);
    if (*downloader == ERROR)
    {
        perror("Can't create downloader socket");
        freeaddrinfo(connectionAddr);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: connecting...\n", socketInfo->socket);
#endif

    int code = connect(*downloader, connectionAddr->ai_addr, connectionAddr->ai_addrlen);
    freeaddrinfo(connectionAddr);
    if (code != EXIT_SUCCESS && errno != EINPROGRESS)
    {
        perror("Can't connect to server");
        close(*downloader);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int safeEraseBuffer(struct SocketInfo *socketInfo)
{
    int code = pthread_mutex_lock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    eraseBuffer(socketInfo->buffer);

    code = pthread_mutex_unlock(socketInfo->buffer->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int createDirectConnectionWithHeader(struct SocketInfo *downloader, struct SocketInfo *socketInfo)
{
    int code = pthread_mutex_lock(downloader->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    code = pthread_mutex_lock(socketInfo->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        pthread_mutex_unlock(downloader->mutex);
        return EXIT_FAILURE;
    }

    downloader->status = PREV_DIRECT_TRANSFER;

    if(addRelatedSocket(downloader, socketInfo) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(socketInfo->mutex);
        pthread_mutex_unlock(downloader->mutex);
        return EXIT_FAILURE;
    }

    if(addRelatedSocket(socketInfo, downloader) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(socketInfo->mutex);
        pthread_mutex_unlock(downloader->mutex);
        return EXIT_FAILURE;
    }

    if(moveHeadersToServer(socketInfo, downloader) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(socketInfo->mutex);
        pthread_mutex_unlock(downloader->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(socketInfo->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        pthread_mutex_unlock(downloader->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(downloader->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int connectToServer(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: create downloader and cache\n", socketInfo->socket);
#endif
    int downloaderSocket;
    if(createDownloaderSocket(&downloaderSocket, socketInfo, storage) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: search cache for %s\n", socketInfo->socket, socketInfo->url);
#endif

    int code = pthread_mutex_lock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if(cacheRecord != NULL)
    {
#ifdef ENABLE_LOG
        printf("client %d: there is cache\n", socketInfo->socket);
#endif
        close(downloaderSocket);

        if(safeAddCacheRecordReader(cacheRecord, socketInfo) != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        pthread_mutex_unlock(cacheManager->mutex);

        if(safeEraseBuffer(socketInfo) != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        return clientCacheTransfer(storage, socketInfo, cacheManager);
    }

#ifdef ENABLE_LOG
    printf("client %d: there isn't cache\n", socketInfo->socket);
#endif

    struct SocketInitData initDataDownloader;
    initDataDownloader.socket = downloaderSocket;
    initDataDownloader.handler = handlerServerSendFromSocket;
    initDataDownloader.url = socketInfo->url;

    struct SocketInfo *downloader;
    if (addThreadToStorage(&downloader, storage, cacheManager, &initDataDownloader) != EXIT_SUCCESS)
    {
#ifdef ENABLE_LOG
        printf("Can't add client %d to storage\n", downloaderSocket);
#endif
        close(downloaderSocket);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    if (createNewCacheRecord(&cacheRecord, cacheManager, socketInfo->url, downloader, socketInfo) != EXIT_SUCCESS)
    {
#ifdef ENABLE_LOG
        printf("client %d: can't create new cache\n", socketInfo->socket);
#endif

        pthread_mutex_unlock(cacheManager->mutex);

        if(createDirectConnectionWithHeader(downloader, socketInfo) != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, downloader->socket);
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }

        return clientDirectTransfer(storage, socketInfo, cacheManager);
    }

    downloader->status = PREV_CACHE_TRANSFER;
    if(moveHeadersToServer(socketInfo, downloader) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, downloader->socket);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    code = pthread_mutex_unlock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        delThreadFromStorage(storage, downloader->socket);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: new cache created\n", socketInfo->socket);
#endif

    return clientCacheTransfer(storage, socketInfo, cacheManager);
}

int checkHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: check header. First line: %.*s\n", socketInfo->socket, strpos(socketInfo->buffer->data, "\r\n"), socketInfo->buffer->data);
#endif

    if (strpos(socketInfo->buffer->data, "GET") != 0)
    {
#ifdef ENABLE_LOG
        printf("client %d: (check header) not GET\n", socketInfo->socket);
#endif
        sendError(storage, socketInfo, ERROR_501_STR);
        return EXIT_FAILURE;
    }
#ifdef ENABLE_LOG
        printf("client %d: (check header) method is GET\n", socketInfo->socket);
#endif

    int firstSpace = strpos(socketInfo->buffer->data, " ");
    int secondSpace = strpos(socketInfo->buffer->data + firstSpace + 1, " ") + firstSpace;

    int urlLength = secondSpace - firstSpace;
    char *url = (char *) calloc(urlLength + 1, sizeof(char));
    if (url == NULL)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    substr(socketInfo->buffer->data, url, firstSpace + 1, secondSpace - firstSpace);
    url[urlLength] = '\0';
    if (addUrlSocketInfo(socketInfo, url) != EXIT_SUCCESS)
    {
        free(url);
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }
    free(url);
#ifdef ENABLE_LOG
    printf("client %d: (check header) URL: %s\n", socketInfo->socket, socketInfo->url);
#endif

    if (strpos(socketInfo->url, "http://") != 0)
    {
#ifdef ENABLE_LOG
        printf("client %d: (check header) not http\n", socketInfo->socket);
#endif
        sendError(storage, socketInfo, ERROR_505_STR);
        return EXIT_FAILURE;
    }

    if (strpos(socketInfo->buffer->data + secondSpace + 2, "HTTP/1.0") != 0)
    {
#ifdef ENABLE_LOG
        printf("client %d: (check header) http isn't 1.0\n", socketInfo->socket);
#endif
        sendError(storage, socketInfo, ERROR_505_STR);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int processHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: process header\n", socketInfo->socket);
#endif

    if (checkHeader(storage, socketInfo, cacheManager) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

#ifdef ENABLE_LOG
    printf("client %d: good header, search cache record for url: %s\n", socketInfo->socket, socketInfo->url);
#endif

    int code = pthread_mutex_lock(cacheManager->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if(cacheRecord == NULL)
    {
#ifdef ENABLE_LOG
        printf("client %d: there isn't cache for: %s\n", socketInfo->socket, socketInfo->url);
#endif

        pthread_mutex_unlock(cacheManager->mutex);
        return connectToServer(storage, socketInfo, cacheManager);
    }

#ifdef ENABLE_LOG
    printf("client %d: there is cache for: %s\n", socketInfo->socket, socketInfo->url);
#endif

    if(safeAddCacheRecordReader(cacheRecord, socketInfo) != EXIT_SUCCESS)
    {
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    eraseBuffer(socketInfo->buffer);

    code = pthread_mutex_unlock(cacheManager->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        delThreadFromStorage(storage, socketInfo->socket);
        return EXIT_FAILURE;
    }

    return clientCacheTransfer(storage, socketInfo, cacheManager);
}

int clientWaitHeader(struct ThreadsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
#ifdef ENABLE_LOG
    printf("client %d: wait header\n", socketInfo->socket);
#endif

    while (strpos(socketInfo->buffer->data, "\r\n\r\n") == NPOS)
    {
        char buffer[BUFFER_SIZE];
        ssize_t size = recv(socketInfo->socket, buffer, BUFFER_SIZE, 0);
        if (size <= 0)
        {
#ifdef ENABLE_LOG
            printf("client %d: go away (wait header)\n", socketInfo->socket);
#endif
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_SUCCESS;
        }

#ifdef ENABLE_LOG
        printf("client %d: (wait header) get part of head: \"%.*s\"\n", socketInfo->socket, (int)size, buffer);
#endif

        if (addCharsToBuffer(socketInfo->buffer, buffer, size) != EXIT_SUCCESS)
        {
            delThreadFromStorage(storage, socketInfo->socket);
            return EXIT_FAILURE;
        }
    }

#ifdef ENABLE_LOG
    printf("client %d: end wait header (end on %d): \"%.*s\"\n", socketInfo->socket,
           strpos(socketInfo->buffer->data, "\r\n\r\n"), socketInfo->buffer->currentSize, socketInfo->buffer->data);
#endif

    return processHeader(storage, socketInfo, cacheManager);
}
