//
// Created by kir55rus on 29.11.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include <pthread.h>
#include "ThreadsStorage.h"
#include "ThreadData.h"
#include "SocketInfo.h"
#include "utils.h"
#include "ThreadData.h"

int initThreadsStorage(struct ThreadsStorage **threadsStorage)
{
    *threadsStorage = (struct ThreadsStorage *) calloc(1, sizeof(struct ThreadsStorage));
    if (*threadsStorage == NULL)
    {
        fprintf(stderr, "Can't allocate memory for threads storage\n");
        return EXIT_FAILURE;
    }

    (*threadsStorage)->mutex = (pthread_mutex_t *) calloc(1, sizeof(pthread_mutex_t));
    if((*threadsStorage)->mutex == NULL)
    {
        fprintf(stderr, "Can't allocate memory for mutex");
        free((*threadsStorage));
        return EXIT_FAILURE;

    }

    int code = pthread_mutex_init((*threadsStorage)->mutex, NULL);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't init mutex");
        free((*threadsStorage)->mutex);
        free(threadsStorage);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructThreadsStorage(struct ThreadsStorage *threadsStorage)
{
    int code = pthread_mutex_lock(threadsStorage->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return;
    }

    pthread_mutex_t * mutex = threadsStorage->mutex;

    for(int i = 0; i < threadsStorage->currentSize; ++i)
    {
        pthread_cancel(threadsStorage->threads[i]);
        struct ThreadData * threadData = threadsStorage->threadsData[i];
        destructSocketInfo(threadData->socketInfo);
        free(threadsStorage->threadsData[i]);
    }

    threadsStorage->mutex = NULL;
    free(threadsStorage);

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

int extendThreadsStorage(struct ThreadsStorage *storage)
{
    int newSize = storage->allocatedSize + STORAGE_ARR_EXTEND_STEP;
    pthread_t *newThreadsArr = (pthread_t *) realloc(storage->threads, newSize * sizeof(pthread_t));
    if (newThreadsArr == NULL)
    {
        fprintf(stderr, "Can't extend ThreadsStorage #1\n");
        return EXIT_FAILURE;
    }
    storage->threads = newThreadsArr;

    struct ThreadData ** newDataArr = (struct ThreadData **) realloc(storage->threadsData, newSize * sizeof(struct ThreadData *));
    if(newDataArr == NULL)
    {
        fprintf(stderr, "Can't extend ThreadsStorage #2\n");
        return EXIT_FAILURE;
    }
    storage->threadsData = newDataArr;

    storage->allocatedSize = newSize;

    return EXIT_SUCCESS;
}

void * threadStart(void * args)
{
    struct ThreadData * threadData = (struct ThreadData *) args;
    threadData->socketInfo->handler(threadData->threadsStorage, threadData->socketInfo, threadData->cacheManager);
    return EXIT_SUCCESS;
}

int addThreadToStorage(struct SocketInfo **socketInfo, struct ThreadsStorage *storage, struct CacheManager *cacheManager,
                       const struct SocketInitData *initData)
{
#ifdef ENABLE_LOG
    printf("socket %d: adding in storage. Size: %d, allocated: %d\n", initData->socket, storage->currentSize, storage->allocatedSize);
#endif
    int code = pthread_mutex_lock(storage->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return EXIT_FAILURE;
    }

    if (storage->currentSize >= storage->allocatedSize && extendThreadsStorage(storage) != EXIT_SUCCESS)
    {
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    storage->threadsData[storage->currentSize] = (struct ThreadData *) calloc(1, sizeof(struct ThreadData));
    if(storage->threadsData[storage->currentSize] == NULL)
    {
        pthread_mutex_unlock(storage->mutex);
        fprintf(stderr, "Can' allocate memory for threadData\n");
        return EXIT_FAILURE;
    }

    if (initSocketInfo(&storage->threadsData[storage->currentSize]->socketInfo) != EXIT_SUCCESS)
    {
        free(storage->threadsData[storage->currentSize]);
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    if(addUrlSocketInfo(storage->threadsData[storage->currentSize]->socketInfo, initData->url) != EXIT_SUCCESS)
    {
        destructSocketInfo(storage->threadsData[storage->currentSize]->socketInfo);
        free(storage->threadsData[storage->currentSize]);
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    storage->threadsData[storage->currentSize]->socketInfo->socket = initData->socket;
    storage->threadsData[storage->currentSize]->socketInfo->handler = initData->handler;

    storage->threadsData[storage->currentSize]->cacheManager = cacheManager;
    storage->threadsData[storage->currentSize]->threadsStorage = storage;

    pthread_attr_t attr;
    code = pthread_attr_init(&attr);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't init attr");
        destructSocketInfo(storage->threadsData[storage->currentSize]->socketInfo);
        free(storage->threadsData[storage->currentSize]);
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't init attr");
        pthread_attr_destroy(&attr);
        destructSocketInfo(storage->threadsData[storage->currentSize]->socketInfo);
        free(storage->threadsData[storage->currentSize]);
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_create(&storage->threads[storage->currentSize], &attr, threadStart, storage->threadsData[storage->currentSize]);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't create thread");
        pthread_attr_destroy(&attr);
        destructSocketInfo(storage->threadsData[storage->currentSize]->socketInfo);
        free(storage->threadsData[storage->currentSize]);
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    code = pthread_attr_destroy(&attr);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't destroy attr");
        pthread_mutex_unlock(storage->mutex);
        return EXIT_FAILURE;
    }

    if(socketInfo != NULL)
    {
        *socketInfo = storage->threadsData[storage->currentSize]->socketInfo;
    }

    ++storage->currentSize;

    code = pthread_mutex_unlock(storage->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;
}

void delThreadFromStorage(struct ThreadsStorage *storage, int fd)
{
#ifdef ENABLE_LOG
    printf("try del socket %d from storage\n", fd);
#endif
    int code = pthread_mutex_lock(storage->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't lock mutex");
        return;
    }

#ifdef ENABLE_LOG
    printf("del socket %d from storage\n", fd);
#endif
    for (int i = 0; i < storage->currentSize; ++i)
    {
        if (storage->threadsData[i] == NULL || storage->threadsData[i]->socketInfo == NULL
            || storage->threadsData[i]->socketInfo->socket != fd)
        {
            continue;
        }

        destructSocketInfo(storage->threadsData[i]->socketInfo);
        free(storage->threadsData[i]);

        storage->threads[i] = storage->threads[storage->currentSize - 1];
        storage->threadsData[i] = storage->threadsData[storage->currentSize - 1];
        storage->threadsData[storage->currentSize - 1] = NULL;

        --storage->currentSize;

        break;
    }

    code = pthread_mutex_unlock(storage->mutex);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can't unlock mutex");
    }
}

