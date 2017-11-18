<?php

class AvailableEmail
{
    private $_id;
    private $_email;
    private $_used;

    public function init($id)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `available_emails` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select available email #' . $id);
            return false;
        }
        $res = $STH->fetch(PDO::FETCH_ASSOC);

        if (!$res) {
            return false;
        }

        $this->initData($res);
        return true;
    }

    public function useIt()
    {
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `available_emails` SET used=1 WHERE `id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select available email #' . $id);
        }
    }

    private function initData($res)
    {
        $this->_id = $res['id'];
        $this->_email = $res['email'];
        $this->_used = $res['used'];
    }

    /**
     * @return mixed
     */
    public function getId()
    {
        return $this->_id;
    }

    /**
     * @return mixed
     */
    public function getEmail()
    {
        return $this->_email;
    }

    /**
     * @return mixed
     */
    public function getUsed()
    {
        return $this->_used;
    }

    public static function findByEmail($email)
    {
        $email = mb_strtolower($email);
        $email = trim($email);
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `available_emails` WHERE LOWER(`email`) LIKE ? AND `used`=0 LIMIT 1");
        $STH->bindValue(1, $email);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select available email #' . $email);
            return null;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);

        if (!$res) {
            return null;
        }

        $d = new AvailableEmail();
        $d->initData($res);
        return $d;
    }
}