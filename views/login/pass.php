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
            <form action="/login/pass/" method="post">
                <p>
                    <?php
                    if (!empty($status)) {
                        echo '<p>' . htmlspecialchars($status) . '</p>';
                    }
                    ?>
                    <input type="email" name="email" placeholder="Email от аккаунта"
                           value="<?php if (!empty($email)) echo htmlspecialchars($email); ?>">
                    <input type="submit" name="submit" value="Восстановить">
                </p>
                <p>
                    <a href="/login">Назад</a>
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>