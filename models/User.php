<?php

class User
{
    private $_id;
    private $_login;
    private $_email;
    private $_pass;
    private $_firstname;
    private $_lastname;
    private $_team = null;
    private $_department = null;
    private $_status;
    private $_restoreHash;
    private $_permissions;

    private $_lastError;

    public static $USER_STATUS = array(
        'NOT_ACTIVE' => 0,
        'ACTIVE' => 1,
    );

    public static $USER_PERMISSIONS = array(
        'USER' => 0,
        'ADMIN' => 1,
    );

    public static function create($login, $pass, $firstname, $lastname, $departmentId, $email = null, $status = 0, $permissions = 0)
    {
        if (empty($login) || empty($pass) || empty($firstname) || empty($lastname) || empty($departmentId)) {
            return false;
        }

        $email = trim($email);
        if (!empty($email) && !filter_var($email, FILTER_VALIDATE_EMAIL)) {
            Logger::logMessage('Bad new email format: ' . $email);
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `users` (login, email, pass, firstname, lastname, department_id, status, permissions) VALUES (?,?,?,?,?,?,?,?)");
        $STH->bindValue(1, trim($login), PDO::PARAM_STR);
        $STH->bindValue(2, empty($email) ? null : $email, PDO::PARAM_STR);
        $STH->bindValue(3, SecurityHelper::cryptPass($pass), PDO::PARAM_STR);
        $STH->bindValue(4, trim($firstname), PDO::PARAM_STR);
        $STH->bindValue(5, trim($lastname), PDO::PARAM_STR);
        $STH->bindValue(6, $departmentId);
        $STH->bindValue(7, $status);
        $STH->bindValue(8, $permissions);
        if (!$STH->execute() || $STH->rowCount() == 0) {
            Logger::logMessage('Can\'t add user. Login: ' . $login . ', email: ' . $email . ', firstname: ' . $firstname
                . ', lastname: ' . $lastname . ', department: ' . $departmentId . ', status: ' . $status . ', perm: ' . $permissions);

            return false;
        }

        return true;
    }

    public function init($userId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `users` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $userId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select user info. User #' . $userId);
            return false;
        }
        $res = $STH->fetch(PDO::FETCH_ASSOC);

        if (!$res) {
            return false;
        }

        $this->initData($res);
        return true;
    }

    public function initByEmail($email)
    {
        $email = trim($email);

        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `users` WHERE `email`=? LIMIT 1");
        $STH->bindValue(1, $email, PDO::PARAM_STR);
        if (!$STH->execute() || !($res = $STH->fetch(PDO::FETCH_ASSOC))) {
            Logger::logMessage('Can\'t find user with email ' . $email);
            $this->_lastError = 'Невозможно найти юзера с данным email';
            return false;
        }

        $this->initData($res);
        return true;
    }

    private function initData($res)
    {
        $this->_id = $res['id'];
        $this->_login = $res['login'];
        $this->_email = $res['email'];
        $this->_pass = $res['pass'];
        $this->_firstname = $res['firstname'];
        $this->_lastname = $res['lastname'];
        if (!empty($res['team_id'])) {
            $this->_team = new Team();
            $this->_team->init($res['team_id']);
        }
        if (!empty($res['department_id'])) {
            $this->_department = new Department();
            $this->_department->init($res['department_id']);
        }
        $this->_status = $res['status'];
        $this->_restoreHash = $res['restore_hash'];
        $this->_permissions = $res['permissions'];
    }

    public function getId()
    {
        return $this->_id;
    }

    public function getLogin()
    {
        return $this->_login;
    }

    public function hasEmail()
    {
        return !empty($this->_email);
    }

    public function getEmail()
    {
        return $this->_email;
    }

    public function setEmail($email)
    {
        $email = trim($email);
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            Logger::logMessage('Bad new email format: ' . $email);
            $this->_lastError = 'Неверный формат email';
            return false;
        }

        Logger::logMessage('Trying set new email ' . $email);

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `email`=?, `status`=? WHERE `id`=?");
        $STH->bindValue(1, $email, PDO::PARAM_STR);
        $STH->bindValue(2, User::$USER_STATUS['NOT_ACTIVE']);
        $STH->bindValue(3, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t set new email');
            $this->_lastError = 'Невозможно использовать данный email. Возможно, он уже используется в системе';
            return false;
        }

        $this->_email = $email;
        $this->_status = User::$USER_STATUS['NOT_ACTIVE'];

        return true;
    }

    public function confirmEmail($code)
    {
        $code = trim($code);
        if (strcmp(md5($this->_email), $code) != 0) {
            Logger::logMessage('Bad confirm code ' . $code . '. Expected ' . md5($this->_email));
            $this->_lastError = 'Неверный код подтверждения';
            return false;
        }

        Logger::logMessage('Confirm success. Activate user');

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `status`=? WHERE `id`=?");
        $STH->bindValue(1, User::$USER_STATUS['ACTIVE']);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t confirm email. User #' . $this->_id);
            $this->_lastError = 'Внутренняя ошибка. Попробуйте позже';
            return false;
        }

        $this->_status = User::$USER_STATUS['ACTIVE'];

        return true;
    }

    public function checkPass($pass)
    {
        $salt = mb_substr($this->_pass, 0, 29);
        $passHash = crypt($pass, $salt);

        return strcmp($this->_pass, $passHash) === 0;
    }

    public function changePass($newPass)
    {
        $newPass = trim($newPass);

        $minLen = Config::getInstance()->getSettings('minPassLength');
        $maxLen = Config::getInstance()->getSettings('maxPassLength');
        if (mb_strlen($newPass) < $minLen ||
            mb_strlen($newPass) > $maxLen) {
            $this->_lastError = 'Длина пароля должна быть от ' . $minLen . ' до ' . $maxLen;
            return false;
        }

        $hash = SecurityHelper::cryptPass($newPass);

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `pass`=? WHERE `id`=?");
        $STH->bindValue(1, $hash, PDO::PARAM_STR);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            $this->_lastError = 'Невозможно обновить пароль';
            Logger::logMessage('Can\'t update password');
            return false;
        }

        $this->_pass = $hash;
        return true;

    }

    public function sendRestoreMail()
    {
        if (empty($this->_email) || $this->_status != User::$USER_STATUS['ACTIVE']) {
            $this->_lastError = 'Нет подтвержденного email';
            Logger::logMessage('Try restore pass for account without email. User #' . $this->_id);
            return false;
        }

        $hash = Random::generateString(50);
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `restore_hash`=? WHERE `id`=?");
        $STH->bindValue(1, $hash, PDO::PARAM_STR);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t set restore hash for user #' . $this->_id);
            $this->_lastError = 'Внутренняя ошибка. Попробуйте еще раз';
            return false;
        }

        $msg = 'Для восстановления пароля перейдите по ссылке http://'
            . Config::getInstance()->getSettings('domain') . '/login/restore/?code=' . $hash . '&id=' . $this->_id;
        if (!Sender::sendEmail($this->_email, 'Восстановление пароля', $msg)) {
            Logger::logMessage('Can\'t send restore mail for user #' . $this->_id);
            $this->_lastError = 'Не удалось отправить письмо. Попробуйте еще раз';
        }

        $this->_restoreHash = $hash;
        Logger::logMessage('Restore mail for user #' . $this->_id . ' was sent');

        return true;
    }

    public function restorePass()
    {
        if (empty($this->_email) || $this->_status != User::$USER_STATUS['ACTIVE']) {
            Logger::logMessage('Restore pass for not active account #' . $this->_id);
            $this->_lastError = 'Нет активированного email';
            return false;
        }

        $newPass = Random::generateString(Config::getInstance()->getSettings('minPassLength'));

        App::getInstance()->getDataBase()->beginTransaction();

        $hash = SecurityHelper::cryptPass($newPass);

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `pass`=?, `restore_hash`=NULL WHERE `id`=?");
        $STH->bindValue(1, $hash, PDO::PARAM_STR);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            $this->_lastError = 'Невозможно сбросить пароль';
            Logger::logMessage('Can\'t update password for user #' . $this->_id);
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        $msg = 'Ваш новый пароль: ' . $newPass . '. Измените его в целях безопасности.';
        if (!Sender::sendEmail($this->_email, 'Новый пароль', $msg)) {
            $this->_lastError = 'Не удалось отправить письмо с новым паролем';
            Logger::logMessage('Can\'t send mail with new pass for user #' . $this->_id);
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        App::getInstance()->getDataBase()->commit();

        $this->_pass = $hash;
        return true;
    }

    public function getRestoreHash()
    {
        return $this->_restoreHash;
    }

    public function getPermissions()
    {
        return $this->_permissions;
    }

    public function isAdmin()
    {
        return $this->_permissions == User::$USER_PERMISSIONS['ADMIN'];
    }

    public function getFirstname()
    {
        return $this->_firstname;
    }

    public function getLastname()
    {
        return $this->_lastname;
    }

    public function getTeam()
    {
        return $this->_team;
    }

    public function getDepartment()
    {
        return $this->_department;
    }

    public function isActive()
    {
        return $this->_status == User::$USER_STATUS['ACTIVE'];
    }

    public function getAvailablePartners()
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id`, `firstname`, `lastname`, `login` FROM `users` WHERE `department_id` != ? AND `team_id` IS NULL");
        $STH->bindValue(1, $this->_department->getId());
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t get available partners for user #' . $this->_id);
            return false;
        }
        return $STH->fetchAll(PDO::FETCH_ASSOC);
    }

    public function isAvailablePartner($partnerId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `users` WHERE `id`=? AND `department_id` != ? AND `team_id` IS NULL");
        $STH->bindValue(1, $partnerId);
        $STH->bindValue(2, $this->_department->getId());
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t check available partner #' . $partnerId . ' for user #' . $this->_id);
            return false;
        }
        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage('Partner #' . $partnerId . ' is not available for new team');
            return false;
        }

        return true;
    }

    public function joinInTeam($teamId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `team_id`=? WHERE `id`=?");
        $STH->bindValue(1, $teamId);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            $this->_lastError = 'Невозможно вступить в команду #' . $teamId;
            Logger::logMessage('Can\'t join in team #' . $teamId);
            return false;
        }

        $this->_team = new Team();
        $this->_team->init($teamId);
        return true;
    }

    public function getLastError()
    {
        return $this->_lastError;
    }
}