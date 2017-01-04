#include <iostream>
#include <netdb.h>
#include <cstring>
#include "Poller.h"
#include "TestTask.h"
#include "tasks/Pop3Task.h"

//void fullSend(int socket, const char *str, int length) {
//    while (length > 0) {
//        int size = send(socket, str, length, 0);
//        if (size <= 0) {
//            return;
//        }
//
//        length -= size;
//        str += size;
//    }
//}
//
//void fullRecv(int socket) {
//    char buffer[10000];
//    int size = recv(socket, buffer, 10000, 0);
//    printf("%.*s\n", size, buffer);
//}
//
//void stringSend(int socket, std::string str) {
//    fullSend(socket, str.c_str(), str.length());
//}
//
//void pop3() {
//    addrinfo hints, *res;
//    memset(&hints, 0, sizeof hints);
//    hints.ai_family = AF_UNSPEC;
//    hints.ai_socktype = SOCK_STREAM;
//    hints.ai_flags = AI_PASSIVE;
//
//    getaddrinfo("fit-fija.ru", "pop3", &hints, &res);
//
//    int m_socket = socket(res->ai_family, res->ai_socktype, res->ai_protocol);
//
//    int val = 1;
//    setsockopt(m_socket, SOL_SOCKET, SO_REUSEADDR, &val, sizeof(val));
//
//    connect(m_socket, res->ai_addr, res->ai_addrlen);
//    freeaddrinfo(res);
//
//    fullRecv(m_socket);
//
//    stringSend(m_socket, "USER mail1@fit-fija.ru\n");
//
//    fullRecv(m_socket);
//
//    stringSend(m_socket, "PASS 28cbvcbvjnrhjqcz\n");
//
//    fullRecv(m_socket);
//
//    stringSend(m_socket, "LIST\n");
//    fullRecv(m_socket);
//
//    stringSend(m_socket, "TOP 1 0\n");
//    fullRecv(m_socket);
//
//    //date -d "Thu, 29 Dec 2016 23:34:31" "+%s"
//}

int main() {
    Poller poller;

//    auto testTask = std::make_shared<TestTask>();
//    testTask->StartSession();
//    poller.ScheduleTask(testTask, 5000, 1000);

    auto pop3Task1 = std::make_shared<Pop3Task>();
    pop3Task1->Init("fit-fija.ru", "mail1@fit-fija.ru", "stupidpass123");
    poller.ScheduleTask(pop3Task1, 5000, 1000);

    poller.Run();

    return 0;
}