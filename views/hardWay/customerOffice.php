<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/customer-office/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <p>
                Здравствуйте, добрые молодцы и красные девицы!<br>
                Живу я за тридевять земель, да знахаркой являюсь в лесу далёком.<br>
                Принесла я вам весть печальную. Беда к нам пришла, что с ней поделать, как справиться, не знаю.
                Совсем Леший лес запустил, не следит за порядком, подевался куда-то. День и ночь ко мне и старый и
                малый приходит за помощью и советом. Болеют зверята, вянут растения, а Лешему хоть бы хны! Волки совсем
                распоясались, бедным зайчатам и бельчатам проходу не дают. Что делать, право не знаю!<br>
                Помогите, спасите от ненастья! Отыщите Кощея окаянного! Сама хотела домой к нему или к Кикиморе на
                болото наведаться, но уж больно занята я, не успеваю совсем!<br>
                Но люди добрые помогли, показали мне диковинки заморские. Есть я в сетях интернетных, а найти
                вы меня там можете вот здесь <a href="https://vk.com/id132946200">https://vk.com/id132946200</a> да в
                Телеграмме по циферкам +79039044399.
                Помочь и ответить на вопросы всегда буду рада.<br>
                <br>
                Удачи, и помогите нам спасти лес от беды-ненастья!
            </p>
            <div align="center">
                <p>
                    Дом Лешего: <br>
                    <img src="/img/maps/leshy-house.jpg" width="800" height="357">
                </p>
                <hr>
                <p>
                    Болото Кикиморы:<br>
                    <img src="/img/maps/kikimora-pond.jpg" width="800" height="581">
                </p>
            </div>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
