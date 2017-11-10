<?php

class TeamTask
{
    private $_teamId;

    /**
     * @var Task
     */
    private $_task;
    private $_lastAnswer;
    private $_status;

    public static $TASK_STATUS = array(
        'EMPTY' => array(0, 'Нет решения'),
        'FAIL' => array(1, 'Неверный ответ'),
        'SUCCESS' => array(2, 'Решение принято'),
        'UNKNOWN' => array(3, 'Неизвестная ошибка'),
    );

    public function init($teamId, $taskId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `team_tasks` WHERE `task_id`=? AND `team_id`=? LIMIT 1");
        $STH->bindValue(1, $taskId);
        $STH->bindValue(2, $teamId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select team_task info. Team #' . $teamId . ', task #' . $taskId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage('Can\'t get team task. Team #' . $teamId . ', task #' . $taskId);
            return false;
        }

        $this->_teamId = $teamId;
        $this->_task = new Task();
        $this->_task->init($taskId);
        $this->_lastAnswer = $res['last_answer'];
        $this->_status = $res['status'];

        return true;
    }

    public function getTeamId()
    {
        return $this->_teamId;
    }

    public function getTask()
    {
        return $this->_task;
    }

    public function getLastAnswer()
    {
        return $this->_lastAnswer;
    }

    public function getStatus()
    {
        return $this->_status;
    }

    public function getStatusText()
    {
        foreach (TeamTask::$TASK_STATUS as $error) {
            if ($error[0] == $this->_status) {
                return $error[1];
            }
        }

        return 'Незивестная ошибка. Код: ' . $this->_status;
    }

    public function setAnswer($answer, $status)
    {
        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `team_tasks` SET `last_answer`=?, `status`=? WHERE `team_id`=? AND `task_id`=?");
        $STH->bindValue(1, $answer, PDO::PARAM_STR);
        $STH->bindValue(2, $status);
        $STH->bindValue(3, $this->_teamId);
        $STH->bindValue(4, $this->_task->getId());
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t update team task status. Team #' . $this->_teamId . ', task #'
                . $this->_task . ', answer: ' . $answer);
            return false;
        }

        $this->_lastAnswer = $answer;
        $this->_status = $status;
        return true;
    }

    public function tryAnswer($answer)
    {
        $answer = trim($answer);
        $res = $this->_task->checkAnswer($answer);
        if ($res) {
            Logger::logMessage('Good answer. Task #' . $this->_task->getId() . '. User\'s answer: "' . $answer . '"');
        } else {
            Logger::logMessage('Bad answer. Task #' . $this->_task->getId() . '. User\'s answer: "'
                . $answer . '". Good answer: "' . $this->_task->getId() . '"');
        }

        $STH = App::getInstance()->getDataBase()->prepare("UPDATE `team_tasks` SET `last_answer`=?, `status`=? WHERE `team_id`=? AND `task_id`=?");
        $STH->bindValue(1, $answer, PDO::PARAM_STR);
        $STH->bindValue(2, $res ? TeamTask::$TASK_STATUS['SUCCESS'][0] : TeamTask::$TASK_STATUS['FAIL'][0]);
        $STH->bindValue(3, $this->_teamId);
        $STH->bindValue(4, $this->_task->getId());
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t update team task status. Team #' . $this->_teamId . ', task #'
                . $this->_task . ', answer: ' . $answer);
            return false;
        }

        return $res;
    }
}