//
// Created by kir55rus on 02.12.16.
//

#ifndef _INC_29_SOCKETINFO_H_
#define _INC_29_SOCKETINFO_H_

#include <poll.h>
#include <stdbool.h>
#include "header.h"

#define RELATED_ARR_EXTEND_STEP 5
#define DOWNLOADER_SOCKET 0

struct Buffer;
struct CacheManager;

enum SocketStatus
{
    WAIT_HEADER = 0,
    SEND_FROM_SOCKET,
    RECEIVE_TO_SOCKET,
    END_WORKING,
    STATUS_COUNT,
};

struct SocketInfo
{
    int socket;
    enum SocketStatus status;
    char *url;
    struct pollfd *pollfd;
    struct Buffer *buffer;

    struct SocketInfo **relatedSockets;
    int countRelatedSockets;
    int relatedSocketsArraySize;

    handlerType handler;
};

int initSocketInfo(struct SocketInfo **socketInfo);

void destructSocketInfo(struct SocketInfo *socketInfo);

int addUrlSocketInfo(struct SocketInfo *socketInfo, const char *url);

int addRelatedSocket(struct SocketInfo *mainSocket, struct SocketInfo *relatedSocket);

void delRelatedSocket(struct SocketInfo *mainSocket, struct SocketInfo *relatedSocket);

void pausePollSocket(struct SocketInfo *socketInfo);

void resumePollSocket(struct SocketInfo *socketInfo);

#endif //_INC_29_SOCKETINFO_H_
