//
// Created by kir55rus on 05.12.16.
//

#ifndef _INC_29_SERVERHANDLERS_H_
#define _INC_29_SERVERHANDLERS_H_

struct ThreadsStorage;
struct CacheManager;
struct SocketInfo;

int handlerServerSendFromSocket(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                                struct CacheManager *cacheManager);

#endif //_INC_29_SERVERHANDLERS_H_
