//
// Created by kir55rus on 06.01.17.
//

#ifndef LAB8_IMAPTASK_H
#define LAB8_IMAPTASK_H

#include <string>
#include <netdb.h>
#include "ITask.h"

class ImapTask : public ITask {
public:
    ImapTask();
    ~ImapTask() override;

    int Init(const std::string &server, const std::string &login, const std::string &pass, const std::string &postbox);

    int StartSession() override;

    int Update() override;

    int Noop() override;

    int EndSession() override;

private:
    std::string     m_server;
    std::string     m_login;
    std::string     m_pass;
    std::string     m_postbox;
    addrinfo *      m_serverAddrInfo;
    int             m_socket;
    int             m_cmdNumber;

    int ProcessSearch(const std::string &data);
    int ProcessMessage(int messageNumber);
    std::string GetCmdId();
    int Send(const std::string &msg);
    int Recv(std::string &res, const std::string &endSymbols);
    void Log(const std::string &msg);
    bool IsGoodAnswer(const std::string &answer);
};


#endif //LAB8_IMAPTASK_H
