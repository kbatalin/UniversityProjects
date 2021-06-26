<?php

class Logger
{
    public static function logMessage($msg)
    {
        $app = App::getInstance();
        $user = $app->get('user');

        $STH = $app->getDataBase()->prepare("INSERT INTO `logs` (`log`, `url`, `log_date`, `user_id`, `user_ip`) VALUES (?, ?, NOW(), ?, ?)");
        $STH->bindValue(1, $msg, PDO::PARAM_STR);
        $STH->bindValue(2, $_SERVER['REQUEST_URI'], PDO::PARAM_STR);
        $STH->bindValue(3, $user ? $user->getId() : null);
        $STH->bindValue(4, $_SERVER['REMOTE_ADDR'], PDO::PARAM_STR);
        $STH->execute();
    }
}