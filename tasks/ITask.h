//
// Created by kir55rus on 03.01.17.
//

#ifndef LAB8_ITASK_H
#define LAB8_ITASK_H

class ITask {
public:
    virtual int StartSession() = 0;

    virtual int Noop() = 0;

    virtual int Update() = 0;

    virtual int EndSession() = 0;

    virtual ~ITask() {};
};

#endif //LAB8_ITASK_H
