<?php

class Login
{
    private $_lastError;

    public function auth($login, $pass)
    {
        $config = Config::getInstance();

        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id`, `pass` FROM `users` WHERE `login`=? LIMIT 1");
        $STH->bindValue(1, $login, PDO::PARAM_STR);
        if (!$STH->execute()) {
            Logger::logMessage("Can\'t select user " . $login);
            $this->_lastError = 'Внутренняя ошибка';
            return false;
        }

        $STH->setFetchMode(PDO::FETCH_ASSOC);
        $res = $STH->fetch();

        if (!$res) {
            Logger::logMessage("Bad login. There isn't user with login " . $login);
            $this->_lastError = 'Неверный логин';
            return false;
        }

        $salt = mb_substr($res['pass'], 0, 29);
        $passHash = crypt($pass, $salt);

        if (strcmp($res['pass'], $passHash) !== 0) {
            Logger::logMessage("Bad login. Bad pass for login " . $login);
            $this->_lastError = 'Неверный пароль';
            return false;
        }

        $randomHash = Random::generateString($config->getSettings('loginHashLength'));
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `auth_hash`=? WHERE `id`=?");
        $STH->bindValue(1, $randomHash, PDO::PARAM_STR);
        $STH->bindValue(2, $res['id']);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t update user\'s auth hash. User #' . $res['id']);
            $this->_lastError = 'Внутренняя ошибка. Попробуйте позже';
            return false;
        }

        setcookie('user', $res['id'], time() + $config->getSettings('authTimeSec'), '/', $config->getSettings('domain'), false, true);
        setcookie('hash', $randomHash, time() + $config->getSettings('authTimeSec'), '/', $config->getSettings('domain'), false, true);

        Logger::logMessage('Success login. User ' . $res['id']);

        $user = new User();
        $user->init($res['id']);
        App::getInstance()->put('user', $user);

        return true;
    }

    public function checkAuth()
    {
        if (!isset($_COOKIE['user']) || !isset($_COOKIE['hash'])) {
            $this->_lastError = 'Нет данных об авторизации';
            return false;
        }

        $userId = trim($_COOKIE['user']);
        $hash = trim($_COOKIE['hash']);

        if (empty($userId) || empty($hash)) {
            $this->_lastError = 'Неверные данные об авторизации';
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id`, `auth_hash` FROM `users` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $userId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select auth key for user #' . $userId);
            $this->_lastError = 'Внутренняя ошибка. Попробуйте позже';
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            $this->_lastError = 'Неверные данные об авторизации';
            return false;
        }

        if (strcmp($hash, $res['auth_hash']) !== 0) {
            $this->_lastError = 'Неверные данные об авторизации';
            return false;
        }

        if (!App::getInstance()->has('user')) {
            $user = new User();
            $user->init($res['id']);
            App::getInstance()->put('user', $user);
        }

        return true;
    }

    public function logout()
    {
        if (empty($_COOKIE['user'])) {
            return;
        }
        $config = Config::getInstance();
        $userId = trim($_COOKIE['user']);

        setcookie('user', null, -1, '/', $config->getSettings('domain'), false, true);
        setcookie('hash', null, -1, '/', $config->getSettings('domain'), false, true);

        if (!empty($userId)) {
            Logger::logMessage('Logout user ' . $userId);
        } else {
            Logger::logMessage('Logout without user id');
        }

        App::getInstance()->put('user', null);
    }

    public function getLastError()
    {
        return $this->_lastError;
    }
}