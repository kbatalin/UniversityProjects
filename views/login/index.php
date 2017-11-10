<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <div class="substrate">
            <form action="/login" method="post">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                }
                ?>
                <p>
                    <input type="text" placeholder="Логин" name="login"
                           value="<?php if (!empty($login)) echo htmlspecialchars($login); ?>">
                </p>
                <p>
                    <input type="password" placeholder="Пароль" name="pass">
                </p>
                <p>
                    <input type="submit" name="submit" value="Войти">
                </p>
            </form>
            <p>
                <a href="/login/pass/">Восстановить пароль</a>
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>