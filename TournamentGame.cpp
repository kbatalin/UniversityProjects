#include "TournamentGame.h"

#include <algorithm>
#include <iostream>

#include "Factory.h"
#include "Interface.h"

namespace {
    bool b = Factory<std::string, Game, std::function<Game*()>>::instance()->addCreator("tournament", []{ return new TournamentGame(); });
}

void TournamentGame::run(const std::vector<std::string> &strategy, const std::string &matrixFile, unsigned steps) {
    if(0 == steps) {
        steps = Interface::getTournamentSteps();
    }

    Interface::printTournamentStart(strategy, steps);

    results_.resize(strategy.size(), 0);
    for(int i = 0, size = static_cast<int>(strategy.size()), round = 1; i < size - 2; ++i) {
        for(int j = i + 1; j < size - 1; ++j) {
            for(int q = j + 1; q < size; ++q, ++round) {
                Interface::printTournamentRoundStart(round, strategy[i], strategy[j], strategy[q]);

                auto tour = Factory<std::string, Game, std::function<Game*()>>::instance()->create("fast");
                tour->run({strategy[i], strategy[j], strategy[q]}, matrixFile, steps);
                auto res = tour->results();
                results_[i] += res[0];
                results_[j] += res[1];
                results_[q] += res[2];

                Interface::printDelim();
            }
        }
    }

    Interface::printResult(strategy, results_);
}
