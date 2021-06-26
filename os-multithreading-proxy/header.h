//
// Created by kir55rus on 29.11.16.
//

#ifndef _INC_29_HEADER_H_
#define _INC_29_HEADER_H_

#define QUEUE_LISTEN_SIZE 32
#define BUFFER_SIZE 1024

#define PROXY_PORT "51234"
#define ERROR -1
#define POLL_TIMEOUT_MS -1
//#define ENABLE_RESUMING
#define ENABLE_LOG

#define ERROR_404 0
#define ERROR_404_STR "HTTP/1.0 404 Not Found\r\n\r\n"
#define ERROR_501 1
#define ERROR_501_STR "HTTP/1.0 501 Not Implemented\r\n\r\n"
#define ERROR_503 2
#define ERROR_503_STR "HTTP/1.0 503 Service Unavailable\r\n\r\n"
#define ERROR_505 3
#define ERROR_505_STR "HTTP/1.0 505 HTTP Version Not Supported\r\n\r\n"
#define ERROR_COUNT 4

struct ThreadsStorage;
struct SocketInfo;
struct CacheManager;

typedef int (*handlerType)(struct ThreadsStorage *storage, struct SocketInfo *socketInfo,
                           struct CacheManager *cacheManager);

#endif //_INC_29_HEADER_H_
