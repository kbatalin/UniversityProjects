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

    int Recv(int socket, std::string &res, const std::string &endSymbols, int bufferSize) {
        if (bufferSize <= 0) {
            return -1;
        }

        while (true) {
            char buffer[bufferSize] = {};
            int code = recv(socket, buffer, bufferSize, MSG_NOSIGNAL);
            if (code <= 0) {
                return endSymbols.empty() ? 0 : -1;
            }

            std::string newData(buffer, code);
            res.append(newData);

            if (!endSymbols.empty() && res.rfind(endSymbols) != std::string::npos) {
                return 0;
            }
        }
    }
}

