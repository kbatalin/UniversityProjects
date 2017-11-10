<?php

class Shop
{
    private $_items;

    public function init()
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `items` ORDER BY `cost` DESC");
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t get items for shop');
            return false;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        while ($res = $STH->fetch()) {
            $item = new Item();
            if (!$item->init($res['id'], 1)) {
                return false;
            }

            $this->_items[] = $item;
        }

        return true;
    }

    public function getItems()
    {
        return $this->_items;
    }
}