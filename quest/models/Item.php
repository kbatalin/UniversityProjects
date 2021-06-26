<?php

class Item
{
    private $_itemId;
    private $_itemName;
    private $_itemDescription;
    private $_itemCost;
    private $_itemCount;
    private $_itemImg;

    public function init($itemId, $count)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `items` WHERE `id`=?");
        $STH->bindValue(1, $itemId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t get item #' . $itemId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage('Can\'t find item #' . $itemId . ' in db');
            return false;
        }

        $this->_itemId = $itemId;
        $this->_itemName = $res['name'];
        $this->_itemDescription = $res['description'];
        $this->_itemCost = $res['cost'];
        $this->_itemCount = $count;
        $this->_itemImg = $res['img'];

        return true;
    }

    public function getId()
    {
        return $this->_itemId;
    }

    public function getImg()
    {
        return $this->_itemImg;
    }

    public function getName()
    {
        return $this->_itemName;
    }

    public function getDescription()
    {
        return $this->_itemDescription;
    }

    public function getCost()
    {
        return $this->_itemCost;
    }

    public function getCount()
    {
        return $this->_itemCount;
    }
}