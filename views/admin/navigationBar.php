<div class="navigationBar">
    <?php
    $pages = array(
        'Юзердэнд' => '/index/index/',
        'Главная' => '/admin/index/',
        'Юзеры' => '/admin/users/',
        'Задания' => '/admin/tasks/'
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
    ?>

    <a href="/login/logout/" style="float: right">Выйти</a>
</div>