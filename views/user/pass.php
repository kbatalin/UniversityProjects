<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => 'pass')); ?>
        <div class="substrate">
            <form action="/user/pass/" method="post">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                }
                ?>
                <p>
                    <input type="password" placeholder="Старый пароль" name="oldPass">
                </p>
                <p>
                    <input type="password" placeholder="Новый пароль" name="newPass">
                </p>
                <p>
                    <input type="password" placeholder="Повторите пароль" name="newPass2">
                </p>
                <p>
                    <input type="submit" value="Изменить">
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>