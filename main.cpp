#include <iostream>
#include <netdb.h>
#include <cstring>
#include "Poller.h"
#include "TestTask.h"
#include "tasks/Pop3Task.h"

int main() {
    Poller poller;

//    auto testTask = std::make_shared<TestTask>();
//    testTask->StartSession();
//    poller.ScheduleTask(testTask, 5000, 1000);

    auto pop3Task1 = std::make_shared<Pop3Task>();
    pop3Task1->Init("fit-fija.ru", "mail1@fit-fija.ru", "stupidpass123");
    poller.ScheduleTask(pop3Task1, 5000);

    poller.Run();

    return 0;
}