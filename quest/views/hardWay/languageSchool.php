<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/language-school/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>
            <form action="/hard-way/language-school/" method="post">
                <input type="text" name="answer" placeholder="Промокод">
                <input type="submit" value="Отправить">
            </form>
            </p>
            <p>
                <?php
                if (!empty($result)) {
                    echo $result;
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
