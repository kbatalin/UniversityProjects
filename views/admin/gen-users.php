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
        <?php View::render('admin/navigationBar', array('currentPage' => '/admin/gen-users/')); ?>
        <div class="substrate">
            <?php
            if (empty($results)) { ?>

                <form action="/admin/gen-users/" method="post">
                    <p>
                        Источник ФИТа:
                        <br>
                        <textarea name="fit" cols="120" rows="10">
http://fit.nsu.ru/uch/22-uch/1503-spiski-grupp-1-kurs
                            </textarea>
                    </p>
                    <p>
                        Источник ФИЯ:
                        <br>
                        <textarea name="fija" cols="120" rows="10">
http://www.nsu.ru/spiski
                            </textarea>
                    </p>
                    <p>
                        <input type="submit" value="Продолжить">
                    </p>
                </form>

            <?php } else { ?>
                <p>
                    Результат:
                </p>
                <p>
<textarea style="width: 100%; height: 200px;">
<?php
$group = 0;
foreach ($results as $res) {
    if ($res['group'] != $group) {
        echo $res['group'] . ":\n";
        $group = $res['group'];
    }
    echo ($res['added'] ? '+ ' : '- ') . $res['firstname'] . ' ' . $res['lastname'] . ': ' . $res['login'] . ' ' . $res['pass'] . "\n";
}
?>
</textarea>
                </p>
            <?php } ?>

        </div>
    </div>
</div>
</body>
</html>