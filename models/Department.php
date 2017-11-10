<?php

class Department
{
    private $_id;
    private $_name;

    public function init($departmentId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `departments` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $departmentId);
        if (!$STH->execute()) {
            Logger::logMessage("Can't execute select in department " . $departmentId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage("Can't find department " . $departmentId);
            return false;
        }

        $this->_id = $res['id'];
        $this->_name = trim($res['name']);

        return true;
    }

    public function getId()
    {
        return $this->_id;
    }

    public function getName()
    {
        return $this->_name;
    }
}