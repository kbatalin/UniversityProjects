//
// Created by kir55rus on 29.11.16.
//

#include <stdbool.h>

#ifndef _INC_29_BUFFER_H_
#define _INC_29_BUFFER_H_

struct Buffer
{
    char *data;
    int allocatedSize;
    int currentSize;

    int totalBytesCount;
};

int initBuffer(struct Buffer **buffer);

void destructBuffer(struct Buffer *buffer);

int addCharsToBuffer(struct Buffer *buffer, const char *data, int size);

void popCharsFromBuffer(struct Buffer *buffer, int size);

int moveCharsToBuffer(struct Buffer *dest, struct Buffer *src, int count);

bool isBufferEmpty(const struct Buffer *buffer);

void eraseBuffer(struct Buffer *buffer);


#endif //_INC_29_BUFFER_H_
