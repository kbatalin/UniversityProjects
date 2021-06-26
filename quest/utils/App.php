<?php

class App
{
    private static $_instance = null;

    private $_DBH = null;
    private $_runtimeSettings = array();

    private function __construct()
    {
        $config = Config::getInstance();

        try {
            $this->_DBH = new PDO("mysql:host=" . $config->getSettings('dbHost')
                . ";dbname=" . $config->getSettings('dbName')
                . ";charset=utf8", $config->getSettings('dbUser'), $config->getSettings('dbPass'));
            $this->_DBH->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_SILENT);
        } catch (PDOException $e) {
            echo $e->getMessage();
        }
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

    public function getDataBase()
    {
        return $this->_DBH;
    }

    public function get($key)
    {
        if ($this->has($key)) {
            return $this->_runtimeSettings[$key];
        }

        return null;
    }

    public function put($key, $val)
    {
        $this->_runtimeSettings[$key] = $val;
    }

    public function has($key)
    {
        return array_key_exists($key, $this->_runtimeSettings) && $this->_runtimeSettings[$key] != null;
    }
}