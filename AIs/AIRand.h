#ifndef _DILEMMA_AIRAND_H_
#define _DILEMMA_AIRAND_H_

#include <array>
#include <random>

#include "AI.h"

class AIRand final : public AI::AIBase{
public:
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override {};
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};

private:
    std::random_device rnd;
};


#endif //_DILEMMA_AIRAND_H_
