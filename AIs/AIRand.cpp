#include "AIRand.h"

#include <functional>

#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("Rand", []{ return new AIRand(); });
}

AI::DECISION AIRand::step() {
    std::uniform_int_distribution<> dist(AI::cooperates, AI::defects);
    return AI::DECISION(dist(rnd));
}
