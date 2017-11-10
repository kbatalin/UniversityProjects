<?php

class Inventory
{
    private $_items;

    public function init($teamId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `items`.`id`, COUNT(`items`.`id`) as itemCount FROM `inventory` INNER JOIN `items` ON `inventory`.`item_id` = `items`.`id` WHERE `inventory`.`team_id`=? GROUP BY `items`.`id`");
        $STH->bindValue(1, $teamId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t get inventory for team #' . $teamId);
            return false;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        $this->_items = array();

        while ($res = $STH->fetch()) {
            $item = new Item();
            if (!$item->init($res['id'], $res['itemCount'])) {
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

    public function hasItem($itemId)
    {
        foreach ($this->_items as $item) {
            if ($item->getId() == $itemId) {
                return true;
            }
        }

        return false;
    }
}