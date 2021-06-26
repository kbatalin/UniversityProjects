<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/index/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>

            <form action="/hard-way/" method="post">
                <p>
                    <textarea name="text" style="width: 100%; height: 300px;"><?php
                        if (!empty($step2Text)) echo htmlspecialchars($step2Text);
                        ?></textarea>
                </p>
                <p>
                    <input type="submit" value="Сохранить">
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
