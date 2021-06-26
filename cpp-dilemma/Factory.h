#ifndef _DILEMMA_FACTORY_H_
#define _DILEMMA_FACTORY_H_

#include <map>
#include <memory>
#include <utility>

template <class ID, class PRODUCT, class CREATOR>
class Factory final {
public:
    static Factory * instance() {
        static Factory factory;
        return  &factory;
    }

    bool addCreator(const ID &id, const CREATOR &cr) {
        return creators_.insert(std::make_pair(id, cr)).second;
    }

    std::unique_ptr<PRODUCT> create(const ID &id) {
        auto cr = creators_.at(id);
        return std::unique_ptr<PRODUCT> (cr());
    }

private:
    Factory() = default;
    Factory(const Factory &) = delete;
    Factory & operator=(Factory &) = delete;

    std::map<ID, CREATOR> creators_;
};

#endif //_DILEMMA_FACTORY_H_
