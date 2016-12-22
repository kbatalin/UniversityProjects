//
// Created by kir55rus on 29.11.16.
//

#ifndef _INC_29_POLLFDS_H_
#define _INC_29_POLLFDS_H_

#include "header.h"

#define STORAGE_ARR_EXTEND_STEP 5

struct ThreadData;

struct ThreadsStorage
{
    pthread_t * threads;
    struct ThreadData ** threadsData;
    int currentSize;
    int allocatedSize;

    pthread_mutex_t * mutex;
};

struct SocketInitData
{
    int socket;
    handlerType handler;
    const char *url;
};

int initThreadsStorage(struct ThreadsStorage **threadsStorage);

void destructThreadsStorage(struct ThreadsStorage *threadsStorage);

int addThreadToStorage(struct SocketInfo **socketInfo, struct ThreadsStorage *storage, struct CacheManager *cacheManager,
                       const struct SocketInitData *initData);

void delThreadFromStorage(struct ThreadsStorage *storage, int fd);

#endif //_INC_29_POLLFDS_H_
