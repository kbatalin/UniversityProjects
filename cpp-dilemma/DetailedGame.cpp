#include "DetailedGame.h"

#include <algorithm>
#include <iostream>

#include "Factory.h"
#include "Interface.h"

namespace {
    bool b = Factory<std::string, Game, std::function<Game*()>>::instance()->addCreator("detailed", []{ return new DetailedGame(); });
}

void DetailedGame::run(const std::vector<std::string> &strategy, const std::string &matrixFile, unsigned) {
    init(matrixFile, strategy);

    results_.resize(3);

    for(unsigned steps = 0; steps != 0 || (steps = Interface::getTicks()); --steps) {
        std::vector<AI::DECISION> res = {strategy_[0]->step(), strategy_[1]->step(), strategy_[2]->step()};
        int line = getMatrixLine(res);

        for(int i = 0; i < 3; ++i) {
            results_[i] += matrix_[line][i];
        }
        Interface::printStep(strategyNames_, matrix_[line], results_);

        sendStep(res);
    }

    Interface::printResult(strategyNames_, results_);
}