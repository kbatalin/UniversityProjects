#include "AIMath.h"

#include <fstream>
#include <functional>
#include <iostream>
#include <memory>

#include "../Factory.h"


namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("Math", []{ return new AIMath(); });
}

AI::DECISION AIMath::step() {
    if(angry_) {
        return AI::defects;
    }
    return AI::cooperates;
}

void AIMath::loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) {
    // points for C in (CCC + CCD + CDD) < points for D in (DDD + CCD + CDD)
    if(matrix[0][0] + matrix[1][0] + matrix[4][0] < matrix[7][0] + matrix[4][1] + matrix[1][2]) {
        angry_ = true;
    }
}
