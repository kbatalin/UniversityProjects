#ifndef _DILEMMA_AIANGRY_H_
#define _DILEMMA_AIANGRY_H_

#include <array>

#include "AI.h"

class AIAngry final : public AI::AIBase {
public:
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override {};
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};
};


#endif //_DILEMMA_AIANGRY_H_
