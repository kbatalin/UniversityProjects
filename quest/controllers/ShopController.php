<?php

class ShopController extends Controller
{
//    public function actionIndex()
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
//        $team = $user->getTeam();
//        if ($team) {
//            $data['teamPoints'] = $team->getPoints();
//            $data['team'] = $team;
//        }
//
//        $shop = new Shop();
//        if ($shop->init()) {
//            $data['items'] = $shop->getItems();
//        }
//
//        $pageInfo = new PageInfo();
//        $pageInfo->init('/shop/index/');
//        $data['pageInfo'] = $pageInfo->getInfo();
//
//        $this->render('index', $data);
//    }
//
//    public function actionBuy()
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
//        if (empty($_GET['item'])) {
//            $this->redirect('/shop/');
//        }
//
//        $team = $user->getTeam();
//        if (empty($team)) {
//            $this->redirect('/');
//        }
//
//        $shop = new Shop();
//        if (!$shop->init()) {
//            Logger::logMessage('Can\'t init shop');
//            $this->redirect('/');
//        }
//
//        $items = $shop->getItems();
//        foreach ($items as $item) {
//            if ($item->getId() == $_GET['item']) {
//                if ($team->buyItem($item)) {
//                    $this->redirect('/team/inventory/');
//                } else {
//                    $this->redirect('/shop/');
//                }
//            }
//        }
//
//        $this->redirect('/shop/');
//    }
}