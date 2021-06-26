<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/shop/index/')); ?>
        <div class="substrate">
            <table class="tasksTable">
                <?php if (empty($items)) { ?>
                    <p>
                        Скоро открытие!
                    </p>
                <?php } else {
                    $teamPoints = !empty($teamPoints) ? (int)$teamPoints : 0;
                    ?>
                    <thead>
                    <tr>
                        <th>Фото</th>
                        <th>Название</th>
                        <th>Описание</th>
                        <th>Стоимость</th>
                        <th>Ваши очки: <?php echo $teamPoints; ?></th>
                    </tr>
                    </thead>
                    <tbody>
                    <?php

                    $i = 0;
                    foreach ($items as $item) {
                        $style = $i++ % 2 ? ' class="gray"' : '';
                        $itemId = htmlspecialchars($item->getId());
                        $itemName = htmlspecialchars($item->getName());
                        $itemDesc = htmlspecialchars($item->getDescription());
                        $itemCost = htmlspecialchars($item->getCost());
                        $itemImg = '<img src="' . htmlspecialchars($item->getImg()) . '" width="50" height="50">';

                        $buyText = '';
                        if ($item->getCost() > $teamPoints) {
                            $buyText = 'Недостаточно очков';
                        } else if (!empty($team) && $team->getInventory()->hasItem($item->getId())) {
                            $buyText = 'Вещь уже куплена';
                        } else {
                            $buyText = '<a href="/shop/buy/?item=' . $itemId . '">Купить</a>';
                        }

                        echo <<<HTML
                <tr {$style}>
                    <td>{$itemImg}</td>
                    <td>{$itemName}</td>
                    <td>{$itemDesc}</td>
                    <td>{$itemCost}</td>
                    <td>{$buyText}</td>
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