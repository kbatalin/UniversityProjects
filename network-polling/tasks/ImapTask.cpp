//
// Created by kir55rus on 06.01.17.
//

#include <cstring>
#include <unistd.h>
#include <iostream>
#include "ImapTask.h"
#include "../Utils.h"
#include "../Converter.h"

ImapTask::ImapTask() : m_serverAddrInfo(NULL), m_socket(0), m_cmdNumber(0) {

}

ImapTask::~ImapTask() {
    freeaddrinfo(m_serverAddrInfo);
}

int ImapTask::Init(const std::string &server, const std::string &login, const std::string &pass,
                   const std::string &postbox) {
    Log("Init");
    m_server = server;
    m_login = login;
    m_pass = pass;
    m_postbox = postbox;

    addrinfo hints;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;

    int code = getaddrinfo(m_server.c_str(), "imap", &hints, &m_serverAddrInfo);
    if (code == -1) {
        perror("Can't get addr info");
        return -1;
    }

    return 0;
}

int ImapTask::StartSession() {
    Log("Start session");
    m_socket = socket(m_serverAddrInfo->ai_family, m_serverAddrInfo->ai_socktype, m_serverAddrInfo->ai_protocol);

    if (m_socket == -1) {
        perror("Can't open socket");
        return -1;
    }

    int code = connect(m_socket, m_serverAddrInfo->ai_addr, m_serverAddrInfo->ai_addrlen);
    if (code == -1) {
        perror("Can't connect");
        close(m_socket);
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n");
    if(code < 0 || answer.find("* OK") != 0) {
        Log("Can't recv init");
        close(m_socket);
        return -1;
    }

    code = Send(GetCmdId() + " LOGIN " + m_login + " " + m_pass + "\r\n");
    if(code < 0) {
        Log("Can't send LOGIN");
        close(m_socket);
        return -1;
    }

    answer.clear();
    code = Recv(answer, "\r\n");
    if(code < 0 || !IsGoodAnswer(answer)) {
        Log("Can't recv LOGIN");
        close(m_socket);
        return -1;
    }
    ++m_cmdNumber;

    return 0;
}

int ImapTask::Update() {
    Log("Update");
    int code = Send(GetCmdId() + " SELECT " + m_postbox + "\r\n");
    if(code < 0) {
        Log("Can't send SELECT");
        close(m_socket);
        return -1;
    }

    std::string answer;
    code = Recv(answer, ".\r\n");
    if(code < 0 || !IsGoodAnswer(answer)) {
        Log("Can't recv SELECT");
        close(m_socket);
        return -1;
    }
    ++m_cmdNumber;

    code = Send(GetCmdId() + " SEARCH RECENT\r\n");
    if(code < 0) {
        Log("Can't send SEARCH");
        close(m_socket);
        return -1;
    }

    answer.clear();
    code = Recv(answer, ".\r\n");
    if(code < 0 || !IsGoodAnswer(answer)) {
        Log("Can't recv SEARCH");
        close(m_socket);
        return -1;
    }
    ++m_cmdNumber;

    code = ProcessSearch(answer);
    if (code < 0) {
        Log("Can't process search");
        close(m_socket);
        return -1;
    }

    code = Send(GetCmdId() + " CLOSE\r\n");
    if(code < 0) {
        Log("Can't send CLOSE");
        close(m_socket);
        return -1;
    }

    answer.clear();
    code = Recv(answer, ".\r\n");
    if(code < 0 || !IsGoodAnswer(answer)) {
        Log("Can't recv CLOSE");
        close(m_socket);
        return -1;
    }
    ++m_cmdNumber;

    return 0;
}

int ImapTask::ProcessSearch(const std::string &data) {
    Log("Process search");

    size_t searchPos = data.find("* SEARCH");
    if(searchPos == std::string::npos) {
        return -1;
    }
    searchPos += strlen("* SEARCH");

    size_t newLinePos = data.find("\r\n", searchPos);

    if (newLinePos == searchPos) {
        Log("There aren't new messages");
        return 0;
    }

    ++searchPos;
    std::string messagesNumbers = data.substr(searchPos, newLinePos - searchPos) + " ";
    size_t prevSpacePos = 0;
    while (prevSpacePos != std::string::npos) {
        size_t spacePos = messagesNumbers.find(" ", prevSpacePos);
        if (spacePos == std::string::npos) {
            break;
        }

        std::string messageNumberStr = messagesNumbers.substr(prevSpacePos, spacePos - prevSpacePos);
        int messageNumber = Converter::convert(messageNumberStr);

        int code = ProcessMessage(messageNumber);
        if (code < 0) {
            return -1;
        }

        prevSpacePos = spacePos + 1;
    }

    return 0;
}

int ImapTask::ProcessMessage(int messageNumber) {
    Log("Process message #" + Converter::convert(messageNumber));

    int code = Send(GetCmdId() + " FETCH " + Converter::convert(messageNumber) + " BODY[]\r\n");
    if (code < 0) {
        Log("Can't send FETCH");
        return -1;
    }

    std::string answer;
    code = Recv(answer, "\r\n)\r\n");
    if (code < 0 || !IsGoodAnswer(answer)) {
        Log("Can't recv FETCH");
        return -1;
    }

    std::cout << "----------\nNew message:\n" << answer << "\n---------------------" << std::endl;

    return 0;
}


int ImapTask::Noop() {
    return 0;
}

int ImapTask::EndSession() {
    return 0;
}

void ImapTask::Log(const std::string &msg) {
    printf("Imap (%s): %s\n", m_login.c_str(), msg.c_str());
}

int ImapTask::Send(const std::string &msg) {
    return Utils::Send(m_socket, msg);
}

int ImapTask::Recv(std::string &res, const std::string &endSymbols) {
    return Utils::Recv(m_socket, res, endSymbols);
}

std::string ImapTask::GetCmdId() {
    return "a" + Converter::convert(m_cmdNumber);
}

bool ImapTask::IsGoodAnswer(const std::string &answer) {
    return answer.find(GetCmdId() + " OK ") != std::string::npos;
}

