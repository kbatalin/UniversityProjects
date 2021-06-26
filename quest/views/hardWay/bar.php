<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/bar/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>Страж трактира угрюмо посмотрел на вас и промолвил: “Хочу услыхать от вас пральные слова! А то не
                пущу!”</p>
            <p>
            <form action="/hard-way/bar/" method="post">
                <input type="text" name="answer" placeholder="Пароль">
                <input type="submit" value="Отправить">
            </form>
            </p>
            <p>
                <?php
                if (!empty($result)) {
                    echo 'Ответ: ' . $result;
                }
                ?>
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
