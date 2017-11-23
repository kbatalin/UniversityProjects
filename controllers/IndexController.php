<?php

class IndexController extends Controller
{
    public function actionIndex()
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

        $data = array(
            'userLogin' => $user->getLogin(),
            'userFirstname' => $user->getFirstname(),
            'userLastname' => $user->getLastname(),
        );

        if (strcmp($user->getLogin(), 'KIMCHENblH') == 0) {
            $data['trueAdmin'] = true;
        }

        if (!empty($user->getTeam())) {
            $data['userTeam'] = $user->getTeam()->getName();
            $data['tasks'] = $user->getTeam()->getTasks();
            $data['teamScores'] = $user->getTeam()->getPoints();

            /*
             * Жилье недорого” https://goo.gl/37CV9f
“Вкусная, свежая шаурма, прекрасная как первая любовь” https://goo.gl/DsPdMn
“Курсы по английскому" https://goo.gl/u57G4F
"Живи красиво" https://goo.gl/NwJgUi
"Жиза" https://goo.gl/u4X4PT
             */

            $urls = array(
                'Жилье недорого' => 'https://goo.gl/37CV9f',
                'Вкусная, свежая шаурма, прекрасная как первая любовь' => 'https://goo.gl/DsPdMn',
                'Курсы по английскому' => 'https://goo.gl/u57G4F',
                'Живи красиво' => 'https://goo.gl/NwJgUi',
                'Жиза' => 'https://goo.gl/u4X4PT',
                'Внимание! Черная пятница!' => '/index/shop',
            );

            $ttt = array();
            foreach ($urls as $k => $v) {
                $tmp = new TeamTask();
                $tmp->setStatus(' ');
                $tmp->setTask(new Task());
                $tmp->getTask()->setVisible(true);
                $tmp->getTask()->setUrl($v);
                $tmp->getTask()->setName($k);
                $ttt[] = $tmp;
            }

            if (count($ttt) > count($data['tasks'])) {
                $a = $ttt;
                $b = $data['tasks'];
            } else {
                $a = $data['tasks'];
                $b = $ttt;
            }

            $ia = 0;
            $ib = 0;
            $res = array();
            for (; $ia < count($a) || $ib < count($b);) {
                if ($ia < count($a)) {
                    if ($ib == 0 || $b[$ib - 1]->getTask()->isVisible()) {
                        $res[] = $a[$ia];
                        ++$ia;
                    }
                }

                if ($ib < count($b)) {
                    if ($ia == 0 || $a[$ia]->getTask()->isVisible()) {
                        $res[] = $b[$ib];
                        ++$ib;
                    }
                }
//                $res[] = $b[$i];
            }

//            for ($i = count($b); $i < count($a); ++$i) {
//                $res[] = $a[$i];
//            }

            $data['tasks'] = $res;

            $data['language'] = $user->getTeam()->getLanguage();
            if (!empty($data['language'])) {
                $data['language'] = Language::$enum[$data['language']];
            }

            $members = $user->getTeam()->getMembers();
            foreach ($members as $member) {
                if ($member->getId() != $user->getId()) {
                    $data['teamPartner'] = $member;
                    break;
                }
            }
        }

        if (!empty($user->getDepartment())) {
            $data['userDepartment'] = $user->getDepartment()->getName();
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('index');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('index', $data);
    }

    public function actionShop()
    {
        $this->render('shop', array('pageInfo' => array('title' => 'ЛАРЁК')));
    }
}