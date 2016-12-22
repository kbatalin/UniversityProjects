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
#include <pthread.h>
#include <string.h>
#include "utils.h"
#include "header.h"
#include "Buffer.h"
#include "SocketInfo.h"
#include "CacheManager.h"
#include "ThreadsStorage.h"

void printError(int code, const char * str)
{
    printf("%s: %s\n", str, strerror(code));
}

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
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(name, service, &hints, &res);
    if (code != EXIT_SUCCESS)
    {
        fprintf(stderr, "Can't get addr info. Error: %s\n", gai_strerror(code));
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

ssize_t sendFromBuffer(struct SocketInfo *socketInfo, int size)
{
    ssize_t sentBytesCount = 0;

    if (socketInfo->buffer->currentSize <= 0)
    {
        return sentBytesCount;
    }

    sentBytesCount = send(socketInfo->socket, socketInfo->buffer->data, size, MSG_NOSIGNAL);
    if (sentBytesCount > 0)
    {
        popCharsFromBuffer(socketInfo->buffer, sentBytesCount);
    }

    return sentBytesCount;
}


