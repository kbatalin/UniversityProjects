<?php

class Sender
{
    public static function sendEmail($email, $subject, $msg)
    {
        $headers = 'From: robot@' . Config::getInstance()->getSettings('domain') . "\r\n" .
            //           'Reply-To: webmaster@example.com' . "\r\n" .
            'X-Mailer: PHP/' . phpversion();

        return mail($email, $subject, $msg, $headers);
    }
}