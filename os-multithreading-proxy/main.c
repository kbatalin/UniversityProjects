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
#include "ThreadsStorage.h"
#include "Buffer.h"
#include "utils.h"
#include "CacheManager.h"
#include "SocketInfo.h"
#include "ClientHandlers.h"
#include "ServerHandlers.h"

void destruct(struct ThreadsStorage *storage, struct CacheManager *cacheManager)
{
    destructThreadsStorage(storage);
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

int init(int *mainSocket, struct ThreadsStorage **socketsStorage, struct CacheManager **cacheManager)
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

    if (initThreadsStorage(socketsStorage) != EXIT_SUCCESS)
    {
        close(*mainSocket);
        return EXIT_FAILURE;
    }

    if (initCacheManager(cacheManager) != EXIT_SUCCESS)
    {
        destructThreadsStorage(*socketsStorage);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int main()
{
    printf("Started\n");

    int mainSocket;
    struct ThreadsStorage *threadsStorage;
    struct CacheManager *cacheManager;
    if (init(&mainSocket, &threadsStorage, &cacheManager) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    while (true)
    {
        struct sockaddr_storage storage;
        socklen_t len = sizeof(storage);
        int fd = accept(mainSocket, (struct sockaddr *) &storage, &len);
        if (fd == ERROR)
        {
            perror("Can't accept connection");
            continue;
        }

        struct SocketInitData initData;
        initData.socket = fd;
        initData.handler = clientWaitHeader;
        initData.url = NULL;

        if (addThreadToStorage(NULL, threadsStorage, cacheManager, &initData) != EXIT_SUCCESS)
        {
#ifdef ENABLE_LOG
            printf("Can't add client %d to storage\n", fd);
#endif
            close(fd);
            continue;
        }

#ifdef ENABLE_LOG
        printf("\nAdd client. fd %d\n", fd);
#endif
    }

    close(mainSocket);
    destruct(threadsStorage, cacheManager);
    return EXIT_SUCCESS;
}
