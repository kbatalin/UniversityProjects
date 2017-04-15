#include "AIAngry.h"

#include <functional>
#include <iostream>

#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("Angry", []{ return new AIAngry(); });
}

AI::DECISION AIAngry::step() {
    return AI::defects;
}
