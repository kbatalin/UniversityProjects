<?php

class Config
{
    private static $_instance = null;
    private $_settings = array();

    private function __construct()
    {
        //DB
        $this->_settings['dbName'] = 'quest17';
        $this->_settings['dbHost'] = 'localhost';
        $this->_settings['dbUser'] = 'quest17_user';
        $this->_settings['dbPass'] = 'quest17_pass';

        $this->_settings['blowfish'] = '$2a$10$';
        $this->_settings['loginHashLength'] = 40;
        $this->_settings['authTimeSec'] = 7 * 24 * 60 * 60;
        $this->_settings['domain'] = 'quest17.local';
        $this->_settings['minPassLength'] = 6;
        $this->_settings['maxPassLength'] = 30;
    }

    protected function __clone()
    {
    }

    static public function getInstance()
    {
        if (is_null(self::$_instance)) {
            self::$_instance = new self();
        }
        return self::$_instance;
    }

    public function getSettings($key)
    {
        return $this->_settings[$key];
    }
}



