#ifndef _DILEMMA_FASTGAME_H_
#define _DILEMMA_FASTGAME_H_

#include <string>
#include <vector>

#include "Game.h"

class FastGame final : public Game {
public:
    void run(const std::vector<std::string> &strategy, const std::string &matrixFile, unsigned steps) override;
};


#endif //_DILEMMA_FASTGAME_H_
