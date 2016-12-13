#include "header.h"
#include <netdb.h>
#include <memory.h>
#include <stdio.h>
#include <stdlib.h>
#include <zconf.h>
#include <stdbool.h>
#include <sys/socket.h>
#include <netdb.h>
#include <sys/types.h>
#include <signal.h>
#include "SocketsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"
#include "ClientHandlers.h"
#include "ServerHandlers.h"

void destruct(struct SocketsStorage *storage, struct CacheManager *cacheManager)
{
    destructSocketsStorage(storage);
    destructCacheManager(cacheManager);
}

int initSocket(int *mainSocket)
{
    struct addrinfo *res = getAddrInfo(NULL, PROXY_PORT);
    if (res == NULL)
    {
        return EXIT_FAILURE;
    }

    *mainSocket = socket(res->ai_family, res->ai_socktype, res->ai_protocol);

    int enable = 1;
    int code = setsockopt(*mainSocket, SOL_SOCKET, SO_REUSEADDR, &enable, sizeof(enable));
    if (code != EXIT_SUCCESS)
    {
        perror("Can't set socket opt\n");
        freeaddrinfo(res);
        return EXIT_FAILURE;
    }

    if (*mainSocket == ERROR)
    {
        perror("Can't open socket");
        freeaddrinfo(res);
        return EXIT_FAILURE;
    }

    code = bind(*mainSocket, res->ai_addr, res->ai_addrlen);
    freeaddrinfo(res);
    if (code != EXIT_SUCCESS)
    {
        perror("Can't bind");
        close(*mainSocket);
        return EXIT_FAILURE;
    }

    code = listen(*mainSocket, QUEUE_LISTEN_SIZE);
    if (code != EXIT_SUCCESS)
    {
        perror("Can't listen");
        close(*mainSocket);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int init(int *mainSocket, struct SocketsStorage **socketsStorage, struct CacheManager **cacheManager)
{
    if (sigset(SIGPIPE, SIG_IGN) == ERROR)
    {
        perror("Can't set signal handler\n");
        return EXIT_FAILURE;
    }

    if (initSocket(mainSocket) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    if (initSocketsStorage(socketsStorage) != EXIT_SUCCESS)
    {
        close(*mainSocket);
        return EXIT_FAILURE;
    }

    if (addSocketToStorage(NULL, *socketsStorage, *mainSocket, POLLIN) != EXIT_SUCCESS)
    {
        close(*mainSocket);
        destructSocketsStorage(*socketsStorage);
        return EXIT_FAILURE;
    }

    if (initCacheManager(cacheManager) != EXIT_SUCCESS)
    {
        destructSocketsStorage(*socketsStorage);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void pollHandler(struct SocketsStorage *socketsStorage, int mainSocket, struct CacheManager *cacheManager, int event)
{
    for (int i = 0; i < socketsStorage->currentSize;)
    {
        if (!(socketsStorage->fds[i].revents & POLLIN) && !(socketsStorage->fds[i].revents & POLLOUT))
        {
            ++i;
            continue;
        }
        int currentFd = socketsStorage->socketsInfo[i]->socket;

        if (socketsStorage->fds[i].fd != mainSocket)
        {
            struct SocketInfo *socketInfo = socketsStorage->socketsInfo[i];
            socketInfo->handler(socketsStorage, socketInfo, cacheManager);

            i += (i >= socketsStorage->currentSize || currentFd == socketsStorage->socketsInfo[i]->socket) ? 1 : 0;
            continue;
        }
        ++i;

        //Main socket
        struct sockaddr_storage storage;
         socklen_t len = sizeof(storage);
        int fd = accept(mainSocket, (struct sockaddr *) &storage, &len);
        if (fd == ERROR)
        {
            perror("Can't accept connection");
            continue;
        }

        struct SocketInfo *socketInfo;
        if (addSocketToStorage(&socketInfo, socketsStorage, fd, POLLIN) != EXIT_SUCCESS)
        {
            close(fd);
            continue;
        }

        socketInfo->handler = clientHandler;

#ifdef ENABLE_LOG
        printf("\nAdd client. fd %d\n", fd);
#endif
    }
}

int main()
{
    printf("Started\n");

    int mainSocket;
    struct SocketsStorage *socketsStorage;
    struct CacheManager *cacheManager;
    if (init(&mainSocket, &socketsStorage, &cacheManager) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    int event;
    while ((event = poll(socketsStorage->fds, socketsStorage->currentSize, POLL_TIMEOUT_MS)) != ERROR)
    {
        pollHandler(socketsStorage, mainSocket, cacheManager, event);
    }

    if (event == ERROR)
    {
        perror("Poll error");
        destruct(socketsStorage, cacheManager);
        return EXIT_FAILURE;
    }

    destruct(socketsStorage, cacheManager);
    return EXIT_SUCCESS;
}
