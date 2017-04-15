#include "Interface.h"

#include <iostream>
#include <list>
#include <utility>

void Interface::printDelim(unsigned length) {
    std::string str(length, '-');
    std::cout << str << std::endl;
}

void Interface::printStep(const std::vector<std::string> &strategyNames, const std::array<int, 3> &matrix,
                      const std::vector<int> &results) {
    for(int i = 0; i < 3; ++i) {
        std::cout << strategyNames[i] << (matrix[i] == AI::cooperates ? " cooperates" : " defects")
        << " and receives " << matrix[i] << " point(s). (Now has " << results[i] << ")" << std::endl;
    }

    printDelim();
}

unsigned Interface::getTicks() {
    std::cout << "Enter tick <n> (without <>) or quit: ";
    std::string str;
    int steps = 0;
    unsigned long tmp = 0;

    for(bool needSteps = true; needSteps;) {
        std::getline(std::cin, str);
        if (str.find("quit") != std::string::npos) {
            steps = 0;
            needSteps = false;
        } else if( (tmp = str.find("tick")) != std::string::npos) {
            if(str.size() == 4 + tmp) {
                steps = 1;
                needSteps = false;
            } else {
                try {
                    steps = std::stoi(str.substr(tmp + 4));
                    if(steps < 1) {
                        std::cout << "Ticks must be >0" << std::endl;
                        continue;
                    }
                    needSteps = false;
                } catch (...) {
                    std::cout << "<n> must be int" << std::endl;
                }
            }
        } else {
            std::cout << "Wrong format" << std::endl;
        }
    }
    return static_cast<unsigned>(steps);
}

void Interface::printResult(const std::vector<std::string> &names, const std::vector<int> &points) {
    std::list<std::pair<std::string, int>> result;

    for(unsigned long i = 0, size = points.size(); i < size; ++i) {
        result.push_back({names[i], points[i]});
    }

    result.sort([](const std::pair<std::string, int> &a, const std::pair<std::string, int> &b){return a.second > b.second;});

    std::cout << "Final result:" << std::endl;
    int i = 0;
    for (const auto &item : result) {
        std::cout << "#" << (i + 1) << " " << item.first << " has " << item.second << " point(s)." << std::endl;
        ++i;
    }
}

void Interface::printTournamentStart(const std::vector<std::string> &strategy, int steps) {
    std::cout << "Tournament is starting. We have " << strategy.size() << " contenders. In round "
    << steps << " steps." << std::endl << std::endl;
}

void Interface::printTournamentRoundStart(int round, const std::string &first, const std::string &second,
                                      const std::string &third) {
    std::cout << "Round " << round << ". Participants: " << first << ", " << second << ", "
    << third << std::endl;
}

unsigned Interface::getTournamentSteps() {
    std::cout << "How many steps per round? ";
    int steps = 0;

    for(bool needSteps = true; needSteps;) {
        std::cin >> steps;
        if(steps < 1) {
            std::cout << "Steps must be >0" << std::endl;
        } else {
            needSteps = false;
        }
    }
    return static_cast<unsigned>(steps);
}
