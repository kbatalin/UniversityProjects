<?php

include_once 'Controller.php';

class ErrorController extends Controller
{
    public function actionIndex()
    {
        echo 'error';
    }

    public function action404()
    {
        echo '404';
    }
}