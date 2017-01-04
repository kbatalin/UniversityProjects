//
// Created by kir55rus on 04.01.17.
//

#ifndef LAB8_UTILS_H
#define LAB8_UTILS_H

#include <string>

namespace Utils {
    const int BUFFER_SIZE = 1024;

    int Send(int socket, const std::string &msg);

    int Recv(int socket, std::string &res, const std::string &endSymbols = "");
}

#endif //LAB8_UTILS_H
