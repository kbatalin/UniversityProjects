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
            <form action="/login/register" method="post">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                }
                ?>
                <p>
                    ФИТ:
                </p>
                <p>
                    <input type="text" placeholder="Имя" name="fitFirstname"
                           value="<?php if (!empty($fitFirstname)) echo htmlspecialchars($fitFirstname); ?>">
                </p>
                <p>
                    <input type="text" placeholder="Фамилия" name="fitLastname"
                           value="<?php if (!empty($fitLastname)) echo htmlspecialchars($fitLastname); ?>">
                </p>
                <p>
                    <input type="email" placeholder="Email (g.nsu.ru)" name="fitEmail"
                           value="<?php if (!empty($fitEmail)) echo htmlspecialchars($fitEmail); ?>">
                </p>
                <hr>
                <p>
                    ФИЯ:
                </p>
                <p>
                    <input type="text" placeholder="Имя" name="fijaFirstname"
                           value="<?php if (!empty($fijaFirstname)) echo htmlspecialchars($fijaFirstname); ?>">
                </p>
                <p>
                    <input type="text" placeholder="Фамилия" name="fijaLastname"
                           value="<?php if (!empty($fijaLastname)) echo htmlspecialchars($fijaLastname); ?>">
                </p>
                <p>
                    <input type="submit" name="submit" value="Регистрация">
                </p>
            </form>
            <p>
                <a href="/login/">Назад</a>
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>