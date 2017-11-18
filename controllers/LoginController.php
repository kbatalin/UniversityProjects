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

    public function actionRegister()
    {
        if (!empty($_POST['submit'])) {
            $data = $_POST;
            if (empty($_POST['fitFirstname']) || empty($_POST['fitLastname']) || empty($_POST['fitEmail'])
                || empty($_POST['fijaFirstname']) || empty($_POST['fijaLastname'])) {
                $data['error'] = 'Все поля обязательны для заполнения';
            } else {
                $_POST['fitEmail'] = trim($_POST['fitEmail']);
                $_POST['fitEmail'] = mb_strtolower($_POST['fitEmail']);

                $availableEmail = AvailableEmail::findByEmail($_POST['fitEmail']);
                if (empty($availableEmail) || $availableEmail->getUsed() != 0) {
                    $data['error'] = 'Неверный email';
                } else {
                    Logger::logMessage('Start creation: ' . implode(', ', $_POST));
                    User::create($_POST['fitEmail'], md5('hw4' . $_POST['fitEmail'])
                        , $_POST['fitFirstname'], $_POST['fitLastname'], 1, $_POST['fitEmail'], 1, 0);
                    User::create($_POST['fitEmail'] . 'fija', md5('hw4' . $_POST['fitEmail'])
                        , $_POST['fijaFirstname'], $_POST['fijaLastname'], 2, $_POST['fitEmail'] . 'fija', 0, 0);

                    $team = new Team();
                    $teamId = $team->create('Моя команда', null);
                    if (empty($teamId)) {
                        $data['error'] = $team->getLastError();
                    } else {
                        $fit = new User();
                        $fit->initByEmail($_POST['fitEmail']);

                        $fija = new User();
                        $fija->initByEmail($_POST['fitEmail'] . 'fija');

                        if (!$fit->joinInTeam($teamId) || !$fija->joinInTeam($teamId)) {
                            $data['error'] = 'Невозможно вступить в команду';
                        } else {
                            Logger::logMessage('New team #' . $teamId . ' created');

                            $msg = 'Ваш логин: ' . $_POST['fitEmail'] . ', пароль: ' . md5('hw4' . $_POST['fitEmail']) . '. Измените его в целях безопасности.';
                            if (!Sender::sendEmail($_POST['fitEmail'], 'Регистрация в квесте', $msg)) {
                                $this->_lastError = 'Не удалось отправить письмо с паролем. Обратитесь к админу';
                                Logger::logMessage('Can\'t send mail with new pass for user ' . $_POST['fitEmail']);
                            } else {
                                $this->redirect('/login/okreg');
                                $availableEmail->useIt();
                            }
                        }
                    }
                }
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('register');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('register', $data);
    }

    public function actionOkreg()
    {
        $pageInfo = new PageInfo();
        $pageInfo->init('okreg');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('okreg', $data);
    }
}