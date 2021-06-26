#ifndef _DILEMMA_AISLAVE_H_
#define _DILEMMA_AISLAVE_H_

#include <array>
#include <string>
#include <vector>

#include "AI.h"

class AISlave final : public AI::AIBase {
public:
    AISlave();
    AI::DECISION step() override;
    void otherSteps(const std::vector<AI::DECISION> &steps) override;
    void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) override {};

private:
    const std::string configFile = "PiBot.txt";
    int activeCode_;
    unsigned maxCodeLen_ = 0;
    std::vector<std::string> codes_;
    unsigned long index_ = 0;
    bool withLord_ = false;
    bool needCheck_ = true;
    std::vector<std::string> history_;
};


#endif //_DILEMMA_AISLAVE_H_
