#include "AIKind.h"

#include <functional>

#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("Kind", []{ return new AIKind(); });
}

AI::DECISION AIKind::step() {
    return AI::cooperates;
}
