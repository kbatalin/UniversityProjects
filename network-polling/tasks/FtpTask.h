//
// Created by kir55rus on 05.01.17.
//

#ifndef LAB8_FTPTASK_H
#define LAB8_FTPTASK_H

#include <string>
#include <netdb.h>
#include "ITask.h"

class FtpTask : public ITask {
public:
    FtpTask();
    ~FtpTask() override;

    int Init(const std::string &server, const std::string &path, const std::string &login, const std::string &pass, const std::string &downloadPath);

    int StartSession() override;

    int Update() override;

    int Noop() override;

    int EndSession() override;

private:
    const static int BUFFER_SIZE = 1024;

    int             m_cmdSocket;
    std::string     m_server;
    std::string     m_path;
    std::string     m_login;
    std::string     m_pass;
    std::string     m_downloadPath;
    addrinfo *      m_cmdServerAddrInfo;
    long            m_lastModifiedTime;

    int DownloadFile();
    int SaveFile(int dataSocket);
    void Log(const std::string &msg);
    static int CreateDataSocket(const std::string &data);
    static long GetLastModifiedTime(const std::string &data);
};


#endif //LAB8_FTPTASK_H
