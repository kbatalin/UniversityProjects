<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => 'task')); ?>
        <div class="substrate">
            <h3><? if (!empty($taskName)) echo htmlspecialchars($taskName); ?></h3>

            <?php
            if (!empty($taskId) && ($taskId == 5) && empty($language)) {
                echo 'Необходимо выбрать язык! <a href="/team/update/">Перейти к выбору</a>';
            } else {
                ?>

                <p>
                    <? /*if(!empty($taskText)) echo nl2br(htmlspecialchars($taskText));*/ ?>
                    <? if (!empty($taskText)) echo nl2br($taskText); ?>
                </p>
                <?php
                if ((empty($isDeadline)) && ((empty($taskStatus) || $taskStatus != TeamTask::$TASK_STATUS['SUCCESS'][0]) && (empty($taskId) || ($taskId != 9)))) {
                    echo <<<HTML
        <form action="" method="post">
        <p>
            <label><input type="text" placeholder="Ответ" name="answer"></label>
        </p>
        <p>
            <input type="submit" value="Ответить">
        </p>
        </form>

HTML;
                }
            }
            ?>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
