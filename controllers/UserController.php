<?php

class UserController extends Controller
{
    public function actionIndex()
    {
        $this->redirect('/');
    }

    public function actionPass()
    {
        $loginModel = new Login();
        if (!$loginModel->checkAuth()) {
            $this->redirect('/login/');
        }

        $user = App::getInstance()->get('user');
        if (!$user->hasEmail()) {
            $this->redirect('/user/email/');
        } else if (!$user->isActive()) {
            $this->redirect('/user/confirm/');
        }

        $data = array();

        if (!empty($_POST['oldPass']) && !empty($_POST['newPass']) && !empty($_POST['newPass2'])) {
            $oldPass = trim($_POST['oldPass']);
            $newPass = trim($_POST['newPass']);
            $newPass2 = trim($_POST['newPass2']);

            if (strcmp($newPass, $newPass2) !== 0) {
                $data['error'] = 'Новые пароли не совпадают';
            } else if (!$user->checkPass($oldPass)) {
                $data['error'] = 'Неверный пароль';
            } else if (!$user->changePass($newPass)) {
                $data['error'] = $user->getLastError();
            } else {
                $this->redirect('/');
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('pass');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('pass', $data);
    }

    public function actionEmail()
    {
        $loginModel = new Login();
        if (!$loginModel->checkAuth()) {
            $this->redirect('/login/');
        }

        $data = array(
            'error' => null,
        );
        if (!empty($_POST['email'])) {
            $data['email'] = $_POST['email'];
            $user = App::getInstance()->get('user');

            if (!$user->setEmail($_POST['email'])) {
                $data['error'] = $user->getLastError();
            } else if (!$this->sendConfirmEmail($_POST['email'])) {
                $data['error'] = 'Не удалось отправить подтверждающее письмо. Свяжитесь с администрацией';
            } else {
                $this->redirect('/user/confirm/');
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('new email');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('email', $data);
    }

    private function sendConfirmEmail($email)
    {
        $email = trim($email);
        $msg = 'Для подтверждения email введи код ' . md5($email)
            . ' или перейди по ссылке: http://' . Config::getInstance()->getSettings('domain')
            . '/user/confirm/?code=' . md5($email);

        $res = Sender::sendEmail($email, 'Подтверждение email', $msg);
        if ($res) {
            Logger::logMessage('Sent confirm mail to ' . $email);
        } else {
            Logger::logMessage('Can\'t sent confirm mail to ' . $email);
        }

        return $res;
    }

    public function actionConfirm()
    {
        $loginModel = new Login();
        if (!$loginModel->checkAuth()) {
            $this->redirect('/login/');
        }

        $data = array(
            'error' => null,
        );
        if (!empty($_GET['code'])) {
            $data['code'] = $_GET['code'];
            $user = App::getInstance()->get('user');

            if ($user->confirmEmail($_GET['code'])) {
                $this->redirect('/');
            } else {
                $data['error'] = $user->getLastError();
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('confirm email');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('confirm', $data);
    }
}