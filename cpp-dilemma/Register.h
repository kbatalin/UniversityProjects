#ifndef _DILEMMA_REGISTER_H_
#define _DILEMMA_REGISTER_H_

#include <map>
#include <utility>

template <class ID, class DATA>
class Register final {
public:
    static Register * instance() {
        static Register register_;
        return &register_;
    }

    const DATA & get(const ID &id) const {
        return data_.at(id);
    }

    bool add(const ID &id, const DATA &data) {
        return data_.insert(std::pair<ID, DATA>(id, data)).second;
    }

private:
    Register() = default;
    Register(const Register &) = delete;
    Register & operator=(const Register &) = delete;

    std::map<ID, DATA> data_;
};

#endif //_DILEMMA_REGISTER_H_
