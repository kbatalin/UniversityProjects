#include "FastGame.h"

#include <algorithm>
#include <iostream>
#include <list>

#include "Factory.h"
#include "Interface.h"

namespace {
    bool b = Factory<std::string, Game, std::function<Game*()>>::instance()->addCreator("fast", []{ return new FastGame(); });
}

void FastGame::run(const std::vector<std::string> &strategy, const std::string & matrixFile, unsigned steps) {
    init(matrixFile, strategy);

    results_.resize(3, 0);

    for (unsigned long i = 0; i < steps; ++i) {
        std::vector<AI::DECISION> res = {strategy_[0]->step(), strategy_[1]->step(), strategy_[2]->step()};
        int line = getMatrixLine(res);

        for(int j = 0; j < 3; ++j) {
            results_[j] += matrix_[line][j];
        }

        sendStep(res);
    }

    Interface::printResult(strategyNames_, results_);
}