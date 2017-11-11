<?php

class Team
{
    private $_id;
    private $_name;
    private $_points;
    private $_inventory = null;
    private $_step2_text;
    private $_lastError;
    private $_language;

    public function create($name, $lang)
    {
        $name = trim($name);
        $lang = mb_strtolower(trim($lang));

        if (mb_strlen($name) > 99) {
            $this->_lastError = 'Слишком длинное название';
            return false;
        }

        if (!array_key_exists($lang, Language::$enum)) {
            $this->_lastError = 'Неверный язык';
            return false;
        }

//        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `teams` WHERE `name`=? LIMIT 1");
//        $STH->bindValue(1, $name, PDO::PARAM_STR);
//        $STH->execute();
//
//        $res = $STH->fetch(PDO::FETCH_ASSOC);
//        if ($res) {
//            $this->_lastError = 'Команда с таким именем уже существует';
//            return false;
//        }

        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `teams` (`name`, `points`, `step2_text`, `language`) VALUES (?, ?, ?, ?)");
        $STH->bindValue(1, $name, PDO::PARAM_STR);
        $STH->bindValue(2, 0);
        $STH->bindValue(3, "", PDO::PARAM_STR);
        $STH->bindValue(4, $lang, PDO::PARAM_STR);
        if (!$STH->execute() || $STH->rowCount() == 0) {
            App::getInstance()->getDataBase()->rollBack();
            $this->_lastError = 'Не удалось создать команду с таким именем. Возможно, оно уже занято';
            Logger::logMessage('Can\'t create team with name ' . $name);
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `teams` WHERE `name`=? LIMIT 1");
        $STH->bindValue(1, $name, PDO::PARAM_STR);
        if (!$STH->execute() || !($res = $STH->fetch(PDO::FETCH_ASSOC))) {
            App::getInstance()->getDataBase()->rollBack();
            $this->_lastError = 'Неизвестная ошибка во время создания команды. Попробуйте еще раз';
            Logger::logMessage('Can\'t select team after insert. Name: ' . $name);
            return false;
        }
        $teamId = $res['id'];

        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `tasks` WHERE `active`=1");
        if (!$STH->execute()) {
            App::getInstance()->getDataBase()->rollBack();
            $this->_lastError = 'Не сделать выборку заданий. Попробуйте еще раз';
            Logger::logMessage('Can\'t select tasks');
            return false;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        while ($res = $STH->fetch()) {
            $taskSTH = App::getInstance()->getDataBase()->prepare("INSERT INTO `team_tasks` (`team_id`, `task_id`, `status`) VALUES (?, ?, ?)");
            $taskSTH->bindValue(1, $teamId);
            $taskSTH->bindValue(2, $res['id']);
            $taskSTH->bindValue(3, TeamTask::$TASK_STATUS['EMPTY'][0]);
            if (!$taskSTH->execute()) {
                var_dump($taskSTH->errorInfo());
                App::getInstance()->getDataBase()->rollBack();
                $this->_lastError = 'Не удалось добавить задания. Попробуйте еще раз';
                Logger::logMessage('Can\'t insert team tasks #' . $res['id'] . ' for team #' . $teamId);
                return false;
            }
        }

        App::getInstance()->getDataBase()->commit();

        if (!$this->init($teamId)) {
            $this->_lastError = 'Не удалось создать команду';
            return false;
        }

        return $teamId;
    }

    public function init($teamId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `teams` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $teamId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select team info. Team #' . $teamId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage("Can't find team #" . $teamId);
            return false;
        }

        $this->_id = $res['id'];
        $this->_name = trim($res['name']);
        $this->_points = $res['points'];
        $this->_step2_text = $res['step2_text'];
        $this->_language = $res['language'];

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

    /**
     * @return Inventory
     */
    public function getInventory()
    {
        if (!$this->_inventory) {
            $this->_inventory = new Inventory();
            if (!$this->_inventory->init($this->_id)) {
                $this->_inventory = null;
                return null;
            }
        }

        return $this->_inventory;
    }

    /**
     * @param $item Item
     * @return bool
     */
    public function canBuyItem($item)
    {
        if ($item->getCost() > $this->_points) {
            Logger::logMessage('Can\'t buy item #' . $item->getId() . ', team #' . $this->_id . '. Not enough money');
            $this->_lastError = 'Недостаточно очков для покупки';
            return false;
        }

        if ($this->getInventory()->hasItem($item->getId())) {
            Logger::logMessage('Can\'t buy item #' . $item->getId() . ', team #' . $this->_id . '. Already has');
            $this->_lastError = 'Вещь уже куплена';
            return false;
        }

        return true;
    }

    /**
     * @param $item Item
     * @return bool
     */
    public function buyItem($item)
    {
        if (!$this->canBuyItem($item)) {
            return false;
        }

        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `inventory` (team_id, item_id) VALUES (?, ?)");
        $STH->bindValue(1, $this->_id);
        $STH->bindValue(2, $item->getId());
        if (!$STH->execute() || $STH->rowCount() == 0) {
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t add item #' . $item->getId() . ', team #' . $this->_id);
            $this->_lastError = 'Внутренняя ошибка';
            return false;
        }

        if (!$this->addPoints(-$item->getCost())) {
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        App::getInstance()->getDataBase()->commit();
        return true;
    }

    /**
     * @param $item Item
     * @return bool
     */
    public function sellItem($item)
    {
        if (!$this->getInventory()->hasItem($item->getId())) {
            Logger::logMessage('Can\'t sell item #' . $item->getId() . ', team #' . $this->_id . '. Not available');
            $this->_lastError = 'У вас нет такой вещи';
            return false;
        }

        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("DELETE FROM `inventory` WHERE `team_id`=? AND `item_id`=? LIMIT 1");
        $STH->bindValue(1, $this->_id);
        $STH->bindValue(2, $item->getId());
        if (!$STH->execute() || $STH->rowCount() == 0) {
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t sell item #' . $item->getId() . ', team #' . $this->_id);
            $this->_lastError = 'Не удалось продать вещь';
            return false;
        }

        if (!$this->addPoints($item->getCost())) {
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        App::getInstance()->getDataBase()->commit();
        return true;
    }

    public function getPoints()
    {
        return $this->_points;
    }

    public function getStep2Text()
    {
        return $this->_step2_text;
    }

    public function setStep2Text($text)
    {
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `teams` SET `step2_text`=? WHERE `id`=?");
        $STH->bindValue(1, $text, PDO::PARAM_STR);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute() || $STH->rowCount() == 0) {
            Logger::logMessage('Can\'t set step2_text for team #' . $this->_id);
            return false;
        }

        $this->_step2_text = $text;
        Logger::logMessage('Step2_text updated for team #' . $this->_id);
        return true;
    }

    public function getTasks()
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `task_id` FROM `team_tasks` WHERE `team_id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select tasks for team #' . $this->_id);
            return false;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        $tasks = array();
        while ($res = $STH->fetch()) {
            $task = new TeamTask();
            $task->init($this->_id, $res['task_id']);
            $tasks[] = $task;
        }

        return $tasks;
    }

    public function addPoints($points)
    {
        Logger::logMessage("Add " . $points . " points to team #" . $this->_id . '. Current count: ' . $this->_points);

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `teams` SET `points`=(`points`+?) WHERE `id`=?");
        $STH->bindValue(1, $points);
        $STH->bindValue(2, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t update team\'s points. Team #' . $this->_id);
            return false;
        }

        $this->_points += $points;
        return true;
    }

    public function erase()
    {
        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("DELETE FROM `team_tasks` WHERE `team_id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t remove team tasks for team #' . $this->_id);
            $this->_lastError = 'Невозможно удалить задания команды';
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("DELETE FROM `inventory` WHERE `team_id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t remove inventory for team #' . $this->_id);
            $this->_lastError = 'Невозможно удалить инвентарь команды';
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `users` SET `team_id`=NULL WHERE `team_id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t remove users from team #' . $this->_id);
            $this->_lastError = 'Невозможно удалить юзеров из команды';
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        $STH = App::getInstance()->getDataBase()->prepare("DELETE FROM `teams` WHERE `id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t remove team #' . $this->_id);
            $this->_lastError = 'Невозможно удалить команду';
            App::getInstance()->getDataBase()->rollBack();
            return false;
        }

        App::getInstance()->getDataBase()->commit();
        return true;
    }

    public function getLastError()
    {
        return $this->_lastError;
    }

    public function getMembers()
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `users` WHERE `team_id`=?");
        $STH->bindValue(1, $this->_id);
        if (!$STH->execute()) {
            return null;
        }
        $STH->setFetchMode(PDO::FETCH_ASSOC);

        $members = array();
        while ($res = $STH->fetch()) {
            $user = new User();
            $user->init($res['id']);
            $members[] = $user;
        }

        return $members;
    }

    public function getLanguage()
    {
        return $this->_language;
    }


}