<?php

class LoginController extends Controller
{
    public function actionIndex()
    {
        $data = array(
            'error' => null,
        );
        if (!empty($_POST['login']) && !empty($_POST['pass'])) {
            $data['login'] = $_POST['login'];
            $loginModel = new Login();

            if ($loginModel->auth($_POST['login'], $_POST['pass'])) {
                //success login
                $user = App::getInstance()->get('user');
                if (!$user->hasEmail()) {
                    $this->redirect('/user/email/');
                } else if (!$user->isActive()) {
                    $this->redirect('/user/confirm/');
                } else {
                    $this->redirect('/');
                }
            } else {
                $data['error'] = $loginModel->getLastError();
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('login');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('index', $data);
    }

    public function actionLogout()
    {
        $loginModel = new Login();
        $loginModel->logout();
        $this->redirect('/');
    }

    public function actionPass()
    {
        if (!empty($_POST['email'])) {
            $data['email'] = trim($_POST['email']);

            $user = new User();
            if (!$user->initByEmail($data['email'])) {
                $data['status'] = $user->getLastError();
            } else if (!$user->sendRestoreMail()) {
                $data['status'] = $user->getLastError();
            } else {
                $data['status'] = 'Письмо с инструкцией отправлено на email';
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('pass-restore');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('pass', $data);
    }

    public function actionRestore()
    {
        if (!empty($_GET['code']) && !empty($_GET['id'])) {
            $user = new User();
            if (!$user->init($_GET['id'])) {
                $data['error'] = 'Неверные данные';
            } else if (empty($user->getRestoreHash())) {
                $data['error'] = 'Неверный код';
            } else if (strcmp($user->getRestoreHash(), trim($_GET['code'])) != 0) {
                $data['error'] = 'Неверный код';
            } else if (!$user->restorePass()) {
                $data['error'] = $user->getLastError();
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('confirm-restore');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('restore', $data);
    }
}