//
// Created by kir55rus on 14.12.16.
//

#ifndef _INC_30_THREADDATA_H_
#define _INC_30_THREADDATA_H_

struct SocketInfo;
struct CacheManager;
struct ThreadsStorage;

struct ThreadData
{
    struct SocketInfo * socketInfo;
    struct CacheManager * cacheManager;
    struct ThreadsStorage * threadsStorage;
};

#endif //_INC_30_THREADDATA_H_
