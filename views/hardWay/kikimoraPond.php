<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/kikimora-pond/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>
                Вы чуть не утонули в болоте. Чёрт знает, зачем вы туда полезли, — пора избавляться от этого фетиша.
                Но сегодня удача на вашей стороне, и, мало того, что вы выбрались живым,
                так ещё и обнаружили несколько фотографий на берегу...
            </p>
            <p><a href="https://www.instagram.com/miss_bolotnaya/">Рассмотреть фотографии</a></p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
