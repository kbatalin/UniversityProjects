cmake_minimum_required(VERSION 3.6)
project(lab8)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES
        Converter.cpp
        Converter.h
        main.cpp Poller.cpp Poller.h tasks/ITask.h TestTask.h tasks/Pop3Task.cpp tasks/Pop3Task.h)

add_executable(lab8 ${SOURCE_FILES})