//
// Created by kir55rus on 29.11.16.
//

#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
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

    (*buffer)->mutex = (pthread_mutex_t *) calloc(1, sizeof(pthread_mutex_t));
    if((*buffer)->mutex == NULL)
    {
        fprintf(stderr, "Can't allocate memory for mutex\n");
        free(*buffer);
        return EXIT_FAILURE;
    }

    int code = pthread_mutex_init((*buffer)->mutex, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        free((*buffer)->mutex);
        free(*buffer);
        return EXIT_FAILURE;
    }

    (*buffer)->ownerCond = (pthread_cond_t *) calloc(1, sizeof(pthread_cond_t));
    if((*buffer)->ownerCond == NULL)
    {
        fprintf(stderr, "Can't allocate memory for clientsCond\n");
        pthread_mutex_destroy((*buffer)->mutex);
        free((*buffer)->mutex);
        free(*buffer);
        return EXIT_FAILURE;
    }

    code = pthread_cond_init((*buffer)->ownerCond, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        pthread_mutex_destroy((*buffer)->mutex);
        free((*buffer)->mutex);
        free((*buffer)->ownerCond);
        free(*buffer);
        return EXIT_FAILURE;
    }

    (*buffer)->clientsCond = (pthread_cond_t *) calloc(1, sizeof(pthread_cond_t));
    if((*buffer)->clientsCond == NULL)
    {
        fprintf(stderr, "Can't allocate memory for clientsCond\n");
        pthread_mutex_destroy((*buffer)->mutex);
        pthread_cond_destroy((*buffer)->ownerCond);
        free((*buffer)->ownerCond);
        free((*buffer)->mutex);
        free(*buffer);
        return EXIT_FAILURE;
    }

    code = pthread_cond_init((*buffer)->clientsCond, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        pthread_mutex_destroy((*buffer)->mutex);
        pthread_cond_destroy((*buffer)->ownerCond);
        free((*buffer)->ownerCond);
        free((*buffer)->mutex);
        free((*buffer)->clientsCond);
        free(*buffer);
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

    int code = pthread_mutex_lock(buffer->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't mutex");
        return;
    }

    pthread_mutex_t *mutex = buffer->mutex;
    buffer->mutex = NULL;

    pthread_cond_destroy(buffer->ownerCond);
    pthread_cond_destroy(buffer->clientsCond);
    free(buffer->ownerCond);
    free(buffer->clientsCond);
    free(buffer->data);
    free(buffer);

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

int extendBuffer(struct Buffer *buffer, int newSize)
{
    if(newSize <= buffer->currentSize)
    {
        return EXIT_SUCCESS;
    }

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

    buffer->data[buffer->currentSize] = '\0';

    return EXIT_SUCCESS;
}

int popCharsFromBuffer(struct Buffer * buffer, int size)
{
    size = min(size, buffer->currentSize);

    for(int i = 0; i < buffer->currentSize - size; ++i)
    {
        int pos = size + i;
        buffer->data[i] = pos < buffer->currentSize ? buffer->data[pos] : (char)'\0';
    }

    buffer->currentSize -= size;

    return EXIT_SUCCESS;
}

int moveCharsToBuffer(struct Buffer *dest, struct Buffer *src, int count)
{
    if(src == dest)
    {
        return EXIT_SUCCESS;
    }

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
    return buffer->currentSize == 0 ? true : false;
}

int eraseBuffer(struct Buffer *buffer)
{
    buffer->currentSize = 0;
    buffer->totalBytesCount = 0;

    return EXIT_SUCCESS;
}
