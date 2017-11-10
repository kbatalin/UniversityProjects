<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => '/hard-way/dragon-software/')); ?>
        <div class="substrate">
            <h3><?php echo htmlspecialchars($pageInfo['title']); ?></h3>
            <ol>
                <li>
                    <p>
                        <a href="#" onclick="document.getElementById('answer1').style.display = 'block';">-Хэй, Горыныч,
                            а не видел ли ты Лешего? Все в лесу его обыскались, найти не могут.</a>
                    </p>
                    <div id="answer1" style="display: none;">
                        <ul>
                            <li>
                                -Что? Да о чём ты говоришь?! Я уже второй час не могу найти свою любимую жижу со вкусом
                                клубники и печеньки, а ты мне тут про Лешего заливаешь!
                            </li>
                            <li>
                                -Да надоел ты уже с этой своей пароварной машиной, в конце-то концов, сколько можно! Ох,
                                вот раньше-то такого не было.
                            </li>
                            <li>
                                -Да уж, не говори, совсем какой-то бедлам творится, чёрт знает что происходит. Леший?
                                Тот, что в лесу главный? Слыхал, слыхал. Видал, Видал.
                            </li>
                        </ul>


                        <p>
                            <a href="#" onclick="document.getElementById('answer11').style.display = 'block';">-А где же
                                ты встречал Лешего?</a>
                        </p>

                        <div id="answer11" style="display: none;">
                            <ul>
                                <li>
                                    -*томно делает затяжку и выпускает облако пара в виде дерева*
                                </li>
                                <li>
                                    -*второй фэйспалм хвостом по лицу*
                                </li>
                                <li>
                                    -Да, вроде как, помощи Леший просил, злато ему нужно было, подработать хотел
                                    немного.
                                </li>
                            </ul>


                            <p>
                                <a href="#" onclick="document.getElementById('answer111').style.display = 'block';">-И
                                    что же, помог ты ему?</a>
                            </p>

                            <div id="answer111" style="display: none;">
                                <ul>
                                    <li>
                                        -Ага, поможет он, он даже жижу помочь найти не может, а ведь мой вэйп гораздо
                                        безопаснее того, что извергает его пасть!
                                    </li>
                                    <li>
                                        -Да как ты смеешь так разговаривать, что стало с твоими манерами?
                                    </li>
                                    <li>
                                        -Помог, а как же! *укоризненно смотрит вслед очередному облаку пара в виде
                                        дерева* Он даже был частично принят на работу!
                                    </li>
                                </ul>
                            </div>

                        </div>

                    </div>
                </li>
                <li>
                    <p>
                        <a href="#" onclick="document.getElementById('answer2').style.display = 'block';">- О всаднике
                            пропавшем ты ничего не слыхивал, Горыныч?</a>
                    </p>
                    <div id="answer2" style="display: none;">
                        <ul>
                            <li>
                                -Оооох, не только моя жижечка пропала, значит.
                            </li>
                            <li>
                                -Да угомонишься ты вообще или нет? Что вообще с тобой творится? Один пар вокруг тебя.
                            </li>
                            <li>
                                -Ой-ёй-ёй, да что же это происходит-то, что происходит… Нет, не знаю ничего.
                            </li>
                        </ul>
                    </div>
                </li>
                <li>
                    <p>
                        <a href="#" onclick="document.getElementById('answer3').style.display = 'block';">- Как жизнь
                            твоя, Горыныч?</a>
                    </p>
                    <div id="answer3" style="display: none;">
                        <ul>
                            <li>
                                -Купил себе вот новую забаву, диковинку, вэйпом называется. Намного безопаснее пламени
                                нашего, ничего не спалит, ни дом ничей, ни лес, тем более. Теперь сеть магазинов жижи
                                открыл!
                            </li>
                            <li>
                                -Странный ты какой-то, раньше таким не был.
                            </li>
                            <li>
                                -*фэйспалмом бьёт хвостом по лицу*
                            </li>
                        </ul>
                    </div>
                </li>
            </ol>

            <script>

            </script>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>
</body>
<?php View::render('common/metrics'); ?>
</html>
