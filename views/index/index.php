<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
    <script src="/js/taskTable.js"></script>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/index/index/')); ?>
        <div class="row">
            <div class="col2">
                <p>
                    Логин: <?php if (!empty($userLogin)) echo htmlspecialchars($userLogin); ?> <br>
                    Имя: <?php if (!empty($userFirstname)) echo htmlspecialchars($userFirstname); ?> <br>
                    Фамилия: <?php if (!empty($userLastname)) echo htmlspecialchars($userLastname); ?> <br>
                    Факультет: <?php if (!empty($userDepartment)) echo htmlspecialchars($userDepartment); ?>
                </p>
                <p>
                    <a href="/user/pass/">Сменить пароль</a> или <a href="/user/email/">email</a>
                </p>

                <hr>
                <?php
                if (!empty($userTeam)) {
                    ?>
                    <p>
                        Команда: <?php echo htmlspecialchars($userTeam); ?>
                        (<a href="/team/leave/">Покинуть</a>)
                    </p>

                    <?php if (!empty($teamPartner)) {
                        $name = htmlspecialchars($teamPartner->getFirstname() . ' ' . $teamPartner->getLastname());
                        $teamScores = !empty($teamScores) ? htmlspecialchars($teamScores) : '0';
                        echo <<<HTML
                <p>
                    Напарник: {$name} <br>
                    Очки: {$teamScores} <br>
                </p>

HTML;
                    }
                } else { ?>
                    <p>
                        <a href="/team/create/">Создать команду</a>
                    </p>

                <?php } ?>

            </div>

            <div class="col2">

                <table class="tasksTable">
                    <thead>
                    <tr>
                        <th>Задача</th>
                        <th>Макс. очки</th>
                        <th>Статус</th>
                    </tr>
                    </thead>
                    <tbody>
                    <?php
                    if (!empty($tasks)) {
                        $i = 0;
                        foreach ($tasks as $task) {
                            if (!$task->getTask()->isVisible()) {
                                continue;
                            }
                            $style = $i++ % 2 ? ' class="gray"' : '';
                            $taskId = htmlspecialchars($task->getTask()->getId());
                            $taskName = htmlspecialchars($task->getTask()->getName());
                            $taskPoints = htmlspecialchars($task->getTask()->getPoints());
                            $taskStatus = htmlspecialchars($task->getStatusText());
                            echo <<<HTML
                    <tr {$style}>
                        <td><a href="/task/?id={$taskId}">{$taskName}</a></td>
                        <td>{$taskPoints}</td>
                        <td>{$taskStatus}</td>
                    </tr>

HTML;
                        }
                    } else {
                        echo '<td colspan="3">Для того, чтобы начать решать задачи, вступите в команду</td>';
                    }
                    ?>

                    </tbody>
                </table>

            </div>
        </div>

        <?php View::render('common/footer', null); ?>

    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>