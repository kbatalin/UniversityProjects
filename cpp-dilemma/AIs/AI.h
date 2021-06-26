#ifndef _DILEMMA_AI_H_
#define _DILEMMA_AI_H_

#include <array>
#include <initializer_list>
#include <vector>

namespace AI {
    enum DECISION {
        cooperates = 0,
        defects
    };

    class AIBase {
    public:
        virtual DECISION step() = 0;

        virtual void otherSteps(const std::vector<DECISION> &steps) = 0;

        virtual void loadMatrix(const std::array<std::array<int, 3>, 8> &matrix) = 0;

        virtual ~AIBase() {};
    };
}

#endif //_DILEMMA_AI_H_