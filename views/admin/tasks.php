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
        <?php View::render('admin/navigationBar', array('currentPage' => '/admin/tasks/')); ?>
        <div class="row">
            <div class="col2">
                <p>
                    Добавить задание:
                </p>
                <form action="/admin/tasks/" method="post">
                    <input type="hidden" name="actionAdd" value="1">
                    <p>
                        <input type="text" name="id" placeholder="id"
                               value="<?php if (!empty($id)) echo htmlspecialchars($id); ?>">
                    </p>
                    <p>
                        <input type="text" name="name" placeholder="Название"
                               value="<?php if (!empty($name)) echo htmlspecialchars($name); ?>">
                    </p>
                    <p>
                        Задание:<br>
                        <textarea name="task"
                                  style="width: 400px; height: 100px;"><?php if (!empty($task)) echo htmlspecialchars($task); ?></textarea>
                    </p>
                    <p>
                        <input type="text" name="points" placeholder="Очки"
                               value="<?php if (!empty($points)) echo htmlspecialchars($points); ?>">
                    </p>
                    <p>
                        <input type="text" name="answer" placeholder="Ответ"
                               value="<?php if (!empty($answer)) echo htmlspecialchars($answer); ?>">
                    </p>
                    <p>
                        <input type="text" name="visible" placeholder="Видимый: 1 - да, 0 - нет"
                               value="<?php if (!empty($visible)) echo htmlspecialchars($visible); ?>">
                    </p>
                    <p>
                        <input type="submit"
                               value="Добавить"> <?php if (!empty($addStatus)) echo htmlspecialchars($addStatus); ?>
                    </p>
                </form>
            </div>
            <div class="col2">
                <p>
                    Редактирование заданий
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>