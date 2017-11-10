<?php

class View
{
    public static function render($_file, $_data = null)
    {
        $_path = 'views/' . $_file . '.php';
        return SimpleRender::render($_path, $_data);
    }
}