#ifndef _DILEMMA_AIMATH_H_
#define _DILEMMA_AIMATH_H_

#include <array>
#include <vector>

#include "AI.h"

class AIMath final : public AI::AIBase{
public:
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override {};
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override;

private:
    bool angry_ = false;
};


#endif //_DILEMMA_AIMATH_H_
