<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/leshy-house/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>
                В доме (если это место можно назвать домом) пусто. Настолько пусто,
                что даже бедным тараканам прятаться негде. Наблюдая за их исходом, вы замечаете, что они что-то забрали
                с собой.
                В тяжёлой борьбе два на два вы забираете у них потрёпанную плёнку...
            </p>
            <p align="center">
                <iframe width="800" height="450" src="https://www.youtube.com/embed/aC_D9iQ62gY?rel=0" frameborder="0"
                        allowfullscreen></iframe>
            </p>
            <p align="center">
                <img src="/img/maps/baba-yaga.jpg" width="800" height="357">
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
