//
// Created by kir55rus on 04.01.17.
//

#include <unistd.h>
#include <netdb.h>
#include <cstring>
#include <iostream>
#include "Pop3Task.h"
#include "../Converter.h"
#include "../Utils.h"

Pop3Task::Pop3Task() : m_socket(0), m_lastUpdateTime(0), m_serverAddrInfo(NULL) {
}

Pop3Task::~Pop3Task() {
    freeaddrinfo(m_serverAddrInfo);
}

int Pop3Task::Init(const std::string &server, const std::string &login, const std::string &pass) {
    Log("Init");
    m_server = server;
    m_login = login;
    m_pass = pass;

    addrinfo hints;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(m_server.c_str(), "pop3", &hints, &m_serverAddrInfo);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    return 0;
}

int Pop3Task::CreateSocket() {
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

int Pop3Task::Auth() {
    Log("Auth");

    std::string answer;
    int code = Recv(answer, "\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Bad answer from server");
        return -1;
    }

    code = Send("USER " + m_login + "\r\n");
    if(code < 0) {
        Log("Can't send user");
        return -1;
    }

    answer.clear();
    code = Recv(answer, "\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Server don't send answer for user");
        return -1;
    }

    code = Send("PASS " + m_pass + "\r\n");
    if(code < 0) {
        Log("Can't send pass");
        return -1;
    }

    answer.clear();
    code = Recv(answer, "\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Server don't send answer for pass");
        return -1;
    }

    return 0;
}

int Pop3Task::Update() {
    Log("Update");
    int code = CreateSocket();
    if (code < 0) {
        Log("Can't create socket");
        return -1;
    }

    code = Auth();
    if(code < 0) {
        Log("Can't auth");
        close(m_socket);
        return -1;
    }

    code = Send("LIST\r\n");
    if(code < 0) {
        Log("Can't send LIST");
        close(m_socket);
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n.\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Can't recv LIST");
        close(m_socket);
        return -1;
    }

    code = ProcessList(answer);
    if(code < 0) {
        Log("Can't process LIST");
        close(m_socket);
        return -1;
    }

    code = Logout();
    if(code < 0) {
        Log("Can't logout");
        close(m_socket);
        return -1;
    }

    close(m_socket);
    return 0;
}

int Pop3Task::ProcessList(const std::string &data) {
    Log("Process LIST");
    time_t lastTime = m_lastUpdateTime;
    size_t index = 0;
    while(1) {
        index = data.find("\r\n", index);
        if(index == std::string::npos) {
            break;
        }

        index += strlen("\r\n");

        size_t spacePos = data.find(" ", index);
        if(spacePos == std::string::npos) {
            break;
        }

        std::string numStr = data.substr(index, spacePos - index);
        int messageIndex = Converter::convert(numStr);
        Log("Message index: " + Converter::convert(messageIndex));

        time_t messageTime = ProcessMessage(messageIndex);
        if(messageTime < 0) {
            return -1;
        }

        lastTime = std::max(lastTime, messageTime);
    }

    Log("New last update time: " + Converter::convert(lastTime));
    m_lastUpdateTime = lastTime;

    return 0;
}

time_t Pop3Task::ProcessMessage(int index) {
    Log("Process message");
    int code = Send("TOP " + Converter::convert(index) + " 0\r\n");
    if(code < 0) {
        Log("Can't send TOP");
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n.\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Can't recv TOP");
        return -1;
    }

    time_t messageTime = GetReceivedTime(answer);
    Log("Message time: " + Converter::convert(messageTime));

    if(messageTime <= m_lastUpdateTime) {
        Log("It's old message");
        return 0;
    }
    Log("It's new message");

    code = DownloadMessage(index);
    if(code < 0) {
        Log("Can't download messsage");
        return -1;
    }

    return messageTime;
}

int Pop3Task::DownloadMessage(int index) {
    Log("Download message");
    int code = Send("RETR " + Converter::convert(index) + "\r\n");
    if(code < 0) {
        Log("Can't send RETR");
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n.\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Can't recv RETR");
        return -1;
    }

    std::cout << "\n-----------------\nNew pop3 message for "
              << m_login << ":\n" << answer << "\n--------------------" << std::endl;

    return 0;
}

int Pop3Task::Logout() {
    Log("Logout");
    int code = Send("QUIT\r\n");
    if(code < 0) {
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n");
    if(code < 0 || answer[0] != '+') {
        return -1;
    }

    close(m_socket);
    m_socket = -1;

    return 0;
}

int Pop3Task::Send(const std::string &msg) {
    return Utils::Send(m_socket, msg);
}

int Pop3Task::Recv(std::string &res, const std::string &endSymbols) {
    return Utils::Recv(m_socket, res, endSymbols);
}

time_t Pop3Task::GetReceivedTime(const std::string &data) {
    size_t index = 0;
    time_t date = -1;

    while(true) {
        index = data.find("Received:", index);
        if (index == std::string::npos) {
            break;
        }

        if(index != 0 && data[index - 1] != '\n') {
            break;
        }

        index = data.find(";", index);
        if (index == std::string::npos) {
            break;
        }
        index += strlen(";");

        index = data.find(", ", index);
        if (index == std::string::npos) {
            break;
        }
        index += strlen(", ");

        size_t newLinePos = data.find("\r\n", index);
        if (newLinePos == std::string::npos) {
            break;
        }

        std::string dateStr = data.substr(index, newLinePos - index);

        date = std::max(date, Converter::convertDate(dateStr));
    }

    return date;
}

void Pop3Task::Log(const std::string &msg) {
    printf("Pop3 (%s): %s\n", m_login.c_str(), msg.c_str());
}

int Pop3Task::StartSession() {
    return 0;
}

int Pop3Task::EndSession() {
    return 0;
}

int Pop3Task::Noop() {
    return 0;
}


