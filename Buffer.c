//
// Created by kir55rus on 29.11.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include "Buffer.h"
#include "utils.h"

int initBuffer(struct Buffer **buffer)
{
    *buffer = (struct Buffer*) calloc(1, sizeof(struct Buffer));
    if(*buffer == NULL)
    {
        fprintf(stderr, "Can't allocate memory for Buffer\n");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructBuffer(struct Buffer *buffer)
{
    if (buffer == NULL)
    {
        return;
    }
    free(buffer->data);
    free(buffer);
}

int extendBuffer(struct Buffer * buffer, int newSize)
{
    char * newArr = (char *) realloc(buffer->data, (newSize + 1) * sizeof(char));
    if (newArr == NULL)
    {
        fprintf(stderr, "Can't extend buffer\n");
        return EXIT_FAILURE;
    }

    buffer->data = newArr;
    buffer->allocatedSize = newSize;
    buffer->data[newSize] = '\0';

    return EXIT_SUCCESS;
}

int addCharsToBuffer(struct Buffer *buffer, const char *data, int size)
{
    int newSize = buffer->currentSize + size;
    if(buffer->allocatedSize <= newSize && extendBuffer(buffer, newSize) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    buffer->totalBytesCount += size;

    for(int i = 0; i < size; ++i, ++buffer->currentSize)
    {
        buffer->data[buffer->currentSize] = data[i];
    }

    return EXIT_SUCCESS;
}

void popCharsFromBuffer(struct Buffer * buffer, int size)
{
    size = min(size, buffer->currentSize);

    for(int i = 0; i < buffer->currentSize - size; ++i)
    {
        int pos = size + i;
        buffer->data[i] = pos < buffer->currentSize ? buffer->data[pos] : (char)'\0';
    }

    buffer->currentSize -= size;
}

int moveCharsToBuffer(struct Buffer *dest, struct Buffer *src, int count)
{
    count = min(count, src->currentSize);
    if(addCharsToBuffer(dest, src->data, count) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    src->currentSize -= count;

    return EXIT_SUCCESS;
}

bool isBufferEmpty(const struct Buffer *buffer)
{
    return buffer->currentSize == 0;
}

void eraseBuffer(struct Buffer *buffer)
{
    buffer->currentSize = 0;
    buffer->totalBytesCount = 0;
}
