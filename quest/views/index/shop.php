<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
    <script src="/js/taskTable.js"></script>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/index/shop/', 'trueAdmin' => !empty($trueAdmin) ? true : false)); ?>

        <div class="substrate">
            <p>
                Обратите внимание, что скоро откроется наш магазин, где вы сможете приобрести товары на любой вкус по
                приятным ценам. Здесь вы можете ознакомиться с ассортиментом. Мы ждем вас с нетерпением!
            </p>
            <br><img src="/img/shop.jpg">
        </div>

        <?php View::render('common/footer', null); ?>

    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>