#include "AISmartAngry.h"

#include <functional>
#include <iostream>

#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("SmartAngry", []{ return new AISmartAngry(); });
}

AI::DECISION AISmartAngry::step() {
    if(angryMode) {
        return AI::defects;
    }
    return AI::cooperates;
}

void AISmartAngry::otherSteps(const std::vector<AI::DECISION> &steps) {
    for (const auto &step : steps) {
        if(step == AI::defects) {
            angryMode = true;
            break;
        }
    }
}
