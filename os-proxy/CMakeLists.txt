cmake_minimum_required(VERSION 3.6)
project(29)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
        header.h SocketsStorage.c SocketsStorage.h utils.c utils.h Buffer.c Buffer.h main.c CacheManager.c CacheManager.h SocketInfo.c SocketInfo.h ClientHandlers.c ClientHandlers.h ServerHandlers.c ServerHandlers.h)

add_executable(29 ${SOURCE_FILES})