//
// Created by kir55rus on 30.11.16.
//

#include "CacheManager.h"
#include <stdlib.h>
#include <pthread.h>
#include <stdio.h>
#include <sys/socket.h>
#include <netdb.h>
#include <string.h>
#include <sys/socket.h>
#include <netdb.h>
#include <assert.h>
#include "utils.h"
#include "header.h"
#include "SocketInfo.h"

//---------------------------CacheRecord----------------------------//

int initCacheRecord(struct CacheRecord **cacheRecord)
{
    *cacheRecord = (struct CacheRecord *) calloc(1, sizeof(struct CacheRecord));
    if (*cacheRecord == NULL)
    {
        fprintf(stderr, "Can't allocate memory for cache record\n");
        return EXIT_FAILURE;
    }

    (*cacheRecord)->status = PARTIAL;

    (*cacheRecord)->mutex = (pthread_mutex_t *) calloc(1, sizeof(pthread_mutex_t));
    if((*cacheRecord)->mutex == NULL)
    {
        fprintf(stderr, "Can't allocate memory for rwlock\n");
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    int code = pthread_mutex_init((*cacheRecord)->mutex, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init rwlock");
        free((*cacheRecord)->mutex);
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    (*cacheRecord)->clientsCond = (pthread_cond_t *) calloc(1, sizeof(pthread_cond_t));
    if((*cacheRecord)->clientsCond == NULL)
    {
        fprintf(stderr, "Can't allocate memory for clientsCond\n");
        pthread_mutex_destroy((*cacheRecord)->mutex);
        free((*cacheRecord)->mutex);
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    code = pthread_cond_init((*cacheRecord)->clientsCond, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init clientsCond");
        pthread_mutex_destroy((*cacheRecord)->mutex);
        free((*cacheRecord)->mutex);
        free((*cacheRecord)->clientsCond);
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    (*cacheRecord)->downloaderCond = (pthread_cond_t *) calloc(1, sizeof(pthread_cond_t));
    if((*cacheRecord)->downloaderCond == NULL)
    {
        fprintf(stderr, "Can't allocate memory for clientsCond\n");
        pthread_mutex_destroy((*cacheRecord)->mutex);
        pthread_cond_destroy((*cacheRecord)->clientsCond);
        free((*cacheRecord)->clientsCond);
        free((*cacheRecord)->mutex);
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    code = pthread_cond_init((*cacheRecord)->downloaderCond, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init clientsCond");
        pthread_mutex_destroy((*cacheRecord)->mutex);
        pthread_cond_destroy((*cacheRecord)->clientsCond);
        free((*cacheRecord)->mutex);
        free((*cacheRecord)->clientsCond);
        free((*cacheRecord)->downloaderCond);
        free(*cacheRecord);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructCacheRecord(struct CacheRecord *cacheRecord)
{
    if (cacheRecord == NULL)
    {
        return;
    }

    int code = pthread_mutex_lock(cacheRecord->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't mutex");
        return;
    }

    pthread_mutex_t *mutex = cacheRecord->mutex;
    cacheRecord->mutex = NULL;

    pthread_cond_destroy(cacheRecord->clientsCond);
    free(cacheRecord->clientsCond);
    pthread_cond_destroy(cacheRecord->downloaderCond);
    free(cacheRecord->downloaderCond);
    free(cacheRecord->data);
    free(cacheRecord->clients);
    free(cacheRecord);

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


//-------------------------------Tree------------------------------//

int initTree(struct Tree **tree)
{
    *tree = (struct Tree *) calloc(1, sizeof(struct Tree));
    if (*tree == NULL)
    {
        fprintf(stderr, "Can't allocate memory for tree\n");
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructTreeNode(struct TreeNode *node)
{
    if (node == NULL)
    {
        return;
    }

    destructCacheRecord(node->value);
    free(node->key);
    free(node);
}

void destructTreeNodes(struct TreeNode *node)
{
    if (node == NULL)
    {
        return;
    }

    destructTreeNodes(node->left);
    destructTreeNodes(node->right);

    destructTreeNodes(node);
}

void destructTree(struct Tree *tree)
{
    destructTreeNodes(tree->root);
    free(tree);
}

int addTreeNode(struct Tree *tree, const char *key, struct CacheRecord *value)
{
    if (key == NULL)
    {
        return EXIT_SUCCESS;
    }

#ifdef ENABLE_LOG
    printf("Add node %s\n", key);
#endif
    struct TreeNode *prev = NULL;
    struct TreeNode *current = tree->root;

    while (current != NULL && strcmp(current->key, key) != 0)
    {
        prev = current;
        current = strcmp(key, current->key) < 0 ? current->left : current->right;
    }

    if (current != NULL)
    {
#ifdef ENABLE_LOG
        printf("There is node with this key\n");
#endif
        return EXIT_SUCCESS;
    }

    struct TreeNode *newNode = (struct TreeNode *) calloc(1, sizeof(struct TreeNode));
    if (newNode == NULL)
    {
        fprintf(stderr, "Can't allocate memory for new node\n");
        return EXIT_FAILURE;
    }

    newNode->key = (char *) calloc(strlen(key) + 1, sizeof(char));
    if (newNode->key == NULL)
    {
        fprintf(stderr, "Can't allocate memory for key\n");
        free(newNode);
        return EXIT_FAILURE;
    }

    ++tree->size;

    strcpy(newNode->key, key);
    newNode->value = value;

    if (prev == NULL)
    {
#ifdef ENABLE_LOG
        printf("It's new root node\n");
#endif
        tree->root = newNode;
        return EXIT_SUCCESS;
    }

    if (strcmp(key, prev->key) < 0)
    {
        prev->left = newNode;
    }
    else
    {
        prev->right = newNode;
    }

    return EXIT_SUCCESS;
}

struct CacheRecord *getTreeNode(struct Tree *tree, const char *key)
{
    if (key == NULL)
    {
        return NULL;
    }

    struct TreeNode *current = tree->root;

    while (current != NULL && strcmp(current->key, key) != 0)
    {
        current = strcmp(key, current->key) < 0 ? current->left : current->right;
    }

    struct CacheRecord * result = current == NULL ? NULL : current->value;

    return result;
}

int delTreeNode(struct Tree *tree, const char *key)
{
    if (key == NULL)
    {
        return EXIT_SUCCESS;
    }
    struct TreeNode *prev = NULL;
    struct TreeNode *current = tree->root;

    while (current != NULL && strcmp(current->key, key) != 0)
    {
        prev = current;
        current = strcmp(key, current->key) < 0 ? current->left : current->right;
    }
    if (current == NULL)
    {
        return EXIT_SUCCESS;
    }

    struct TreeNode **ptrFromPrev = (prev == NULL) ? &tree->root : (prev->left == current ? &prev->left : &prev->right);
    if (current->left == NULL && current->right == NULL)
    {
        --tree->size;
        *ptrFromPrev = NULL;
        destructTreeNode(current);
        return EXIT_SUCCESS;
    }
    if ((current->left == NULL || current->right == NULL) && current->left != current->right)
    {
        --tree->size;
        struct TreeNode *notNullChild = current->left != NULL ? current->left : current->right;
        *ptrFromPrev = notNullChild;
        destructTreeNode(current);
        return EXIT_SUCCESS;
    }

    if (current->right->left == NULL)
    {
        --tree->size;
        current->right->left = current->left;
        *ptrFromPrev = current->right;
        destructTreeNode(current);
        return EXIT_SUCCESS;
    }

    struct TreeNode *prevMostLeftNode = current->right;
    struct TreeNode *mostLeftNode = prevMostLeftNode->left;
    while (mostLeftNode->left != NULL)
    {
        prevMostLeftNode = mostLeftNode;
        mostLeftNode = mostLeftNode->left;
    }

    --tree->size;
    prevMostLeftNode->left = mostLeftNode->right;
    mostLeftNode->left = current->left;
    mostLeftNode->right = current->right;
    *ptrFromPrev = mostLeftNode;
    destructTreeNode(current);
    return EXIT_SUCCESS;
}


//---------------------------- Cache Manager -----------------------------//

int initCacheManager(struct CacheManager **cacheManager)
{
    *cacheManager = (struct CacheManager *) calloc(1, sizeof(struct CacheManager));
    if (*cacheManager == NULL)
    {
        fprintf(stderr, "Can't allocate memory for cache manager\n");
        return EXIT_FAILURE;
    }

    if (initTree(&(*cacheManager)->cacheTree) != EXIT_SUCCESS)
    {
        free(*cacheManager);
        return EXIT_FAILURE;
    }

    (*cacheManager)->mutex = (pthread_mutex_t *) calloc(1, sizeof(pthread_mutex_t));
    if((*cacheManager)->mutex == NULL)
    {
        fprintf(stderr, "Can't allocate memory for mutex\n");
        destructTree((*cacheManager)->cacheTree);
        free(*cacheManager);
        return EXIT_FAILURE;
    }

    int code = pthread_mutex_init((*cacheManager)->mutex, NULL);
    if(code != EXIT_SUCCESS)
    {
        printError(code, "Can' init mutex");
        destructTree((*cacheManager)->cacheTree);
        free((*cacheManager)->mutex);
        free(*cacheManager);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void destructCacheManager(struct CacheManager *cacheManager)
{
    if (cacheManager == NULL)
    {
        return;
    }

    int code = pthread_mutex_lock(cacheManager->mutex);
    if (code != EXIT_SUCCESS)
    {
        printError(code, "Can't mutex lock");
        return;
    }

    pthread_mutex_t *mutex = cacheManager->mutex;
    cacheManager->mutex = NULL;

    destructTree(cacheManager->cacheTree);
    free(cacheManager);

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

struct CacheRecord *getCacheRecord(struct CacheManager *cacheManager, const char *key)
{
    return getTreeNode(cacheManager->cacheTree, key);
}

int delCacheRecord(struct CacheManager *cacheManager, const char *key)
{
#ifdef ENABLE_LOG
    printf("Del cache record %s\n", key);
#endif
    struct CacheRecord *cacheRecord = getTreeNode(cacheManager->cacheTree, key);
    if (cacheRecord == NULL)
    {
        return EXIT_SUCCESS;
    }

    if(cacheRecord->status == FULL)
    {
        cacheManager->currentSize -= cacheRecord->allocatedDataSize;
    }
    else //PARTIAL or BIG_FILE
    {
        cacheManager->currentSize -= MAX_CACHE_RECORD_SIZE;
    }

    return delTreeNode(cacheManager->cacheTree, key);
}

int addCacheRecordReader(struct CacheRecord *cacheRecord, struct SocketInfo *client)
{
#ifdef ENABLE_LOG
    printf("client %d: add reader for: %s\n", client->socket, client->url);
#endif

    if (cacheRecord->clientsCount < cacheRecord->clientsArraySize)
    {
        cacheRecord->clients[cacheRecord->clientsCount++] = client;
        return EXIT_SUCCESS;
    }

    int newSize = cacheRecord->clientsArraySize + ARRAY_EXTEND_STEEP;
    struct SocketInfo **newArray = (struct SocketInfo **) realloc(cacheRecord->clients,
                                                                  newSize * sizeof(struct SocketInfo *));
    if (newArray == NULL)
    {
        fprintf(stderr, "Can't realloc clients array\n");
        return EXIT_FAILURE;
    }

    cacheRecord->clientsArraySize = newSize;
    cacheRecord->clients = newArray;
    cacheRecord->clients[cacheRecord->clientsCount++] = client;

    return EXIT_SUCCESS;
}

int delUnusedCacheRecord(struct CacheManager *cacheManager, struct TreeNode *node)
{
    if (node == NULL)
    {
        return EXIT_FAILURE;
    }
//захватить ноду на чтение
    if (node->value->clientsCount == 0 && node->value->downloader == NULL)
    {
        delCacheRecord(cacheManager, node->key);
        return EXIT_SUCCESS;
    }

    if (delUnusedCacheRecord(cacheManager, node->left) == EXIT_SUCCESS)
    {
        return EXIT_SUCCESS;
    }

    if (delUnusedCacheRecord(cacheManager, node->right) == EXIT_SUCCESS)
    {
        return EXIT_SUCCESS;
    }

    return EXIT_FAILURE;
}

int createNewCacheRecord(struct CacheRecord **pCacheRecord, struct CacheManager *cacheManager, const char *key,
                         struct SocketInfo *downloader, struct SocketInfo *client)
{
    while(cacheManager->currentSize + MAX_CACHE_RECORD_SIZE > MAX_CACHE_SIZE)
    {
        if(delUnusedCacheRecord(cacheManager, cacheManager->cacheTree->root) != EXIT_SUCCESS)
        {
            return EXIT_FAILURE;
        }
    }

    struct CacheRecord *cacheRecord;

    if (initCacheRecord(&cacheRecord) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    cacheRecord->downloader = downloader;

    if (addCacheRecordReader(cacheRecord, client) != EXIT_SUCCESS)
    {
        destructCacheRecord(cacheRecord);
        return EXIT_FAILURE;
    }

    if (addTreeNode(cacheManager->cacheTree, key, cacheRecord) != EXIT_SUCCESS)
    {
        destructCacheRecord(cacheRecord);
        return EXIT_FAILURE;
    }

    cacheManager->currentSize += MAX_CACHE_RECORD_SIZE;

    if(pCacheRecord != NULL)
    {
        *pCacheRecord = cacheRecord;
    }

    return EXIT_SUCCESS;
}

void stopWriteCacheRecord(struct CacheManager *cacheManager, struct CacheRecord *cacheRecord)
{
    if(cacheRecord == NULL || cacheManager == NULL)
    {
            return;
    }

#ifdef ENABLE_LOG
    printf("server %d: stop write cache\n", cacheRecord->downloader->socket);
#endif

    cacheManager->currentSize -= (MAX_CACHE_RECORD_SIZE - cacheRecord->allocatedDataSize);
    cacheRecord->status = FULL;
    cacheRecord->downloader = NULL;
}

void stopReadCacheRecord(struct CacheRecord * cacheRecord, struct SocketInfo * socketInfo)
{
    for (int i = 0; i < cacheRecord->clientsCount; ++i)
    {
        if (socketInfo->socket == cacheRecord->clients[i]->socket)
        {
            cacheRecord->clients[i] = cacheRecord->clients[cacheRecord->clientsCount - 1];
            cacheRecord->clients[cacheRecord->clientsCount - 1] = NULL;
            --cacheRecord->clientsCount;
            break;
        }
    }
}

void findAndStopReadCacheRecord(struct CacheManager *cacheManager, struct SocketInfo *socketInfo)
{
    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if (cacheRecord == NULL)
    {
        return;
    }

    stopReadCacheRecord(cacheRecord, socketInfo);
}

int extendCacheRecord(struct CacheRecord *cacheRecord, struct CacheManager *cacheManager, int newSize)
{
    if (newSize <= cacheRecord->allocatedDataSize)
    {
        return EXIT_SUCCESS;
    }

    char *newData = (char *) realloc(cacheRecord->data, newSize * sizeof(char));
    if (newData == NULL)
    {
        fprintf(stderr, "Can't allocate memory for cache record\n");
        return EXIT_FAILURE;
    }

    cacheRecord->data = newData;
    cacheRecord->allocatedDataSize = newSize;

    return EXIT_SUCCESS;
}

int addCharsToCacheRecord(struct CacheRecord *cacheRecord, struct CacheManager *cacheManager, const char *bytes, int size)
{
    int newSize = size + cacheRecord->currentDataSize;
    if (newSize > MAX_CACHE_RECORD_SIZE)
    {
        return EXIT_FAILURE;
    }
    if (newSize >= cacheRecord->allocatedDataSize &&
        extendCacheRecord(cacheRecord, cacheManager, newSize) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    for (int i = 0; i < size; ++i)
    {
        cacheRecord->data[cacheRecord->currentDataSize++] = bytes[i];
    }

    return EXIT_SUCCESS;
}
