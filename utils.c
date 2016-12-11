//
// Created by kir55rus on 29.11.16.
//

//#include <lzma.h>
#include <memory.h>
#include <netdb.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include <string.h>
#include "utils.h"
#include "header.h"
#include "Buffer.h"
#include "SocketInfo.h"
#include "CacheManager.h"
#include "SocketsStorage.h"

void substr(const char *src, char *dest, int pos, int len)
{
    memcpy(dest, src + pos, len);
}

int strpos(const char *haystack, const char *needle)
{
    if (haystack == NULL || needle == NULL)
    {
        return NPOS;
    }

    char *ptr = strstr(haystack, needle);
    if (NULL == ptr)
    {
        return NPOS;
    }

    return ptr - haystack;
}

void strerase(char *str, int pos, int length)
{
    int strLength = strlen(str);

    for (int i = pos, countLeft = strLength - length; str[i] != '\0' && countLeft > 0; ++i, --countLeft)
    {
        str[i] = (i + length < strLength) ? str[i + length] : (char) '\0';
    }
}

struct addrinfo *getAddrInfo(const char *name, const char *service)
{
    struct addrinfo hints, *res;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(name, service, &hints, &res);
    if (code != EXIT_SUCCESS)
    {
        perror("Can't get addr info");
        return NULL;
    }

    return res;
}

int min(int a, int b)
{
    return a < b ? a : b;
}

int max(int a, int b)
{
    return a < b ? b : a;
}

void sendError(struct SocketsStorage *storage, struct SocketInfo *socketInfo, int errorCode)
{
    static const char *errorsStr[ERROR_COUNT];
    errorsStr[ERROR_404] = ERROR_404_STR;
    errorsStr[ERROR_501] = ERROR_501_STR;
    errorsStr[ERROR_505] = ERROR_505_STR;

    eraseBuffer(socketInfo->buffer);
    addCharsToBuffer(socketInfo->buffer, errorsStr[errorCode], strlen(errorsStr[errorCode]));
    resumePollSocket(socketInfo);
    socketInfo->pollfd->events = POLLOUT;
    socketInfo->status = SEND_FROM_SOCKET;
}

ssize_t sendFromBuffer(struct SocketInfo *socketInfo, int size)
{
    ssize_t sentBytesCount = 0;

    if (socketInfo->buffer->currentSize <= 0)
    {
        return sentBytesCount;
    }

    sentBytesCount = send(socketInfo->socket, socketInfo->buffer->data, size, 0);
    if (sentBytesCount > 0)
    {
        popCharsFromBuffer(socketInfo->buffer, size);
    }

    return sentBytesCount;
}

void delRelationWithDownloader(struct SocketsStorage *storage, struct SocketInfo *socketInfo)
{
    resumePollSocket(socketInfo->relatedSockets[DOWNLOADER_SOCKET]);
    delRelatedSocket(socketInfo->relatedSockets[DOWNLOADER_SOCKET], socketInfo);

    if (socketInfo->relatedSockets[DOWNLOADER_SOCKET]->countRelatedSockets == 0)
    {
        delSocketFromStorage(storage, socketInfo->relatedSockets[DOWNLOADER_SOCKET]->socket);
    }
}

void disconnectClient(struct SocketsStorage *storage, struct CacheManager *cacheManager, struct SocketInfo *socketInfo)
{
    printf("Disconnect client %d (url: %s)\n", socketInfo->socket, socketInfo->url == NULL ? "" : socketInfo->url);
    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);

    if (cacheRecord == NULL && socketInfo->countRelatedSockets > 0)
    {
        delRelationWithDownloader(storage, socketInfo);

        delSocketFromStorage(storage, socketInfo->socket);
        return;
    }
    else if (cacheRecord == NULL)
    {
        delSocketFromStorage(storage, socketInfo->socket);
        return;
    }

    resumePollSocket(cacheRecord->downloader);

    stopReadCacheRecord(cacheManager, socketInfo);

#ifndef ENABLE_RESUMING
    if (cacheRecord->clientsCount == 0 && cacheRecord->status != FULL)
    {
        delCacheRecord(cacheManager, socketInfo->url);
    }
#endif

    delSocketFromStorage(storage, socketInfo->socket);
}

void delRelatedClients(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager)
{
    int relatedCount = socketInfo->countRelatedSockets;
    for (int i = 0; i < relatedCount; ++i)
    {
        resumePollSocket(*socketInfo->relatedSockets);
        disconnectClient(storage, cacheManager, socketInfo->relatedSockets[0]);
    }
}

void disconnectServer(struct SocketsStorage *storage, struct CacheManager *cacheManager, struct SocketInfo *socketInfo,
                      bool isError)
{
    printf("Disconnect server %d (url: %s)\n", socketInfo->socket, socketInfo->url == NULL ? "" : socketInfo->url);
    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);

    if (cacheRecord == NULL && socketInfo->countRelatedSockets > 0)
    {
        delRelatedClients(storage, socketInfo, cacheManager);
        return;
    }
    else if (cacheRecord == NULL)
    {
        delSocketFromStorage(storage, socketInfo->socket);
        return;
    }

    for (int i = 0; i < cacheRecord->clientsCount; ++i)
    {
        resumePollSocket(cacheRecord->clients[i]);
    }

    if (isError)
    {
        printf("Delete cache record\n");
        delCacheRecord(cacheManager, socketInfo->url);
    }
    else
    {
        cacheRecord->downloader = NULL;
    }

    delSocketFromStorage(storage, socketInfo->socket);
}



