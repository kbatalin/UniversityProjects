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
            <form action="/user/confirm/" method="get">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                }
                ?>
                <p>
                    Внимание! Письмо может доставляться очень долго, а после этого попасть в папку Спам
                </p>
                <p>
                    Код подтверждения:
                    <input type="text" name="code" value="<?php if (!empty($code)) echo htmlspecialchars($code); ?>">
                </p>
                <p>
                    <input type="submit" name="submit" value="Продолжить">
                </p>
                <p>
                    <a href="/user/email/">Сменить email</a>
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>