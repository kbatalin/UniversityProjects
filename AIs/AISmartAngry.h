#ifndef _DILEMMA_AISMARTANGRY_H_
#define _DILEMMA_AISMARTANGRY_H_

#include <array>

#include "AI.h"

class AISmartAngry final : public AI::AIBase {
public:
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override;
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};

private:
    bool angryMode = false;
};


#endif //_DILEMMA_AISMARTANGRY_H_
