#include "Game.h"

#include <fstream>
#include <iostream>
#include <list>

#include "Register.h"
#include "Factory.h"

void Game::init(const std::string &matrixFile, const std::vector<std::string> &strategy) {
    loadMatrix(matrixFile);
    loadStrategy(strategy);
}

void Game::loadMatrix(const std::string & matrixFile) {
    std::ifstream matrix;
    matrix.exceptions(std::ios::failbit | std::ios::badbit);

    try {
        matrix.open(matrixFile);

        if (!matrix.is_open()) {
            throw std::runtime_error("Matrix file not found");
        }

        for (int i = 0; i < 8; ++i) {
            if (matrix.eof()) {
                throw std::runtime_error("Matrix file is bad");
            }

            int index = 0;
            for (int j = 4; j != 0; j /= 2) {
                int n;
                matrix >> n;
                index += j * n;
            }

            for (int j = 0; j < 3; ++j) {

                matrix >> matrix_[index][j];
            }
        }
    } catch (std::ifstream::failure &e) {
        throw std::runtime_error("Matrix file damaged");
    }

    matrix.close();
}

void Game::loadStrategy(const std::vector<std::string> &strategy) {
    strategyNames_ = strategy;
    auto factory = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance();
    for (const auto &item : strategyNames_) {
        strategy_.push_back(factory->create(item));
        strategy_.back()->loadMatrix(matrix_);
    }
}

void Game::sendStep(const std::vector<AI::DECISION> &steps) const {
    strategy_[0]->otherSteps({steps[1], steps[2]});
    strategy_[1]->otherSteps({steps[0], steps[2]});
    strategy_[2]->otherSteps({steps[0], steps[1]});
//    for (const auto &strategy : strategy_) {
//        strategy->otherSteps(steps);
//    }
};


int Game::getMatrixLine(const std::vector<AI::DECISION> &players) {
    return 4 * players[0] + 2 *  players[1] + players[2];
}

const std::vector<int> & Game::results() const {
    return results_;
}