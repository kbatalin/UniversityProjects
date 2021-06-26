<?php

class SecurityHelper
{
    public static function cryptPass($pass)
    {
        $pass = trim($pass);

        $blowfish = Config::getInstance()->getSettings('blowfish');
        $salt = Random::generateString(22);
        $hash = crypt($pass, $blowfish . $salt);

        return $hash;
    }

    public static function hashEquals($hash1, $hash2)
    {
        return strcmp($hash1, $hash2) == 0;
    }
}