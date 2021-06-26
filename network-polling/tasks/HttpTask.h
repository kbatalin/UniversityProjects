//
// Created by kir55rus on 04.01.17.
//

#ifndef LAB8_HTTPTASK_H
#define LAB8_HTTPTASK_H

#include <string>
#include "ITask.h"

class HttpTask : public ITask {
public:
    HttpTask();
    ~HttpTask() override;

    int Init(const std::string &url, const std::string &downloadPath);

    int StartSession() override;

    int Update() override;

    int Noop() override;

    int EndSession() override;

private:
    using checksum_t = unsigned long long int;

    std::string     m_url;
    std::string     m_downloadPath;
    checksum_t      m_downloadedChecksum;
    addrinfo *      m_serverAddrInfo;
    int             m_socket;
    time_t          m_lastModifiedDate;

    int DownloadData();
    int CreateSocket();
    int Send(const std::string &msg);
    int Recv(std::string &res, const std::string &endSymbols = "");
    void Log(const std::string &msg);
    int SaveFile(const std::string &data);
    int SaveFile(const char *data, size_t size);

    static checksum_t GetChecksum(const std::string &data);
    static checksum_t GetChecksum(const char *data, size_t size);
    static time_t GetLastModifiedTime(const std::string &head);
};


#endif //LAB8_HTTPTASK_H
