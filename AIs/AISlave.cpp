#include "AISlave.h"

#include <fstream>
#include <functional>
#include <iostream>
#include <random>

#include "../Register.h"
#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("Slave", []{ return new AISlave(); });
}

AISlave::AISlave() {
    history_.resize(2);
    std::string path = Register<std::string, std::string>::instance()->get("configsPath");
    std::ifstream in;
    in.exceptions(std::ios::failbit | std::ios::badbit);
    try {
        in.open(path + "/" + configFile);
        if (!in.is_open()) {
            throw std::runtime_error("PiBot's config file not found");
        }
        while (!in.eof()) {
            std::string code;
            in >> code;
            if (!code.empty()) {
                codes_.push_back(code);
                if (maxCodeLen_ < code.size()) {
                    maxCodeLen_ = static_cast<int>(code.size());
                }
            }
        }
    } catch (std::ifstream::failure &e) {
        throw std::runtime_error("PiBot's config file damaged");
    }
    in.close();

    if(0 == codes_.size()) {
        throw std::runtime_error("PiBot's config file damaged");
    }
    std::random_device rnd;
    std::uniform_int_distribution<> dist(0, static_cast<int>(codes_.size() - 1));
    activeCode_ = dist(rnd);
}

AI::DECISION AISlave::step() {
    if(needCheck_) {
        return (codes_[activeCode_][index_++] == '0' ? AI::cooperates : AI::defects);
    }
    if(withLord_) {
        return AI::cooperates;
    }
    return AI::defects;
}

void AISlave::otherSteps(const std::vector<AI::DECISION> &steps) {
    if(needCheck_) {
        history_[0] += steps[0] ? '1' : '0';
        history_[1] += steps[1] ? '1' : '0';

        bool equal[2] = {false, false};
        for (const auto &code : codes_) {
            if(code == history_[0]) {
                equal[0] = true;
            } else if(code == history_[1]) {
                equal[1] = true;
            }
        }
        if(equal[0] || equal[1]) {
            needCheck_ = false;
            withLord_ = true;
        }
    }
    if(history_[0].size() > maxCodeLen_) {
        needCheck_ = false;
    }
}