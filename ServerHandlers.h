//
// Created by kir55rus on 05.12.16.
//

#ifndef _INC_29_SERVERHANDLERS_H_
#define _INC_29_SERVERHANDLERS_H_

struct SocketsStorage;
struct CacheManager;
struct SocketInfo;

int serverHandler(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager);

#endif //_INC_29_SERVERHANDLERS_H_
