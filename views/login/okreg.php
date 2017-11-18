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
            <form action="/login/restore/" method="get">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                } else if (!empty($_GET['id']) && !empty($_GET['code'])) { ?>
                    <p>
                        Ваш пароль выслан на почту.
                    </p>
                <?php } ?>
                <p>
                    <a href="/login/">На страницу авторизаии</a>
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>