//
// Created by kir55rus on 04.01.17.
//

#include <unistd.h>
#include <netdb.h>
#include <cstring>
#include <iostream>
#include "Pop3Task.h"
#include "../Converter.h"

Pop3Task::Pop3Task() : m_socket(-1), m_lastUpdateTime(0) {
}

Pop3Task::~Pop3Task() {
    if (m_socket >= 0) {
        close(m_socket);
    }
}

int Pop3Task::Init(const std::string &server, const std::string &login, const std::string &pass) {
    Log("Init");
    m_server = server;
    m_login = login;
    m_pass = pass;

    return 0;
}

int Pop3Task::CreateSocket() {
    addrinfo hints, *res;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(m_server.c_str(), "pop3", &hints, &res);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    m_socket = socket(res->ai_family, res->ai_socktype, res->ai_protocol);

    if (m_socket == -1) {
        freeaddrinfo(res);
        perror("Can't open socket");
        return -1;
    }

    code = connect(m_socket, res->ai_addr, res->ai_addrlen);
    freeaddrinfo(res);

    if (code == -1) {
        perror("Can't connect");
        return -1;
    }

    return 0;
}

int Pop3Task::StartSession() {
    Log("Start session");
    int code = CreateSocket();
    if (code < 0) {
        Log("Can't create socket");
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n");
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
    int code = Send("LIST\r\n");
    if(code < 0) {
        Log("Can't send LIST");
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n.\r\n");
    if(code < 0 || answer[0] != '+') {
        Log("Can't recv LIST");
        return -1;
    }

    code = ProcessList(answer);
    if(code < 0) {
        Log("Can't process LIST");
        return -1;
    }

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

int Pop3Task::EndSession() {
    Log("End session");
    int code = Send("QUIT\r\n");
    if(code < 0) {
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n");
    if(code < 0 || answer[0] != '+') {
        return -1;
    }

    return 0;
}

int Pop3Task::Send(const std::string &msg) {
    const char *ptr = msg.c_str();
    int length = msg.size();

    while(length > 0) {
        int code = send(m_socket, ptr, length, MSG_NOSIGNAL);
        if(code <= 0) {
            return -1;
        }

        length -= code;
        ptr += code;
    }

    return 0;
}

int Pop3Task::Recv(std::string &res, const std::string &endSymbols) {
    while(true) {
        char buffer[BUFFER_SIZE] = {};
        int code = recv(m_socket, buffer, BUFFER_SIZE, MSG_NOSIGNAL);
        if(code <= 0) {
            return -1;
        }

        std::string newData(buffer, code);
        res.append(newData);

        if(newData.rfind(endSymbols) != std::string::npos) {
            return 0;
        }
    }
}

time_t Pop3Task::GetReceivedTime(const std::string &data) {
    size_t index = data.find("Received: by ");
    if (index == std::string::npos) {
        return -1;
    }

    index = data.find(", ", index);
    if (index == std::string::npos) {
        return -1;
    }
    index += strlen(", ");

    size_t newLinePos = data.find("\r\n", index);
    if (newLinePos == std::string::npos) {
        return -1;
    }

    std::string dateStr = data.substr(index, newLinePos - index);

    time_t date = Converter::convertDate(dateStr);

    return date;
}

void Pop3Task::Log(const std::string &msg) {
    printf("Pop3 (%s): %s\n", m_login.c_str(), msg.c_str());
}


