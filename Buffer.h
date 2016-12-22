//
// Created by kir55rus on 29.11.16.
//

#ifndef _INC_29_BUFFER_H_
#define _INC_29_BUFFER_H_

#include <stdbool.h>
#include <pthread.h>

struct Buffer
{
    char *data;
    int allocatedSize;
    int currentSize;

    int totalBytesCount;

    pthread_mutex_t *mutex;
    pthread_cond_t *ownerCond;
    pthread_cond_t *clientsCond;
};

int initBuffer(struct Buffer **buffer);

void destructBuffer(struct Buffer *buffer);

int addCharsToBuffer(struct Buffer *buffer, const char *data, int size);

int popCharsFromBuffer(struct Buffer *buffer, int size);

int moveCharsToBuffer(struct Buffer *dest, struct Buffer *src, int count);

bool isBufferEmpty(const struct Buffer *buffer);

int eraseBuffer(struct Buffer *buffer);


#endif //_INC_29_BUFFER_H_
