//
// Created by kir55rus on 04.01.17.
//

#ifndef LAB8_POP3TASK_H
#define LAB8_POP3TASK_H

#include <string>
#include "ITask.h"

class Pop3Task : public ITask {
public:
    Pop3Task();
    ~Pop3Task() override;

    int Init(const std::string &server, const std::string &login, const std::string &pass);

    int StartSession() override;

    int Update() override;

    int EndSession() override;

private:
    const static int BUFFER_SIZE = 1024;

    std::string     m_server;
    std::string     m_login;
    std::string     m_pass;
    int             m_socket;
    time_t          m_lastUpdateTime;
    addrinfo *      m_serverAddrInfo;

    int ProcessList(const std::string &data);
    time_t ProcessMessage(int index);
    int DownloadMessage(int index);
    int CreateSocket();
    int Send(const std::string &msg);
    int Recv(std::string &res, const std::string &endSymbols);
    void Log(const std::string &msg);

    static time_t GetReceivedTime(const std::string &data);
};


#endif //LAB8_POP3TASK_H
