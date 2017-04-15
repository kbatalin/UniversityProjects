#ifndef _DILEMMA_DETAILEDGAME_H_
#define _DILEMMA_DETAILEDGAME_H_

#include <string>
#include <vector>

#include "Game.h"

class DetailedGame final : public Game {
public:
    void run(const std::vector<std::string> &strategy, const std::string &matrixFile, unsigned steps) override;
};


#endif //_DILEMMA_DETAILEDGAME_H_
