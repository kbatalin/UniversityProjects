<?php

class Task
{
    private $_id;
    private $_name;
    private $_text;
    private $_points;
    private $_answer;
    private $_visible;
    private $_deadline;

    public static function create($id, $name, $text, $points, $answer, $active, $visible)
    {
        $name = trim($name);
        $text = trim($text);
        $points = trim($points);
        $answer = trim($answer);
        $active = trim($active);
        if (empty($answer)) {
            Logger::logMessage('Can\'t create task with empty name or answer');
            return false;
        }

        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `tasks` (id, name, task, points, answer, active, visible) VALUES (?,?,?,?,?,?,?)");
        $STH->bindValue(1, empty($id) ? null : $id);
        $STH->bindValue(2, empty($name) ? null : $name, PDO::PARAM_STR);
        $STH->bindValue(3, $text, PDO::PARAM_STR);
        $STH->bindValue(4, $points);
        $STH->bindValue(5, $answer, PDO::PARAM_STR);
        $STH->bindValue(6, $active);
        $STH->bindValue(7, $visible);
        if (!$STH->execute() || $STH->rowCount() == 0) {
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t add task. name: ' . $name);
            return false;
        }

//        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `tasks` ORDER BY `id` DESC LIMIT 1");
//        if (!$STH->execute() || !($res = $STH->fetch(PDO::FETCH_ASSOC))) {
//            App::getInstance()->getDataBase()->rollBack();
//            Logger::logMessage('Can\'t get task id. name: ' . $name);
//            return false;
//        }
//        $taskId = $res['id'];

        $taskId = App::getInstance()->getDataBase()->lastInsertId();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `team_tasks` (team_id, task_id, last_answer, status) SELECT `id`, ?, NULL, ? FROM `teams`");
        $STH->bindValue(1, $taskId);
        $STH->bindValue(2, TeamTask::$TASK_STATUS['EMPTY'][0]);
        if (!$STH->execute()) {
            var_dump($STH->errorInfo());
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t add team_tasks for task #' . $taskId);
            return false;
        }

        App::getInstance()->getDataBase()->commit();

        return true;
    }

    public function init($taskId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `tasks` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $taskId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select task info. Task #' . $taskId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage("Can't find task " . $taskId);
            return false;
        }

        $this->_id = $res['id'];
        $this->_name = trim($res['name']);
        $this->_text = trim($res['task']);
        $this->_points = $res['points'];
        $this->_answer = trim($res['answer']);
        $this->_visible = $res['visible'];
        $this->_deadline = $res['deadline'];

        return true;
    }

    function checkAnswer($answer)
    {
        $answer = trim($answer);
        $answer = mb_ereg_replace(' ', '', $answer);

        if ($this->_id == 952) {
            $answer = mb_ereg_replace('!', '', $answer);
        }

        return strcmp(mb_strtolower($answer), mb_strtolower($this->_answer)) == 0;
    }

    public function getId()
    {
        return $this->_id;
    }

    public function getName()
    {
        return $this->_name;
    }

    public function getText()
    {
        return $this->_text;
    }

    public function getPoints()
    {
        return $this->_points;
    }

    public function getAnswer()
    {
        return $this->_answer;
    }

    public function getDeadline()
    {
        return $this->_deadline;
    }

    public function isVisible()
    {
        return $this->_visible;
    }
}