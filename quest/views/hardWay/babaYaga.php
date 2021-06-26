<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/baba-yaga/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>
                У входа вас встретила Баба Яга.<br>
                — Ой, простите-извините меня, люди добрые! Совсема запурхалася я! Тяжко-то одной работать!
                Вот побяжала детишек Водяного учить... Но я вам отвар вкусный приготовила и печенюшек из лягушек
                испекла. А к Кощею можно входити токмо особым гостям! Так что по пустякам болезного не тревожьте!<br>
                На столе стоял графин с кислотного цвета жидкостью и приклеенной бумажкой, на которой было написано:
                “Горная роса”, а также тарелка с печеньем. Печенье выглядело вполне аппетитным, пока не начало квакать
                и ловить мух.<br>
                На полке книжного шкафа выделялась одна рукопись. Вы решили взглянуть на неё и обнаружили
                на обложке слова: “Мой дневник”...<br>
            </p>
            <p><a href="https://twitter.com/babka__yaga?s=09">Дневник Бабы Яги</a></p>
        </div>

        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
