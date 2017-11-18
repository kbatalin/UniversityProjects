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

        if (!empty($user->getTeam())) {
            $data['userTeam'] = $user->getTeam()->getName();
            $data['tasks'] = $user->getTeam()->getTasks();
            $data['teamScores'] = $user->getTeam()->getPoints();

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
}