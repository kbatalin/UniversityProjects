<?php

class AdminController extends Controller
{
    public function actionIndex()
    {
        $this->checkPermissions();

        $logs = new Logs();
        if ($logs->init()) {
            $data['logs'] = $logs->getLogs();
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('admin');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('index', $data);
    }

    public function actionUsers()
    {
        $this->checkPermissions();

        if (!empty($_POST['actionAdd'])) {
            if (User::create($_POST['login'], $_POST['pass'], $_POST['firstname'], $_POST['lastname'], $_POST['department'],
                !empty($_POST['email']) ? $_POST['email'] : null,
                !empty($_POST['status']) ? $_POST['status'] : 0,
                !empty($_POST['permissions']) ? $_POST['permissions'] : 0)) {
                $data['addStatus'] = 'Добавлен';
            } else {
                $data['addStatus'] = 'Не удалось добавить';
                $data['login'] = $_POST['login'];
                $data['pass'] = $_POST['pass'];
                $data['firstname'] = $_POST['firstname'];
                $data['lastname'] = $_POST['lastname'];
                $data['department'] = $_POST['department'];
                $data['email'] = $_POST['email'];
                $data['status'] = $_POST['status'];
                $data['permissions'] = $_POST['permissions'];
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('/admin/users/');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('users', $data);
    }

    public function actionGenUsers()
    {
        $this->checkPermissions();

        if (!empty($_POST['fit']) || !empty($_POST['fija'])) {
            $fit = preg_split('/\r\n|[\r\n]/', $_POST['fit']);
            $fija = preg_split('/\r\n|[\r\n]/', $_POST['fija']);

            $parser = new Parser();
            if ($parser->parse($fit, $fija)) {
                $data['results'] = $parser->getUsers();

                foreach ($data['results'] as &$userInfo) {
                    $userInfo['login'] = LangHelper::makeLogin($userInfo['group'], $userInfo['firstname'], $userInfo['lastname']);
                    $userInfo['pass'] = Random::generateString(Config::getInstance()->getSettings('minPassLength'));

                    if (User::create($userInfo['login'], $userInfo['pass'], $userInfo['firstname'],
                        $userInfo['lastname'], $userInfo['department'])) {
                        $userInfo['added'] = 1;
                    } else {
                        $userInfo['added'] = 0;
                    }

                }
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('/admin/gen-users/');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('gen-users', $data);
    }

    public function actionTasks()
    {
        $this->checkPermissions();

        if (isset($_POST['id']) && isset($_POST['name']) && isset($_POST['task']) && !empty($_POST['answer']) && isset($_POST['points']) && isset($_POST['visible'])) {
            if (Task::create($_POST['id'], $_POST['name'], $_POST['task'], $_POST['points'], $_POST['answer'], 1, $_POST['visible'])) {
                $data['addStatus'] = 'Задание добавлено';
            } else {
                $data['addStatus'] = 'Ошибка добавления';
                $data['name'] = trim($_POST['name']);
                $data['task'] = trim($_POST['task']);
                $data['answer'] = trim($_POST['answer']);
                $data['points'] = trim($_POST['points']);
                $data['visible'] = trim($_POST['visible']);
            }
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('/admin/tasks/');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('tasks', $data);
    }

    private function checkPermissions()
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

        if (!$user->isAdmin()) {
            $this->redirect('/');
        }

        Logger::logMessage('Take in the admin panel');
    }
}