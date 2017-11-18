<?php

class TeamController extends Controller
{
    public function actionIndex()
    {

    }

    public function actionCreate()
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

        if (!empty($user->getTeam())) {
            $this->redirect('/');
        }

        $data = array();

        if (!empty($_POST['name']) && !empty($_POST['partner']) && !empty($_POST['language'])) {
            $data['name'] = trim($_POST['name']);
            $data['partner'] = trim($_POST['partner']);
            $data['language'] = trim($_POST['language']);

            if (!$user->isAvailablePartner($data['partner'])) {
                $data['error'] = 'Необходимо выбрать партнера из списка';
            } else if (!array_key_exists($data['language'], Language::$enum)) {
                $data['error'] = 'Необходимо выбрать язык из списка';
            } else {
                $team = new Team();
                $teamId = $team->create($_POST['name'], $data['language']);
                if (empty($teamId)) {
                    $data['error'] = $team->getLastError();
                } else {
                    $partner = new User();
                    $partner->init($data['partner']);
                    if (!$user->joinInTeam($teamId) || !$partner->joinInTeam($teamId)) {
                        $data['error'] = 'Невозможно вступить в команду';
                    } else {
                        Logger::logMessage('New team #' . $teamId . ' created');
                        $this->redirect('/');
                    }
                }
            }

        }

        $data['availablePartners'] = $user->getAvailablePartners();
        $data['languages'] = Language::$enum;

        $pageInfo = new PageInfo();
        $pageInfo->init('new team');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('create', $data);
    }

    public function actionLeave()
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

        if (empty($user->getTeam())) {
            $this->redirect('/');
        }

        if (!empty($_GET['confirm'])) {
            Logger::logMessage('Erase team #' . $user->getTeam()->getId());
            $user->getTeam()->erase();
            $this->redirect('/');
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('leave-team');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('leave', $data);
    }

    public function actionInventory()
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

        $team = $user->getTeam();
        if ($team) {
            $inventory = $team->getInventory();
            if ($inventory) {
                $data['items'] = $inventory->getItems();
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('/team/inventory/');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('inventory', $data);
    }

    public function actionUpdate()
    {
        $loginModel = new Login();
        if (!$loginModel->checkAuth()) {
            $this->redirect('/login/');
        }
        $user = App::getInstance()->get('user');
        $team = $user->getTeam();

        $data = array();

        if (!empty($_POST['submit'])) {
            $data = $_POST;

            if (!array_key_exists($data['language'], Language::$enum)) {
                $data['error'] = 'Необходимо выбрать язык из списка';
            } else if (empty($data['name']) || mb_strlen($data['name']) > 50){
                $data['error'] = 'Название команды обязательно и должно быть меньше 50 символов';
            } else {
                $STH = App::getInstance()->getDataBase()->prepare("UPDATE teams SET name=?, language=? WHERE id=?");
                $STH->bindValue(1, $data['name'], PDO::PARAM_STR);
                $STH->bindValue(2, $data['language'], PDO::PARAM_STR);
                $STH->bindValue(3,$team->getId());

                if (!$STH->execute()) {
                    App::getInstance()->getDataBase()->rollBack();
                    Logger::logMessage('Can\'t update team: id' . $team->getId());
                } else {
                    $this->redirect('/');
                }

            }
        }

        $data['languages'] = Language::$enum;
        $data['name'] = $team->getName();
        $data['language'] = $team->getLanguage();

        $pageInfo = new PageInfo();
        $pageInfo->init('/team/update/');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('update', $data);
    }
}