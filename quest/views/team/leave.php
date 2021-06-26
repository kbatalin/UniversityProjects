<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => 'leave_team')); ?>
        <div class="substrate">
            <p>
                Вы уверены, что хотите выйти из команды?<br>
                Все достижения будут утеряны.
            </p>
            <p>
                <a href="/team/leave/?confirm=1">Выйти из команды</a>
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>