<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/team/inventory/')); ?>
        <div class="substrate">

            <table class="tasksTable">
                <?php if (empty($items)) { ?>
                    <p>
                        Ваша команда еще ничего не покупала
                    </p>
                <?php } else { ?>
                    <thead>
                    <tr>
                        <th>Фото</th>
                        <th>Название</th>
                        <th>Описание</th>
                        <th>Количество</th>
                    </tr>
                    </thead>
                    <tbody>
                    <?php

                    $i = 0;
                    foreach ($items as $item) {
                        $style = $i++ % 2 ? ' class="gray"' : '';
                        $itemName = htmlspecialchars($item->getName());
                        $itemDesc = htmlspecialchars($item->getDescription());
                        $itemCount = htmlspecialchars($item->getCount());
                        $itemImg = '<img src="' . htmlspecialchars($item->getImg()) . '" width="50" height="50">';

                        echo <<<HTML
                <tr {$style}>
                    <td>{$itemImg}</td>
                    <td>{$itemName}</td>
                    <td>{$itemDesc}</td>
                    <td>{$itemCount}</td>
                </tr>

HTML;
                    }

                    ?>

                    </tbody>
                <?php } ?>
            </table>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>