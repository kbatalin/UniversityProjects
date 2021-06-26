//
// Created by kir55rus on 03.01.17.
//

#include <chrono>
#include <iostream>
#include "Poller.h"

void Poller::StartSessions() {
    for (auto it = m_tasks.begin(); it != m_tasks.end();) {
        int code = it->task->StartSession();
        if(code < 0) {
            it = m_tasks.erase(it);
        } else {
            ++it;
        }
    }
}

void Poller::Run() {
    StartSessions();

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
        taskData.timeToNoop -= dtMs;

        if(taskData.timeToUpdate <= 0 && UpdateTask(taskData) < 0) {
            it = m_tasks.erase(it);
            continue;
        } else if (taskData.noopTimeout > 0 && taskData.timeToNoop <= 0 && NoopTask(taskData) < 0) {
            it = m_tasks.erase(it);
            continue;
        }

        ++it;
    }
}

int Poller::UpdateTask(Poller::TaskData &taskData) {
    taskData.timeToUpdate = taskData.updateTimeout;
    taskData.timeToNoop = taskData.noopTimeout;

    int result = taskData.task->Update();
    if(result < 0) {
        return -1;
    }

    return 0;
}

int Poller::NoopTask(Poller::TaskData &taskData) {
    taskData.timeToNoop = taskData.noopTimeout;

    int result = taskData.task->Noop();
    if(result < 0) {
        return -1;
    }

    return 0;
}

void Poller::ScheduleTask(std::shared_ptr<ITask> task, long updateTimeout, long noopTimeout /*= 0*/) {
    TaskData taskData;

    taskData.updateTimeout = updateTimeout;
    taskData.timeToUpdate = 0;

    taskData.noopTimeout = noopTimeout;
    taskData.timeToNoop = 0;

    taskData.task = task;

    m_tasks.push_back(taskData);
}

long Poller::GetCurrentMs()
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
}



