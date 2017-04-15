#ifndef _DILEMMA_GAME_H_
#define _DILEMMA_GAME_H_

#include <array>
#include <memory>
#include <string>
#include <vector>

#include "AIs/AI.h"

class Game {
public:
    virtual void run(const std::vector<std::string> & strategy, const std::string & matrixFile, unsigned steps) = 0;
    virtual const std::vector<int> &results() const;
    virtual ~Game() {};

protected:
    std::vector<std::unique_ptr<AI::AIBase>> strategy_;
    std::vector<std::string> strategyNames_;
    std::array<std::array<int, 3>, 8> matrix_;
    std::vector<int> results_;

    virtual void init(const std::string &matrixFile, const std::vector<std::string> &strategy);
    virtual int getMatrixLine(const std::vector<AI::DECISION> &players);
    virtual void sendStep(const std::vector<AI::DECISION> &steps) const;

private:
    void loadMatrix(const std::string & matrixFile);
    void loadStrategy(const std::vector<std::string> &strategy);
};

#endif //_DILEMMA_GAME_H_
