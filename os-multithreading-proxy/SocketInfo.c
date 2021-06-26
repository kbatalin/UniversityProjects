//
// Created by kir55rus on 02.12.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
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

    (*socketInfo)->status = INIT;

    if (initBuffer(&(*socketInfo)->buffer) != EXIT_SUCCESS)
    {
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    (*socketInfo)->mutex = (pthread_mutex_t *) calloc(1, sizeof(pthread_mutex_t));
    if((*socketInfo)->mutex == NULL)
    {
        fprintf(stderr, "Can't allocate memory for rwlock\n");
        destructBuffer((*socketInfo)->buffer);
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    int code = pthread_mutex_init((*socketInfo)->mutex, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        destructBuffer((*socketInfo)->buffer);
        free((*socketInfo)->mutex);
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    (*socketInfo)->cond = (pthread_cond_t *) calloc(1, sizeof(pthread_cond_t));
    if((*socketInfo)->cond == NULL)
    {
        fprintf(stderr, "Can't allocate memory for clientsCond\n");
        destructBuffer((*socketInfo)->buffer);
        pthread_mutex_destroy((*socketInfo)->mutex);
        free((*socketInfo)->mutex);
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    code = pthread_cond_init((*socketInfo)->cond, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        destructBuffer((*socketInfo)->buffer);
        pthread_mutex_destroy((*socketInfo)->mutex);
        free((*socketInfo)->mutex);
        free((*socketInfo)->cond);
        free(*socketInfo);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructSocketInfo(struct SocketInfo *socketInfo)
{
    int code = pthread_mutex_lock(socketInfo->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't mutex");
        return;
    }

    pthread_mutex_t *mutex = socketInfo->mutex;
    socketInfo->mutex = NULL;

#ifdef ENABLE_LOG
    printf("Destruct socket %d\n", socketInfo->socket);
#endif

    pthread_cond_destroy(socketInfo->cond);
    free(socketInfo->cond);
    close(socketInfo->socket);
    free(socketInfo->url);
    free(socketInfo->relatedSockets);
    destructBuffer(socketInfo->buffer);
    free(socketInfo);

    code = pthread_mutex_unlock(mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        free(mutex);
        return;
    }

    code = pthread_mutex_destroy(mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't destroy mutex");
        free(mutex);
        return;
    }

    free(mutex);
}

int addUrlSocketInfo(struct SocketInfo *socketInfo, const char *url)
{
    if(url == NULL || url == socketInfo->url)
    {
        return EXIT_SUCCESS;
    }

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
#ifdef ENABLE_LOG
    printf("add relation with %d and %d\n", mainSocket->socket, relatedSocket->socket);
#endif
    if (mainSocket->relatedSocketsArraySize <= mainSocket->countRelatedSockets &&
        extendRelatedSocketsArray(mainSocket) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    mainSocket->relatedSockets[mainSocket->countRelatedSockets++] = relatedSocket;

    return EXIT_SUCCESS;
}

int delRelatedSocket(struct SocketInfo *mainSocket, struct SocketInfo *relatedSocket)
{
#ifdef ENABLE_LOG
    printf("del relation with %d and %d\n", mainSocket->socket, relatedSocket->socket);
#endif
    for (int i = 0; i < mainSocket->countRelatedSockets; ++i)
    {
        if (mainSocket->relatedSockets[i]->socket == relatedSocket->socket)
        {
            mainSocket->relatedSockets[i] = mainSocket->relatedSockets[mainSocket->countRelatedSockets - 1];
            --mainSocket->countRelatedSockets;
            break;
        }
    }

    return EXIT_SUCCESS;
}
