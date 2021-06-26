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
        <?php View::render('admin/navigationBar', array('currentPage' => '/admin/users/')); ?>
        <div class="row">
            <div class="col2">
                <p>
                    Добавить юзера:
                </p>
                <form action="/admin/users/" method="post">
                    <input type="hidden" name="actionAdd" value="1">
                    <p>
                        <input type="text" name="login" placeholder="Логин*"
                               value="<?php if (!empty($login)) echo htmlspecialchars($login); ?>">
                    </p>
                    <p>
                        <input type="email" name="email" placeholder="Email"
                               value="<?php if (!empty($email)) echo htmlspecialchars($email); ?>">
                    </p>
                    <p>
                        <input type="text" name="department" placeholder="Факультет*"
                               value="<?php if (!empty($department)) echo htmlspecialchars($department); ?>">
                    </p>
                    <p>
                        <input type="text" name="firstname" placeholder="Имя*"
                               value="<?php if (!empty($firstname)) echo htmlspecialchars($firstname); ?>">
                    </p>
                    <p>
                        <input type="text" name="lastname" placeholder="Фамилия*"
                               value="<?php if (!empty($lastname)) echo htmlspecialchars($lastname); ?>">
                    </p>
                    <p>
                        <input type="text" name="pass" placeholder="Пароль*"
                               value="<?php if (!empty($pass)) echo htmlspecialchars($pass); ?>">
                    </p>
                    <p>
                        <input type="text" name="status" placeholder="Статус"
                               value="<?php if (isset($status)) echo htmlspecialchars($status); ?>">
                    </p>
                    <p>
                        <input type="text" name="permissions" placeholder="Права (1 - админ, 0 - обычный юзер)"
                               value="<?php if (isset($permissions)) echo htmlspecialchars($permissions); ?>">
                    </p>
                    <p>
                        <input type="submit"
                               value="Добавить"> <?php if (!empty($addStatus)) echo htmlspecialchars($addStatus); ?>
                    </p>
                </form>

                <hr>

                <p>
                    <a href="/admin/gen-users/">Сгенерировать</a> (Взять списки с nsu.ru)
                </p>

            </div>
            <div class="col2">
                <p>
                    Редактирование юзеров
                </p>
            </div>
        </div>
    </div>
</div>
</body>
</html>