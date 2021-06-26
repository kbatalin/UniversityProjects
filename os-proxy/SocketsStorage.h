//
// Created by kir55rus on 29.11.16.
//

#ifndef _INC_29_POLLFDS_H_
#define _INC_29_POLLFDS_H_

#include <poll.h>

#define STORAGE_ARR_START_SIZE 10
#define STORAGE_ARR_EXTEND_STEP 5

struct SocketInfo;

struct SocketsStorage
{
    struct pollfd *fds;
    struct SocketInfo **socketsInfo;
    int currentSize;
    int allocatedSize;
};

int initSocketsStorage(struct SocketsStorage **socketsStorage);

void destructSocketsStorage(struct SocketsStorage *socketsStorage);

int addSocketToStorage(struct SocketInfo **socketInfo, struct SocketsStorage *storage, int fd, short int events);

void delSocketFromStorage(struct SocketsStorage *storage, int fd);

#endif //_INC_29_POLLFDS_H_
