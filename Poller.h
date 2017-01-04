//
// Created by kir55rus on 03.01.17.
//

#ifndef LAB8_POLLER_H
#define LAB8_POLLER_H


#include <memory>
#include <vector>
#include "tasks/ITask.h"

class Poller {
public:
    void Run();

    void ScheduleTask(std::shared_ptr<ITask> task, long updateTimeout);

private:
    struct TaskData {
        long timeToUpdate;
        long updateTimeout;

        std::shared_ptr<ITask> task;
    };

    std::vector<TaskData> m_tasks;

    void PerformTasks(long dtMs);

    static long GetCurrentMs();
};


#endif //LAB8_POLLER_H
