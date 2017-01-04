//
// Created by kir55rus on 04.01.17.
//

#include <sys/socket.h>
#include "Utils.h"

namespace Utils {

    int Send(int socket, const std::string &msg) {
        const char *ptr = msg.c_str();
        int length = msg.size();

        while (length > 0) {
            int code = send(socket, ptr, length, MSG_NOSIGNAL);
            if (code <= 0) {
                return -1;
            }

            length -= code;
            ptr += code;
        }

        return 0;
    }

    int Recv(int socket, std::string &res, const std::string &endSymbols) {
        while (true) {
            char buffer[BUFFER_SIZE] = {};
            int code = recv(socket, buffer, BUFFER_SIZE, MSG_NOSIGNAL);
            if (code <= 0) {
                return -1;
            }

            std::string newData(buffer, code);
            res.append(newData);

            if (newData.rfind(endSymbols) != std::string::npos) {
                return 0;
            }
        }
    }
}

