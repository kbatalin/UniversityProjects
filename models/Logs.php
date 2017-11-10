<?php

class Logs
{
    private $_logs;

    public function init()
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `logs` ORDER BY `id` DESC LIMIT 5000");
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t get logs');
            return false;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        $this->_logs = array();
        while ($res = $STH->fetch()) {
            $this->_logs[] = $res;
        }

        return true;
    }

    public function getLogs()
    {
        return $this->_logs;
    }
}