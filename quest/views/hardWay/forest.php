<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/forest/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>

            <form action="/hard-way/forest/" method="post">
                <input type="hidden" name="sellHoney" value="1">
                <p align="center">
                    <input type="submit" value="Отдать мед">
                </p>
            </form>

            <?php
            if (!empty($result)) {
                echo '<p align="center">
                    ' . htmlspecialchars($result) . '
                </p>  ';
            }
            ?>


            <p align="center">
                <iframe width="800" height="450" src="https://www.youtube.com/embed/iwh1vpWiaXQ?rel=0" frameborder="0"
                        allowfullscreen></iframe>
            </p>
            <p align="center">
                <img src="/img/maps/bogatyr.jpg" width="800" height="447">
            </p>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
