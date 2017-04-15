#include "AITitForTat.h"

#include <functional>
#include <iostream>

#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("TitForTat", []{ return new AITitForTat(); });
}

AI::DECISION AITitForTat::step() {
    for (const auto &step : lastStep) {
        if(step == AI::defects) {
            return AI::defects;
        }
    }

    return AI::cooperates;
}

void AITitForTat::otherSteps(const std::vector<AI::DECISION> &steps) {
    lastStep = steps;
}
