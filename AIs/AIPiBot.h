#ifndef _DILEMMA_AIPIBOT_H_
#define _DILEMMA_AIPIBOT_H_

#include <array>
#include <string>
#include <vector>
#include <memory>

#include "AI.h"

class AIPiBot final : public AI::AIBase {
public:
    AIPiBot();
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override;
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};

private:
    const std::string configFile = "PiBot.txt";
    std::vector<std::string> codes_;
    std::vector<std::string> history_;
    std::unique_ptr<AI::AIBase> disguise_;
    std::unique_ptr<AI::AIBase> slave_;
    bool needCheck_ = true;
    bool withSlave_ = false;
    unsigned long maxCodeLen_ = 0;
};


#endif //_DILEMMA_AIPIBOT_H_
