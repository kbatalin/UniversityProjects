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

        if (!empty($_POST['name']) && !empty($_POST['partner'])) {
            $data['name'] = trim($_POST['name']);
            $data['partner'] = trim($_POST['partner']);

            if (!$user->isAvailablePartner($data['partner'])) {
                $data['error'] = 'Необходимо выбрать партнера из списка';
            } else {
                $team = new Team();
                $teamId = $team->create($_POST['name']);
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
}