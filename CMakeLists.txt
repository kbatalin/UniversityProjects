cmake_minimum_required(VERSION 3.6)
project(30)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    Buffer.c
    Buffer.h
    CacheManager.c
    CacheManager.h
    ClientHandlers.c
    ClientHandlers.h
    header.h
    main.c
    ServerHandlers.c
    ServerHandlers.h
    SocketInfo.c
    SocketInfo.h
        ThreadsStorage.c
        ThreadsStorage.h
    utils.c
    utils.h ThreadData.h)

add_executable(30 ${SOURCE_FILES})