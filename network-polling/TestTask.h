//
// Created by kir55rus on 04.01.17.
//

#ifndef LAB8_TESTTASK_H
#define LAB8_TESTTASK_H

#include <iostream>
#include "tasks/ITask.h"

class TestTask : public ITask {
public:
    int StartSession() override {
        std::cout << "StartSession" << std::endl;
        return 0;
    };

    int Update() override {
        std::cout << "Update" << std::endl;
        return 0;
    };

    int EndSession() override {
        std::cout << "EndSession" << std::endl;
        return 0;
    };
};

#endif //LAB8_TESTTASK_H
