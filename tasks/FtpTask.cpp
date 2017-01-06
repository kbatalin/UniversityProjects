//
// Created by kir55rus on 05.01.17.
//

#include <cstring>
#include <iostream>
#include <unistd.h>
#include <fstream>
#include "FtpTask.h"
#include "../Utils.h"
#include "../Converter.h"

FtpTask::FtpTask() : m_cmdSocket(0), m_cmdServerAddrInfo(NULL), m_lastModifiedTime(0) {
}

FtpTask::~FtpTask() {
    freeaddrinfo(m_cmdServerAddrInfo);
}

int FtpTask::Init(const std::string &server, const std::string &path, const std::string &login, const std::string &pass, const std::string &downloadPath) {
    Log("Init");
    m_server = server;
    m_path = path;
    m_login = login;
    m_pass = pass;
    m_downloadPath = downloadPath;

    addrinfo hints;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(m_server.c_str(), "ftp", &hints, &m_cmdServerAddrInfo);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    return 0;
}

int FtpTask::StartSession() {
    Log("Start session");

    m_cmdSocket = socket(m_cmdServerAddrInfo->ai_family, m_cmdServerAddrInfo->ai_socktype, m_cmdServerAddrInfo->ai_protocol);

    if (m_cmdSocket == -1) {
        perror("Can't open socket");
        return -1;
    }

    int code = connect(m_cmdSocket, m_cmdServerAddrInfo->ai_addr, m_cmdServerAddrInfo->ai_addrlen);
    if (code == -1) {
        perror("Can't connect");
        return -1;
    }

    std::string answer;
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv Start session");
        close(m_cmdSocket);
        return -1;
    }

    code = Utils::Send(m_cmdSocket, "USER " + m_login + "\r\n");
    if(code < 0) {
        Log("Can't send USER");
        close(m_cmdSocket);
        return -1;
    }

    answer.clear();
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0) {
        Log("Can't recv USER");
        close(m_cmdSocket);
        return -1;
    }

    if(answer[0] == '2') {
        Log("Success login");
        return 0;
    }

    if(answer[0] != '3') {
        Log("Bas answer for USER");
        close(m_cmdSocket);
        return -1;
    }

    code = Utils::Send(m_cmdSocket, "PASS " + m_pass + "\r\n");
    if(code < 0) {
        Log("Can't send PASS");
        close(m_cmdSocket);
        return -1;
    }

    answer.clear();
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv PASS");
        close(m_cmdSocket);
        return -1;
    }

    code = Utils::Send(m_cmdSocket, "TYPE i\r\n");
    if(code < 0) {
        Log("Can't send TYPE");
        close(m_cmdSocket);
        return -1;
    }

    answer.clear();
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv TYPE");
        close(m_cmdSocket);
        return -1;
    }

    return 0;
}

int FtpTask::Update() {
    Log("Update");
    int code = Utils::Send(m_cmdSocket, "MDTM " + m_path + "\r\n");
    if(code < 0) {
        Log("Can't send MDTM");
        close(m_cmdSocket);
        return -1;
    }

    std::string answer;
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv MDTM");
        close(m_cmdSocket);
        return -1;
    }

    long newLastModifiedTime = GetLastModifiedTime(answer);
    if(newLastModifiedTime <= m_lastModifiedTime) {
        Log("It's old file");
        return 0;
    }
    Log("It's new file");

    code = DownloadFile();
    if(code < 0) {
        Log("Can't download file");
        close(m_cmdSocket);
        return -1;
    }

    m_lastModifiedTime = newLastModifiedTime;

    return 0;
}

int FtpTask::DownloadFile() {
    Log("Download file");

    int code = Utils::Send(m_cmdSocket, "PASV\r\n");
    if (code < 0) {
        Log("Can't send PASV");
        return -1;
    }

    std::string answer;
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv PASV");
        return -1;
    }

    int dataSocket = CreateDataSocket(answer);
    if (dataSocket < 0) {
        Log("Can't create data socket");
        return -1;
    }

    code = Utils::Send(m_cmdSocket, "RETR " + m_path + "\r\n");
    if (code < 0) {
        Log("Can't send RETR");
        close(dataSocket);
        return -1;
    }

    answer.clear();
    code = Utils::Recv(m_cmdSocket, answer, "\r\n", 1);
    if(code < 0 || answer[0] != '1') {
        Log("Can't recv info RETR");
        close(dataSocket);
        return -1;
    }

    code = SaveFile(dataSocket);
    if (code < 0) {
        Log("Can't save file");
        close(dataSocket);
        return -1;
    }
    close(dataSocket);

    answer.clear();
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if(code < 0 || answer[0] != '2') {
        Log("Can't recv RETR");
        return -1;
    }

    return 0;
}

int FtpTask::SaveFile(int dataSocket) {
    Log("Save file");
    std::ofstream out;
    out.open(m_downloadPath, std::ofstream::out);
    if(!out.is_open() || !out.good()) {
        return -1;
    }

    ssize_t size;
    do {
        char buffer[BUFFER_SIZE];
        size = recv(dataSocket, buffer, BUFFER_SIZE, MSG_NOSIGNAL);
        if(size <= 0) {
            continue;
        }

        out.write(buffer, size);
        if (!out.good()) {
            out.close();
            return -1;
        }

    } while(size > 0);

    out.close();
    return 0;
}

int FtpTask::Noop() {
    Log("Noop");

    int code = Utils::Send(m_cmdSocket, "NOOP\r\n");
    if (code < 0) {
        Log("Can't send NOOP");
        close(m_cmdSocket);
        return -1;
    }

    std::string answer;
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if (code < 0 || answer[0] != '2') {
        Log("Can't recv NOOP");
        close(m_cmdSocket);
        return -1;
    }

    return 0;
}

int FtpTask::EndSession() {
    Log("End session");

    int code = Utils::Send(m_cmdSocket, "QUIT\r\n");
    if (code < 0) {
        Log("Can't send QUIT");
        close(m_cmdSocket);
        return -1;
    }

    std::string answer;
    code = Utils::Recv(m_cmdSocket, answer, "\r\n");
    if (code < 0 || answer[0] != '2') {
        Log("Can't recv QUIT");
        close(m_cmdSocket);
        return -1;
    }

    return 0;
}

void FtpTask::Log(const std::string &msg) {
    printf("Pop3 (%s): %s\n", m_server.c_str(), msg.c_str());
}

int FtpTask::CreateDataSocket(const std::string &data) {
    size_t beginBracketPos = data.find("(");
    if (beginBracketPos == std::string::npos) {
        return -1;
    }
    beginBracketPos += strlen("(");

    size_t endBracketPos = data.find(")", beginBracketPos);
    if (endBracketPos == std::string::npos) {
        return -1;
    }

    std::string rowAddress = "," + data.substr(beginBracketPos, endBracketPos - beginBracketPos);
    std::string ipStr;

    size_t prevCommaPos = 0;
    for(int i = 0; i < 4; ++i) {
        size_t commaPos = rowAddress.find(",", prevCommaPos + 1);
        if (commaPos == std::string::npos) {
            return -1;
        }

        ipStr += rowAddress.substr(prevCommaPos + 1, commaPos - prevCommaPos - 1) + ".";
        prevCommaPos = commaPos;
    }

    ipStr.erase(ipStr.size() - 1);

    size_t lastCommaPos = rowAddress.find(",", prevCommaPos + 1);
    if (lastCommaPos == std::string::npos) {
        return -1;
    }
    int port = Converter::convert(rowAddress.substr(prevCommaPos + 1, lastCommaPos - prevCommaPos - 1));
    port <<= 8;
    port += Converter::convert(rowAddress.substr(lastCommaPos + 1));
    std::string portStr = Converter::convert(port);

    addrinfo hints, *res;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(ipStr.c_str(), portStr.c_str(), &hints, &res);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    int dataSocket = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
    if (dataSocket == -1) {
        perror("Can't open socket");
        freeaddrinfo(res);
        return -1;
    }

    code = connect(dataSocket, res->ai_addr, res->ai_addrlen);
    if (code == -1) {
        perror("Can't connect");
        freeaddrinfo(res);
        return -1;
    }
    freeaddrinfo(res);

    return dataSocket;
}

long FtpTask::GetLastModifiedTime(const std::string &data) {
    size_t spacePos = data.find(" ");
    if (spacePos == std::string::npos) {
        return -1;
    }
    spacePos += strlen(" ");

    size_t newLinePos = data.find("\r\n", spacePos);
    if (newLinePos == std::string::npos) {
        return -1;
    }

    std::string rowDateStr = data.substr(spacePos, newLinePos - spacePos);

    std::string dateStr = rowDateStr.substr(0, 4) + "-" + rowDateStr.substr(4, 2) + "-" + rowDateStr.substr(6, 2) + " "
    + rowDateStr.substr(8, 2) + ":" + rowDateStr.substr(10, 2) + ":" + rowDateStr.substr(12, 2);

    long dateTimestamp = Converter::convertDate(dateStr);
    return dateTimestamp;
}


