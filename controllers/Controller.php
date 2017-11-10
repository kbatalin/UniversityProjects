<?php

class Controller
{
    protected function render($_page, $_data = null, $_return = false)
    {
        $_controllerName = get_class($this);
        $_path = 'views/' . lcfirst(mb_substr($_controllerName, 0, mb_strrpos($_controllerName, 'Controller'))) . '/' . $_page . '.php';

        return SimpleRender::render($_path, $_data, $_return);
    }

    protected function redirect($location)
    {
        header('Location: ' . $location);
        exit;
    }
}