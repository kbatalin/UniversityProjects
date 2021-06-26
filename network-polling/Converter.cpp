//
// Created by kir55rus on 30.09.16.
//

#include <cstdlib>
#include <cstdio>
#include <string>
#include "Converter.h"

namespace Converter {
    std::string convert(int n) {
        char buf[100];
        sprintf(buf, "%d", n);
        return std::string(buf);
    }

    std::string convert(long n) {
        char buf[100];
        sprintf(buf, "%ld", n);
        return std::string(buf);
    }

    int convert(const std::string &str) {
        return atoi(str.c_str());
    }

    std::string charToBytes(const char *str, int size) {
        std::string res;
        for (int i = 0; i < size; ++i) {
            res += convert((int) str[i]) + " ";
        }

        return res;
    }

    std::string convert(in_port_t port) {
        return convert((int) port);
    }

    time_t convertDate(const std::string &strDate) {
        std::string cmd = "date -d \"" + strDate + "\" \"+%s\"";
        FILE *pipe = popen(cmd.c_str(), "r");
        if(pipe == NULL) {
            return -1;
        }

        time_t timestamp;
        fscanf(pipe, "%ld", &timestamp);
        pclose(pipe);

        return timestamp;
    }
}
