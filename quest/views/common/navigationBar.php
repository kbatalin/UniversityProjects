<?php View::render('common/timer');?>

<div class="navigationBar">
    <?php

    $gonna = 'https://www.youtube.com/watch?v=dQw4w9WgXcQ';

    $pages = array(
        'Главная' => '/index/index/',
//        'Магазин' => '/shop/index/',
//        'Инвентарь' => '/team/inventory/',
//        'Письмо от знахарки' => '/hard-way/customer-office/',
//        'Моя история' => '/hard-way/index/',
        'АДМИНКА' => !empty($trueAdmin) ? 'http://ccfit.nsu.ru/~lihachev/posv/roketi.html' : $gonna,
        'ЛАРЁК' => '/index/shop',
    );

    $pagesCount = count($pages);
    $currentPage = !empty($currentPage) ? trim($currentPage) : '';

    foreach ($pages as $name => $url) {
        if (strcmp($url, $currentPage) == 0) {
            echo ' ' . $name . ' ';
        } else {
            echo ' <a href="' . $url . '">' . $name . '</a> ';
        }

        if (--$pagesCount > 0) {
            echo '&centerdot;';
        }
    }

    $user = App::getInstance()->get('user');
    if ($user && $user->isAdmin()) {
        echo ' &centerdot; <a href="/admin/">Админка</a>';
    }
    ?>

    <a href="/login/logout/" style="float: right">Выйти</a>
</div>