//
// Created by kir55rus on 29.11.16.
//

#ifndef _INC_29_UTILS_H_
#define _INC_29_UTILS_H_

#include <stdbool.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>


#define NPOS -1

struct ThreadsStorage;
struct CacheManager;
struct SocketInfo;
struct Buffer;

void printError(int code, const char * str);

void substr(const char *src, char *dest, int pos, int len);

int strpos(const char *haystack, const char *needle);

void strerase(char *str, int pos, int length);

struct addrinfo *getAddrInfo(const char *name, const char *service);

int min(int a, int b);

int max(int a, int b);

ssize_t sendFromBuffer(struct SocketInfo *socketInfo, int size);

#endif //_INC_29_UTILS_H_
