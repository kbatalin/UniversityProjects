#include "AIPiBot.h"

#include <fstream>
#include <iostream>

#include "../Register.h"
#include "../Factory.h"

namespace {
    bool b = Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->addCreator("PiBot", []{ return new AIPiBot(); });
}

AIPiBot::AIPiBot() : disguise_(std::move(Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->create("SmartAngry"))),
slave_(std::move(Factory<std::string, AI::AIBase, std::function<AI::AIBase*()>>::instance()->create("Slave"))) {
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
}

AI::DECISION AIPiBot::step() {
    if(needCheck_) {
        return slave_->step();
    }
    if(withSlave_) {
        return AI::defects;
    }
    return disguise_->step();
}

void AIPiBot::otherSteps(const std::vector<AI::DECISION> &steps) {
    disguise_->otherSteps(steps);
    slave_->otherSteps(steps);
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
        if(equal[0] && equal[1]) {
            needCheck_ = false;
            withSlave_ = true;
        }
    }
    if(history_[0].size() > maxCodeLen_) {
        needCheck_ = false;
    }
}