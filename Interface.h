#ifndef _DILEMMA_INTERFACE_H_
#define _DILEMMA_INTERFACE_H_

#include <string>
#include <vector>
#include <array>

#include "AIs/AI.h"

class Interface {
public:
    static void printDelim(unsigned length = 15);
    static void printStep(const std::vector<std::string> &strategyNames, const std::array<int, 3> &matrix,
                          const std::vector<int> &results);
    static unsigned getTicks();
    static void printResult(const std::vector<std::string> &names, const std::vector<int> &points);

    static void printTournamentStart(const std::vector<std::string> &strategy, int steps);
    static void printTournamentRoundStart(int round, const std::string &first, const std::string &second,
                                          const std::string &third);
    static unsigned getTournamentSteps();
};


#endif //_DILEMMA_INTERFACE_H_
