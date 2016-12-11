//
// Created by kir55rus on 05.12.16.
//

#ifndef _INC_29_CLIENTHANDLERS_H_
#define _INC_29_CLIENTHANDLERS_H_

struct SocketsStorage;
struct SocketInfo;
struct CacheManager;

int clientHandler(struct SocketsStorage *storage, struct SocketInfo *socketInfo, struct CacheManager *cacheManager);

#endif //_INC_29_CLIENTHANDLERS_H_
