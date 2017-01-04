//
// Created by kir55rus on 03.01.17.
//

#include <chrono>
#include <iostream>
#include "Poller.h"

void Poller::Run() {
    long prevTime = GetCurrentMs();
    while(m_tasks.size() > 0) {
        long currentTime = GetCurrentMs();
        long dtMs = currentTime - prevTime;
        prevTime = currentTime;

        PerformTasks(dtMs);
    }
}

void Poller::PerformTasks(long dtMs) {
    for(auto it = m_tasks.begin(); it != m_tasks.end();) {
        auto &taskData = *it;

        taskData.timeToUpdate -= dtMs;

        if(taskData.timeToUpdate > 0) {
            ++it;
            continue;
        }

        taskData.timeToUpdate = taskData.updateTimeout;

        int result = taskData.task->StartSession();
        if(result < 0) {
            it = m_tasks.erase(it);
            continue;
        }

        result = taskData.task->Update();
        if(result < 0) {
            it = m_tasks.erase(it);
            continue;
        }

        result = taskData.task->EndSession();
        if(result < 0) {
            it = m_tasks.erase(it);
            continue;
        }

        ++it;
    }
}

void Poller::ScheduleTask(std::shared_ptr<ITask> task, long updateTimeout) {
    TaskData taskData;

    taskData.updateTimeout = updateTimeout;
    taskData.timeToUpdate = 0;

    taskData.task = task;

    m_tasks.push_back(taskData);
}

long Poller::GetCurrentMs()
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
}


