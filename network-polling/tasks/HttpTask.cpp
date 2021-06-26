//
// Created by kir55rus on 04.01.17.
//

#include <iostream>
#include <cstring>
#include <netdb.h>
#include <unistd.h>
#include <fstream>
#include "HttpTask.h"
#include "../Utils.h"
#include "../Converter.h"

HttpTask::HttpTask() :m_downloadedChecksum(0), m_serverAddrInfo(NULL), m_socket(-1), m_lastModifiedDate(0) {
}

HttpTask::~HttpTask() {
    freeaddrinfo(m_serverAddrInfo);
}

int HttpTask::Init(const std::string &url, const std::string &downloadPath) {
    Log("Init");
    m_url = url;
    m_downloadPath = downloadPath;

    size_t firstPos = url.find("http://");
    if (firstPos == std::string::npos) {
        return -1;
    }
    firstPos += strlen("http://");

    size_t secondPos = url.find("/", firstPos);
    if (secondPos == std::string::npos) {
        secondPos = url.size();
    }

    std::string domain = url.substr(firstPos, secondPos - firstPos);

    addrinfo hints;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(domain.c_str(), "http", &hints, &m_serverAddrInfo);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    return 0;
}

int HttpTask::CreateSocket() {
    Log("Create socket");
    m_socket = socket(m_serverAddrInfo->ai_family, m_serverAddrInfo->ai_socktype, m_serverAddrInfo->ai_protocol);

    if (m_socket == -1) {
        perror("Can't open socket");
        return -1;
    }

    int code = connect(m_socket, m_serverAddrInfo->ai_addr, m_serverAddrInfo->ai_addrlen);
    if (code == -1) {
        perror("Can't connect");
        return -1;
    }

    return 0;
}

int HttpTask::StartSession() {
    return 0;
}

int HttpTask::Update() {
    Log("Update");

    int code = CreateSocket();
    if (code < 0) {
        return -1;
    }

    code = Send("HEAD " + m_url + " HTTP/1.0\r\n\r\n");
    if(code < 0) {
        Log("Can't send HEAD");
        close(m_socket);
        return -1;
    }

    std::string answer;
    code = Recv(answer);
    if(code < 0) {
        Log("Can't recv HEAD");
        close(m_socket);
        return -1;
    }
    close(m_socket);

    time_t lastModifiedDate = GetLastModifiedTime(answer);
    if(lastModifiedDate < 0) {
        Log("Head doesn't have last modified time");
        return DownloadData();
    }
    Log("Head has last modified time");

    if(m_lastModifiedDate >= lastModifiedDate) {
        Log("It's old file");
        return 0;
    }
    Log("It's new file");

    code = DownloadData();
    if (code < 0) {
        Log("Can't download file");
        return -1;
    }

    m_lastModifiedDate = lastModifiedDate;

    return 0;
}

int HttpTask::DownloadData() {
    Log("Download data");

    int code = CreateSocket();
    if (code < 0) {
        return -1;
    }

    code = Send("GET " + m_url + " HTTP/1.0\r\n\r\n");
    if(code < 0) {
        Log("Can't send GET");
        close(m_socket);
        return -1;
    }

    std::string answer;
    code = Recv(answer);
    if(code < 0) {
        Log("Can't recv GET");
        close(m_socket);
        return -1;
    }
    close(m_socket);

    size_t dataPos = answer.find("\r\n\r\n");
    if (dataPos == std::string::npos) {
        Log("Can't find data");
        return -1;
    }
    dataPos += strlen("\r\n\r\n");
    const char *dataPtr = answer.c_str() + dataPos;
    size_t dataSize = answer.size() - dataPos;

    checksum_t newDataChecksum = GetChecksum(dataPtr, dataSize);
    if(newDataChecksum == m_downloadedChecksum) {
        Log("Old file");
        return 0;
    }
    Log("New file");

    code = SaveFile(dataPtr, dataSize);
    if (code < 0) {
        Log("Can't save file");
        return -1;
    }

    m_downloadedChecksum = newDataChecksum;

    return 0;
}

int HttpTask::EndSession() {
    return 0;
}

int HttpTask::Send(const std::string &msg) {
    return Utils::Send(m_socket, msg);
}

int HttpTask::Recv(std::string &res, const std::string &endSymbols) {
    return Utils::Recv(m_socket, res, endSymbols);
}

void HttpTask::Log(const std::string &msg) {
    printf("Http (%s): %s\n", m_url.c_str(), msg.c_str());
}

int HttpTask::SaveFile(const std::string &data) {
    return SaveFile(data.c_str(), data.size());
}

int HttpTask::SaveFile(const char *data, size_t size) {
    Log("Save file");
    std::ofstream out;
    out.open(m_downloadPath, std::ofstream::out);
    if(!out.is_open() || !out.good()) {
        return -1;
    }

    out.write(data, size);

    if (!out.good()) {
        out.close();
        return -1;
    }

    out.close();
    return 0;
}

HttpTask::checksum_t HttpTask::GetChecksum(const std::string &data) {
    return GetChecksum(data.c_str(), data.size());
}

HttpTask::checksum_t HttpTask::GetChecksum(const char *data, size_t size) {
    checksum_t result = 0;

    for(size_t i = 0; i < size; ++i) {
        result = (result + data[i]) & 0xFF;
    }
    result = ((result ^ 0xFF) + 1) & 0xFF;

    return result;
}

time_t HttpTask::GetLastModifiedTime(const std::string &head) {
    size_t linePos = head.find("Last-Modified: ");
    if(linePos == std::string::npos) {
        return -1;
    }
    linePos += strlen("Last-Modified: ");

    size_t newLinePos = head.find("\r\n", linePos);
    if (newLinePos == std::string::npos) {
        return -1;
    }

    std::string dateStr = head.substr(linePos, newLinePos - linePos);
    time_t date = Converter::convertDate(dateStr);

    return date;
}

int HttpTask::Noop() {
    return 0;
}

