//
// Created by kir55rus on 30.11.16.
//

#ifndef _INC_29_CACHEMANAGER_H_
#define _INC_29_CACHEMANAGER_H_

#include <zconf.h>

#define ARRAY_EXTEND_STEEP 10
#define MAX_CACHE_RECORD_SIZE (20 * 1024 * 1024LL)
#define MAX_CACHE_SIZE (400 * 1024 * 1024LL)

struct SocketInfo;

enum CacheStatus
{
    PARTIAL,
    FULL,
    BIG_FILE,
    BAD_ANSWER,
    ERROR_CACHE,
};

struct CacheRecord
{
    char *data;
    int currentDataSize;
    int allocatedDataSize;

    enum CacheStatus status;

    struct SocketInfo *downloader;

    struct SocketInfo **clients;
    int clientsCount;
    int clientsArraySize;

    pthread_cond_t *downloaderCond;
    pthread_cond_t *clientsCond;
    pthread_mutex_t *mutex;
};

struct TreeNode
{
    char *key;
    struct CacheRecord *value;

    struct TreeNode *left;
    struct TreeNode *right;
};

struct Tree
{
    struct TreeNode *root;
    int size;
};

struct CacheManager
{
    struct Tree *cacheTree;
    long long int currentSize;

    pthread_mutex_t *mutex;
};

int initCacheRecord(struct CacheRecord **cacheRecord);

void destructCacheRecord(struct CacheRecord *cacheRecord);

int initCacheManager(struct CacheManager **cacheManager);

void destructCacheManager(struct CacheManager *cacheManager);

struct CacheRecord *getCacheRecord(struct CacheManager *cacheManager, const char *key);

int delCacheRecord(struct CacheManager *cacheManager, const char *key);

int createNewCacheRecord(struct CacheRecord **cacheRecord, struct CacheManager *cacheManager, const char *key,
                         struct SocketInfo *downloader, struct SocketInfo *client);

void stopWriteCacheRecord(struct CacheManager *cacheManager, struct CacheRecord *cacheRecord);

int addCacheRecordReader(struct CacheRecord *cacheRecord, struct SocketInfo *client);

void stopReadCacheRecord(struct CacheRecord * cacheRecord, struct SocketInfo * socketInfo);

void findAndStopReadCacheRecord(struct CacheManager *cacheManager, struct SocketInfo *socketInfo);

int addCharsToCacheRecord(struct CacheRecord *cacheRecord, struct CacheManager *cacheManager, const char *bytes, int size);

#endif //_INC_29_CACHEMANAGER_H_
