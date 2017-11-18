<?php

class HardWayController extends Controller
{
//    public function actionIndex()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionIndex. Team #' . $team->getId());
//
//        if (!empty($_POST['text'])) {
//            $team->setStep2Text($_POST['text']);
//        }
//
//        $data['step2Text'] = $team->getStep2Text();
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/index/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('index', $data);
//    }
//
//    public function actionCustomerOffice()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionCustomerOffice. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/customer-office/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('customerOffice', $data);
//    }
//
//    public function actionLeshyHouse()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionLeshyHouse. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/leshy-house/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('leshyHouse', $data);
//    }
//
//    public function actionKikimoraPond()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionKikimoraPond. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/kikimora-pond/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('kikimoraPond', $data);
//    }
//
//    public function actionLanguageSchool()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionLanguageSchool. Team #' . $team->getId());
//
//        $answers = array(
//            'JCrmck1970'
//        );
//
//        $data = array();
//
//        if (!empty($_POST['answer'])) {
//            $userAnswer = mb_strtolower(mb_ereg_replace(' ', '', trim($_POST['answer'])));
//
//            foreach ($answers as $answer) {
//                $answer = mb_strtolower(mb_ereg_replace(' ', '', $answer));
//
//                if (strcmp($userAnswer, $answer) == 0) {
//                    Logger::logMessage('Step2: language. Good answer ' . trim($_POST['answer']));
//                    $data['result'] = '<img align="center" src="/img/koschei.jpg" width="800" height="873"><br><br>
//                Мистер Цунг открыл вам дверь. Перед собой вы увидели старца, пафосно сидящего на троне. Он смотрел на
//                вас в течение трёх минут, изучая каждый сантиметр ваших тел, а потом обратился к вам...<br>
//                <a href="/files/koschey.wav">Прослушать</a><br>
//                <a href="/hard-way/hh-ru/">hh.ru</a>';
//                    break;
//                }
//            }
//
//            if (empty($data['result'])) {
//                Logger::logMessage('Step2: language. Bad answer ' . trim($_POST['answer']));
//                $data['result'] = 'Неверный промокод';
//            }
//        }
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/language-school/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('languageSchool', $data);
//    }
//
//    public function actionBar()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionBar. Team #' . $team->getId());
//
//        $answers = array(
//            'у нас не закрывают снаружи',
//            'у нас не запирают снаружи',
//            'мы не запираем снаружи',
//            'мы не закрываем снаружи',
//            'у нас не закрывают людей снаружи',
//            'у нас не запирают людей снаружи',
//            'мы не запираем людей снаружи',
//            'мы не закрываем людей снаружи'
//        );
//
//        $data = array();
//
//        if (!empty($_POST['answer'])) {
//            $userAnswer = mb_strtolower(mb_ereg_replace(' ', '', trim($_POST['answer'])));
//            if (preg_match('/[a-z]/is', $userAnswer) != 0) {
//                $data['result'] = 'Не понимаю, братец, это на заморском?';
//            } else {
//
//                foreach ($answers as $answer) {
//                    $answer = mb_strtolower(mb_ereg_replace(' ', '', $answer));
//
//                    if (strcmp($userAnswer, $answer) == 0) {
//                        Logger::logMessage('Step2: bar. Good answer ' . trim($_POST['answer']));
//                        $data['result'] = '<a href="/files/hornpub.apk">HornPub</a>';
//                        break;
//                    }
//                }
//
//                if (empty($data['result'])) {
//                    Logger::logMessage('Step2: bar. Bad answer ' . trim($_POST['answer']));
//                    $data['result'] = 'Неверный пароль';
//                }
//            }
//        }
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/bar/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('bar', $data);
//    }
//
//    public function actionForest()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionForest. Team #' . $team->getId());
//
//        if (!empty($_POST['sellHoney'])) {
//            $honey = new Item();
//            $honey->init(12, 1);
//            if ($team->sellItem($honey)) {
//                $data['result'] = 'О, спасибо! Сколько он там стоил? Чек вижу! 170? Возвращаю всё до монеты! А, историю
//                хотите? Скажем, так, истории не будет. Нет. Но скажу один интересный факт: в наших краях на всех голов
//                не хватает. Так что вот!';
//            } else {
//                $data['result'] = $team->getLastError();
//            }
//        }
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/forest/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('forest', $data);
//    }
//
//    public function actionDragonSoftware()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionDragonSoftware. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/dragon-software/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('dragonSoftware', $data);
//    }
//
//    public function actionBogatyr()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionBogatyr. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/bogatyr/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('bogatyr', $data);
//    }
//
//    public function actionHhRu()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionHhRu. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/hh-ru/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('hhRu', $data);
//    }
//
//    public function actionBabaYaga()
//    {
//        $this->checkLogin();
//
//        $user = App::getInstance()->get('user');
//        $team = $user->getTeam();
//
//        Logger::logMessage('View step2 actionBabaYaga. Team #' . $team->getId());
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/hard-way/baba-yaga/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('babaYaga', $data);
//    }
//
//    private function checkLogin()
//    {
//        $loginModel = new Login();
//        if (!$loginModel->checkAuth()) {
//            $this->redirect('/login/');
//        }
//
//        $user = App::getInstance()->get('user');
//        if (!$user->hasEmail()) {
//            $this->redirect('/user/email/');
//        } else if (!$user->isActive()) {
//            $this->redirect('/user/confirm/');
//        }
//
//        if (!$user->getTeam()) {
//            $this->redirect('/team/create/');
//        }
//    }
}