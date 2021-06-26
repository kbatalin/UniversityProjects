//
// Created by kir55rus on 29.11.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include "SocketsStorage.h"
#include "SocketInfo.h"

int initSocketsStorage(struct SocketsStorage **socketsStorage)
{
    *socketsStorage = (struct SocketsStorage *) calloc(1, sizeof(struct SocketsStorage));
    if (*socketsStorage == NULL)
    {
        fprintf(stderr, "Can't allocate memory for socketsStorage\n");
        return EXIT_FAILURE;
    }

    (*socketsStorage)->fds = (struct pollfd *) calloc(STORAGE_ARR_START_SIZE, sizeof(struct pollfd));
    if ((*socketsStorage)->fds == NULL)
    {
        fprintf(stderr, "Can't allocate memory for SocketsStorage::fds\n");
        free(*socketsStorage);
        return EXIT_FAILURE;
    }

    (*socketsStorage)->socketsInfo = (struct SocketInfo **) calloc(STORAGE_ARR_START_SIZE, sizeof(struct SocketInfo));
    if ((*socketsStorage)->socketsInfo == NULL)
    {
        fprintf(stderr, "Can't allocate memory for SocketsStorage::socketsInfo\n");
        free((*socketsStorage)->fds);
        free(*socketsStorage);
        return EXIT_FAILURE;
    }

    (*socketsStorage)->allocatedSize = STORAGE_ARR_START_SIZE;
    (*socketsStorage)->currentSize = 0;

    return EXIT_SUCCESS;
}

void destructSocketsStorage(struct SocketsStorage *socketsStorage)
{
    struct SocketInfo **ptr = socketsStorage->socketsInfo;
    while (*ptr)
    {
        destructSocketInfo(*ptr);
        *ptr = NULL;
        ++ptr;
    }

    free(socketsStorage->socketsInfo);
    free(socketsStorage->fds);
    free(socketsStorage);
}

int extendSocketsStorage(struct SocketsStorage *storage)
{
    int newSize = storage->allocatedSize + STORAGE_ARR_EXTEND_STEP;
    struct pollfd *newArr = (struct pollfd *) realloc(storage->fds, newSize * sizeof(struct pollfd));
    if (newArr == NULL)
    {
        fprintf(stderr, "Can't extend SocketsStorage #1");
        return EXIT_FAILURE;
    }
    storage->fds = newArr;

    for (int i = 0; i < storage->currentSize; ++i)
    {
        storage->socketsInfo[i]->pollfd = &storage->fds[i];
    }

    struct SocketInfo **newSocketsInfo = (struct SocketInfo **) realloc(storage->socketsInfo,
                                                                        newSize * sizeof(struct SocketInfo *));
    if (newSocketsInfo == NULL)
    {
        fprintf(stderr, "Can't extend SocketsStorage #2");
        return EXIT_FAILURE;
    }
    storage->socketsInfo = newSocketsInfo;

    storage->allocatedSize = newSize;

    return EXIT_SUCCESS;
}

int addSocketToStorage(struct SocketInfo **socketInfo, struct SocketsStorage *storage, int fd, short int events)
{
    if (storage->currentSize >= storage->allocatedSize && extendSocketsStorage(storage) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    struct SocketInfo *socket;
    if (initSocketInfo(&socket) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    socket->socket = fd;
    socket->pollfd = &storage->fds[storage->currentSize];

    storage->fds[storage->currentSize].fd = fd;
    storage->fds[storage->currentSize].events = events;
    storage->fds[storage->currentSize].revents = 0;

    storage->socketsInfo[storage->currentSize] = socket;
    ++storage->currentSize;

    if (socketInfo != NULL)
    {
        *socketInfo = socket;
    }

    return EXIT_SUCCESS;
}

void delSocketFromStorage(struct SocketsStorage *storage, int fd)
{
    for (int i = 0; i < storage->currentSize; ++i)
    {
        if (storage->socketsInfo[i] == NULL || storage->socketsInfo[i]->socket != fd)
        {
            continue;
        }

        destructSocketInfo(storage->socketsInfo[i]);

        storage->fds[i].fd = -1;
        storage->socketsInfo[i] = NULL;

        storage->fds[i] = storage->fds[storage->currentSize - 1];
        storage->socketsInfo[i] = storage->socketsInfo[storage->currentSize - 1];
        storage->socketsInfo[storage->currentSize - 1] = NULL;
        if (storage->socketsInfo[i] != NULL)
        {
            storage->socketsInfo[i]->pollfd = &(storage->fds[i]);
        }

        --storage->currentSize;

        break;
    }

}

