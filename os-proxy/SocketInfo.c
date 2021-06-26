//
// Created by kir55rus on 02.12.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include <zconf.h>
#include <sys/socket.h>
#include <netdb.h>
#include <memory.h>
#include <string.h>
#include "SocketInfo.h"
#include "Buffer.h"
#include "utils.h"
#include "header.h"

int initSocketInfo(struct SocketInfo **socketInfo)
{
    *socketInfo = (struct SocketInfo *) calloc(1, sizeof(struct SocketInfo));
    if (*socketInfo == NULL)
    {
        fprintf(stderr, "Can't allocate memory for socket info\n");
        return EXIT_FAILURE;
    }

    if (initBuffer(&(*socketInfo)->buffer) != EXIT_SUCCESS)
    {
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    (*socketInfo)->status = WAIT_HEADER;

    return EXIT_SUCCESS;
}

void destructSocketInfo(struct SocketInfo *socketInfo)
{
    if (socketInfo == NULL)
    {
        return;
    }

#ifdef ENABLE_LOG
    printf("Destruct socket %d\n", socketInfo->socket);
#endif

    close(socketInfo->socket);
    free(socketInfo->url);
    free(socketInfo->relatedSockets);
    destructBuffer(socketInfo->buffer);
    free(socketInfo);
}

int addUrlSocketInfo(struct SocketInfo *socketInfo, const char *url)
{
    socketInfo->url = (char *) calloc(strlen(url) + 1, sizeof(char));
    if (socketInfo->url == NULL)
    {
        fprintf(stderr, "Can't allocate memory for url\n");
        return EXIT_FAILURE;
    }

    strcpy(socketInfo->url, url);
    return EXIT_SUCCESS;
}

int extendRelatedSocketsArray(struct SocketInfo *socketInfo)
{
    int newSize = socketInfo->relatedSocketsArraySize + RELATED_ARR_EXTEND_STEP;
    struct SocketInfo **newArr = (struct SocketInfo **) realloc(socketInfo->relatedSockets,
                                                                newSize * sizeof(struct SocketInfo *));
    if (newArr == NULL)
    {
        fprintf(stderr, "Can't allocate memory for related sockets array\n");
        return EXIT_FAILURE;
    }

    socketInfo->relatedSocketsArraySize = newSize;
    socketInfo->relatedSockets = newArr;

    return EXIT_SUCCESS;
}

int addRelatedSocket(struct SocketInfo *mainSocket, struct SocketInfo *relatedSocket)
{
    if (mainSocket->relatedSocketsArraySize <= mainSocket->countRelatedSockets &&
        extendRelatedSocketsArray(mainSocket) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    mainSocket->relatedSockets[mainSocket->countRelatedSockets++] = relatedSocket;
    return EXIT_SUCCESS;
}

void delRelatedSocket(struct SocketInfo *mainSocket, struct SocketInfo *relatedSocket)
{
    for (int i = 0; i < mainSocket->countRelatedSockets; ++i)
    {
        if (mainSocket->relatedSockets[i]->socket == relatedSocket->socket)
        {
            mainSocket->relatedSockets[i] = mainSocket->relatedSockets[mainSocket->countRelatedSockets - 1];
            --mainSocket->countRelatedSockets;
            break;
        }
    }
}

void pausePollSocket(struct SocketInfo *socketInfo)
{
    if (socketInfo == NULL)
    {
        return;
    }
#ifdef ENABLE_LOG
    printf("Pause %d\n", socketInfo->socket);
#endif
    socketInfo->pollfd->fd = -1;
    socketInfo->pollfd->revents = 0;
}

void resumePollSocket(struct SocketInfo *socketInfo)
{
    if (socketInfo == NULL)
    {
        return;
    }
#ifdef ENABLE_LOG
    printf("Resume %d\n", socketInfo->socket);
#endif
    socketInfo->pollfd->fd = socketInfo->socket;
//    socketInfo->pollfd->revents = 0;
}
