cmake_minimum_required(VERSION 3.3)
project(Dilemma)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp TournamentGame.cpp TournamentGame.h Register.h Game.cpp Game.h FastGame.cpp FastGame.h Factory.h DetailedGame.cpp DetailedGame.h AIs/AIRand.cpp AIs/AIRand.h AIs/AIKind.cpp AIs/AIKind.h AIs/AIAngry.cpp AIs/AIAngry.h AIs/AI.h AIs/AITitForTat.cpp AIs/AITitForTat.h AIs/AISmartAngry.cpp AIs/AISmartAngry.h AIs/AIPiBot.cpp AIs/AIPiBot.h AIs/AISlave.cpp AIs/AISlave.h AIs/AIMath.cpp AIs/AIMath.h Interface.cpp Interface.h)
add_executable(Dilemma ${SOURCE_FILES})

set (Boost_USE_STATIC_LIBS ON)
set (Boost_USE_MULTITHREADED ON)
find_package (Boost 1.54.0 COMPONENTS program_options exception REQUIRED)
include_directories(${Boost_INCLUDE_DIR})
target_link_libraries (Dilemma ${Boost_LIBRARIES})
