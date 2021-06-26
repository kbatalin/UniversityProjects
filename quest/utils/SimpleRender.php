<?php

class SimpleRender
{
    public static function render($_file, $_data = null, $_return = false)
    {
        if (!is_file($_file)) {
            return null;
        }

        if (is_array($_data)) {
            extract($_data, EXTR_SKIP);
        }

        if ($_return) {
            ob_start();
            ob_implicit_flush(false);
            include($_file);
            return ob_get_clean();
        } else {
            include $_file;
            return null;
        }
    }
}