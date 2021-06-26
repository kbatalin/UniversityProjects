#ifndef _DILEMMA_AITITFORTAT_H_
#define _DILEMMA_AITITFORTAT_H_

#include <array>

#include "AI.h"

class AITitForTat final : public AI::AIBase {
public:
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override;
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};

private:
    std::vector<AI::DECISION> lastStep;
};


#endif //_DILEMMA_AITITFORTAT_H_
