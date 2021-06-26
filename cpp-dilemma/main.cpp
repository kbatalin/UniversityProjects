#include <cstring>
#include <iostream>
#include <stdexcept>
#include <vector>

#include "boost/program_options.hpp"
#include "boost/exception/all.hpp"
#include "Game.h"
#include "Register.h"
#include "Factory.h"

bool defaultInit(std::string &mode, std::vector<std::string> &strategy, std::string &configsPath, std::string &matrixFile) {
    if(strategy.size() < 3) {
        std::cerr << "Enough players" << std::endl;
        return false;
    }
    if(mode.empty()) {
        if(strategy.size() == 3) {
            mode = "detailed";
        } else {
            mode = "tournament";
        }
    }
    if(matrixFile.empty()) {
        matrixFile = "config.txt";
    }
    if(configsPath.empty()) {
        configsPath = ".";
    }
    return true;
}

bool parseParam(int argc, const char * const *argv, std::string &mode, std::vector<std::string> &strategy,
                std::string &configsPath, std::string &matrixFile, unsigned &steps) {
    boost::program_options::options_description description("Prisoner's dilemma");
    description.add_options()
            ("help,h", "Display this help message")
            ("mode,m", boost::program_options::value<std::string>(&mode), "Game mode")
            ("steps,s", boost::program_options::value<unsigned>(&steps), "Count steps in tournament and fast mode")
            ("configs,c", boost::program_options::value<std::string>(&configsPath), "Path of configs for bots")
            ("matrix,d", boost::program_options::value<std::string>(&matrixFile), "Matrix of game")
            ("bots", boost::program_options::value<std::vector<std::string>>(&strategy)->required(), "Bots for game");

    boost::program_options::positional_options_description p;
    p.add("bots", -1);
    boost::program_options::variables_map vm;

    try {
        boost::program_options::store(
                boost::program_options::command_line_parser(argc, argv).options(description).positional(p).run(), vm);
    } catch (boost::exception &e) {
        std::cerr << boost::diagnostic_information(e);
    }
    boost::program_options::notify(vm);

    if(vm.count("help")) {
        std::cout << description << std::endl;
    }

    return defaultInit(mode, strategy, configsPath, matrixFile);
}

int main(int argc, const char * const *argv) {
    unsigned steps = 0;
    std::string configsPath;
    std::string matrixFile;
    std::vector<std::string> strategy;
    std::string mode;

    if(!parseParam(argc, argv, mode, strategy, configsPath, matrixFile, steps)) {
        return -1;
    }

    try {
        Register<std::string, std::string>::instance()->add("configsPath", configsPath);
        auto game = Factory<std::string, Game, std::function<Game*()>>::instance()->create(mode);
        game->run(strategy, matrixFile, steps);
    } catch (std::runtime_error &e) {
        std::cout << e.what() << std::endl;
        return -1;
    } catch (std::out_of_range &e) {
        std::cout << "Invalid mode or strategy" << std::endl;
        return -1;
    } catch (...) {
        std::cout << "Unknown error" << std::endl;
        return -1;
    }

    return 0;
}