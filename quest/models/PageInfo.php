<?php

class PageInfo
{
    private $_page;
    private $_title;
    private $_description;

    public function init($page)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `pages` WHERE `page`=? LIMIT 1");
        $STH->bindValue(1, $page, PDO::PARAM_STR);
        $STH->setFetchMode(PDO::FETCH_ASSOC);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select page info for page ' . $page);
            return false;
        }

        $res = $STH->fetch();
        if (!$res) {
            Logger::logMessage("Can't get page info.");
            return false;
        }

        $this->_page = $res['page'];
        $this->_title = $res['title'];
        $this->_description = $res['description'];

        return true;
    }

    public function getPage()
    {
        return $this->_page;
    }

    public function getTitle()
    {
        return $this->_title;
    }

    public function getDescription()
    {
        return $this->_description;
    }

    public function getInfo()
    {
        return array(
            'page' => $this->_page,
            'title' => $this->_title,
            'description' => $this->_description,
        );
    }
}