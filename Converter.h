//
// Created by kir55rus on 10.09.16.
//

#ifndef _LAB1_CONVERTER_H_
#define _LAB1_CONVERTER_H_

#include <cstdio>
#include <string>
#include <netinet/in.h>

namespace Converter {
    std::string convert(int n);

    std::string convert(long n);

    std::string convert(in_port_t port);

    int convert(const std::string &str);

    time_t convertDate(const std::string &strDate);

    std::string charToBytes(const char *str, int size);
}

#endif //_LAB1_CONVERTER_H_
