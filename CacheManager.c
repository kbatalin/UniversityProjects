//
// Created by kir55rus on 30.11.16.
//

#include "CacheManager.h"
#include <stdlib.h>
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
    assert(MAX_CACHE_RECORD_SIZE <= MAX_CACHE_SIZE);

    *cacheRecord = (struct CacheRecord *) calloc(1, sizeof(struct CacheRecord));
    if (*cacheRecord == NULL)
    {
        fprintf(stderr, "Can't allocate memory for cache record\n");
        return EXIT_FAILURE;
    }

    (*cacheRecord)->status = PARTIAL;

    return EXIT_SUCCESS;
}

void destructCacheRecord(struct CacheRecord *cacheRecord)
{
    if (cacheRecord == NULL)
    {
        return;
    }

    free(cacheRecord->data);
    free(cacheRecord->clients);
    free(cacheRecord);
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

    printf("Add node %s\n", key);

    struct TreeNode *prev = NULL;
    struct TreeNode *current = tree->root;

    while (current != NULL && strcmp(current->key, key) != 0)
    {
        prev = current;
        current = strcmp(key, current->key) < 0 ? current->left : current->right;
    }

    if (current != NULL)
    {
        printf("There is node with this key\n");
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
        printf("It's new root node\n");
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

    return current == NULL ? NULL : current->value;
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

    return EXIT_SUCCESS;
}

void destructCacheManager(struct CacheManager *cacheManager)
{
    if (cacheManager == NULL)
    {
        return;
    }

    destructTree(cacheManager->cacheTree);
    free(cacheManager);
}

struct CacheRecord *getCacheRecord(struct CacheManager *cacheManager, const char *key)
{
    return getTreeNode(cacheManager->cacheTree, key);
}

int delCacheRecord(struct CacheManager *cacheManager, const char *key)
{
    printf("Del cache record %s\n", key);
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

int createNewCacheRecord(struct CacheRecord **cacheRecord, struct CacheManager *cacheManager, const char *key,
                         struct SocketInfo *downloader, struct SocketInfo *client)
{
    while(cacheManager->currentSize + MAX_CACHE_RECORD_SIZE > MAX_CACHE_SIZE)
    {
        if(delUnusedCacheRecord(cacheManager, cacheManager->cacheTree->root) != EXIT_SUCCESS)
        {
            return EXIT_FAILURE;
        }
    }

    if (initCacheRecord(cacheRecord) != EXIT_SUCCESS)
    {
        return EXIT_FAILURE;
    }

    (*cacheRecord)->downloader = downloader;

    if (addCacheRecordReader(*cacheRecord, client) != EXIT_SUCCESS)
    {
        destructCacheRecord(*cacheRecord);
        return EXIT_FAILURE;
    }

    if (addTreeNode(cacheManager->cacheTree, key, *cacheRecord) != EXIT_SUCCESS)
    {
        destructCacheRecord(*cacheRecord);
        return EXIT_FAILURE;
    }

    cacheManager->currentSize += MAX_CACHE_RECORD_SIZE;

    return EXIT_SUCCESS;
}

void stopWriteCacheRecord(struct CacheManager *cacheManager, struct CacheRecord *cacheRecord)
{
    if(cacheRecord == NULL || cacheManager == NULL)
    {
        return;
    }

    cacheManager->currentSize -= (MAX_CACHE_RECORD_SIZE - cacheRecord->allocatedDataSize);
    cacheRecord->status = FULL;
}

void stopReadCacheRecord(struct CacheManager *cacheManager, struct SocketInfo *socketInfo)
{
    struct CacheRecord *cacheRecord = getCacheRecord(cacheManager, socketInfo->url);
    if (cacheRecord == NULL)
    {
        return;
    }

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
