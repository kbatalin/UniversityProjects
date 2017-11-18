<?php

class Task
{
    private $_id;
    private $_name;
    private $_text;
    private $_points;
    private $_answer;
    private $_visible;
    private $_deadline;
    public $_url;

    /**
     * @param mixed $id
     */
    public function setId($id)
    {
        $this->_id = $id;
    }

    /**
     * @param mixed $name
     */
    public function setName($name)
    {
        $this->_name = $name;
    }

    /**
     * @param mixed $text
     */
    public function setText($text)
    {
        $this->_text = $text;
    }

    /**
     * @param mixed $points
     */
    public function setPoints($points)
    {
        $this->_points = $points;
    }

    /**
     * @param mixed $answer
     */
    public function setAnswer($answer)
    {
        $this->_answer = $answer;
    }

    /**
     * @param mixed $visible
     */
    public function setVisible($visible)
    {
        $this->_visible = $visible;
    }

    /**
     * @param mixed $deadline
     */
    public function setDeadline($deadline)
    {
        $this->_deadline = $deadline;
    }

    /**
     * @param mixed $url
     */
    public function setUrl($url)
    {
        $this->_url = $url;
    }



    public static function create($id, $name, $text, $points, $answer, $active, $visible)
    {
        $name = trim($name);
        $text = trim($text);
        $points = trim($points);
        $answer = trim($answer);
        $active = trim($active);
        if (empty($answer)) {
            Logger::logMessage('Can\'t create task with empty name or answer');
            return false;
        }

        App::getInstance()->getDataBase()->beginTransaction();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `tasks` (id, name, task, points, answer, active, visible) VALUES (?,?,?,?,?,?,?)");
        $STH->bindValue(1, empty($id) ? null : $id);
        $STH->bindValue(2, empty($name) ? null : $name, PDO::PARAM_STR);
        $STH->bindValue(3, $text, PDO::PARAM_STR);
        $STH->bindValue(4, $points);
        $STH->bindValue(5, $answer, PDO::PARAM_STR);
        $STH->bindValue(6, $active);
        $STH->bindValue(7, $visible);
        if (!$STH->execute() || $STH->rowCount() == 0) {
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t add task. name: ' . $name);
            return false;
        }

//        $STH = App::getInstance()->getDataBase()->prepare("SELECT `id` FROM `tasks` ORDER BY `id` DESC LIMIT 1");
//        if (!$STH->execute() || !($res = $STH->fetch(PDO::FETCH_ASSOC))) {
//            App::getInstance()->getDataBase()->rollBack();
//            Logger::logMessage('Can\'t get task id. name: ' . $name);
//            return false;
//        }
//        $taskId = $res['id'];

        $taskId = App::getInstance()->getDataBase()->lastInsertId();

        $STH = App::getInstance()->getDataBase()->prepare("INSERT INTO `team_tasks` (team_id, task_id, last_answer, status) SELECT `id`, ?, NULL, ? FROM `teams`");
        $STH->bindValue(1, $taskId);
        $STH->bindValue(2, TeamTask::$TASK_STATUS['EMPTY'][0]);
        if (!$STH->execute()) {
            var_dump($STH->errorInfo());
            App::getInstance()->getDataBase()->rollBack();
            Logger::logMessage('Can\'t add team_tasks for task #' . $taskId);
            return false;
        }

        App::getInstance()->getDataBase()->commit();

        return true;
    }

    public function init($taskId)
    {
        $STH = App::getInstance()->getDataBase()->prepare("SELECT * FROM `tasks` WHERE `id`=? LIMIT 1");
        $STH->bindValue(1, $taskId);
        if (!$STH->execute()) {
            Logger::logMessage('Can\'t select task info. Task #' . $taskId);
            return false;
        }

        $res = $STH->fetch(PDO::FETCH_ASSOC);
        if (!$res) {
            Logger::logMessage("Can't find task " . $taskId);
            return false;
        }

        $this->_id = $res['id'];
        $this->_name = trim($res['name']);
        $this->_text = trim($res['task']);
        $this->_points = $res['points'];
        $this->_answer = trim($res['answer']);
        $this->_visible = $res['visible'];
        $this->_deadline = $res['deadline'];

        return true;
    }

    function checkAnswer($answer)
    {
        $answer = trim($answer);
        $answer = mb_ereg_replace(' ', '', $answer);

        $rightAnswer = mb_ereg_replace(' ', '', $this->_answer);

        return strcmp(mb_strtolower($answer), mb_strtolower($rightAnswer)) == 0;
    }

    public function getId()
    {
        return $this->_id;
    }

    public function getName()
    {
        return $this->_name;
    }

    public function getText()
    {
        return $this->_text;
    }

    public function getPoints()
    {
        return $this->_points;
    }

    public function getAnswer()
    {
        return $this->_answer;
    }

    public function getDeadline()
    {
        return $this->_deadline;
    }

    public function isVisible()
    {
        return $this->_visible;
    }

    public function __toString()
    {
        return 'Task #' . $this->_id . ' (' . $this->_name . ')';
    }

    public static function getTask5($lang)
    {
        $lang = trim(mb_strtolower($lang));

        if (strcmp('es', $lang) == 0) {
            return 'Juan Blanco es un estudiante de la Universidad Estatal de Novosibirsk. Estudia las siencias humanas y quiere trabajar en un periodico. Ahora tiene que hacer muchas cosas, por ejeplo estudiar. Pero vive en la residencia con sus amigas Pablo y Pedro por eso es bastante difícil concentrarse cuando alguien de su habitación hace bromas o escucha la música por la noche.Por supuesto, Juan se distrae mucho y va a dormir muy tarde.Cuando Juan egresa después de sus estudios a las 12 los chicos duerman porque tienen las clases bastante tarde. Entonces él no le gusta esta situación pero no tiene ninguna oportunidad a cambiar eso.Los chicos son tus amigos y no quere vivir con alqien más. Pero con principio Juan va a hablar con ellos. Probablamente Pablo y Pedro están de acuerdo en escuchar la música a travéz de auriculares y bajar la voz cuando él estudia. 
Pero hay otras distracciones. En el grupo de Juan hay una chica que es muy tal en la lingüística, no entiende nado en esta discipina. Le llama Margo. Juan no puede negarle porque le gusta mucho pasar el tiempo a ella. A veces haze sus deberes de casa plenamente. Sente que Margo lo usa y no tiene sentimientos pon él. Eso le da mucha tristeza, entonces va a erminar eso voluntoriado. 
La último problemo es que nuestro Juan está enpezando a entender que odia el clima en Novosibirsk porque hace muy frío por la tanto quiere regresa a España y elegir una universidad allí';
        }

        if (strcmp('de', $lang) == 0) {
            return 'Eigentlich heyße ich Richart. Aber alle schreiben „Richard“. Ich muss immer meinen Namen buzkstabieren. Es ist schlecht. Man sollen das alles prüfen. Zum Beispiel, heute verkaufe ich der Auto. Das kostet fast vierzih Tousend Euro. Und man muss meinen Namen in den Dokumenten richtig schreibe! Das ist tsiemlich wichtig. Vil Geld. Sehr wichtig.
Geztern habe ich einen Brief bekommen. Ich lese ihn jetzt. Da stehd: „Rischard! Wi warten auf Sie auf der Poswjaga!“. Wieder?! Gibt es diesen Ort in der Welt, wa man meinen Namen richtig schreibt? Ok, ich komme, aber ich brauche viel Alkogol auf der Poswjaga. Und den Prief brenne ich.
Ich rufe meynen Freund Franz an. Ziebenmal – er schläft wie immer. Endlich antwortet er.
“Guten negroM!“
„Was?“
„Franz! Es ist eine Chiffre. Bist du was, tsu dumm?“
„Nein, ich verstehe. Was willst doo?“
„Kommst du auf die Poswjaga?“
„Hmmmmm. Ich habe kein Keld.“
„Ich gebe dir zehn Eura. Ich verkaufe heute mein Auto!“
„Nicht bilig, denke ich?“
„Genug. So was, gehst tu?“
„Mit deinem Geld – natürlich!“
Das ist daz. Ich sehe im Fenster einen Mann – er möchte mit mir sbrechen.
„Auf Wedersehe, Franz.“
Ich gehe  hinaus.
„Etschuldingung, sind Sie Herr Rischard?“ – fragt der Mann.
„Ja. Aber nicht Rischard, sondern Rischart, mit T!“
„Aber in den Autopapiren steht Rischard mit D.“
.
.
.
Ich hasse mei Leben.
';
        }

        if (strcmp('fr', $lang) == 0) {
            return 'Vous avez ouvert le porte. Les gouttes de pluie vous accueillent amicalement, mais leur resignation n\'est pas accessible pour vous. Vous avez déjà fait le premier pas, mais le second es plus difficile. Qu\'attendez-vous ? "Unam in armis salutem". Quelqu\'un a choisit le suiside, mais ce n\'est pas le bon choix, parce que les autres commenceront à envier. À vrai dire, la connaissance est une chose très intéressante. Une travail quotidienne, au total, profitera à vous, et après, vous direz, que les devoirs ennuyeux étaient très importants. Bien-sûr, tout peut se passer autrement, personne ne sais qu\'on attend derrière la porte suivante.';
        }

        if (strcmp('zh', $lang) == 0) {
            return '大卫：你好，我叫大卫。
玛丽：你好，我是玛丽，认识你很高兴。大卫，你有什么事？
大卫：听说你可以卖有名的旧书。
玛丽：是啊。我是商堵。你要买那本书？
大卫：我找一本书，叫《周易》。我去了图书馆借这本书，不过我没借了。图书馆没有这本搞典。
玛丽：我也没有《周易》，因为这本书在中国的博物馆。我有别的旧书，你看看吧。现在我打算喝茶和吃饭，你想吗？
大卫：谢谢你，你非常好客！我要喝一杯茶，可是我不想吃这个菜。我不喜欢吃路米条。
玛丽：OK。我的朋友去了树贫捡特别草，所以茶很好喝！
大卫：真不错！你喜欢看什么书？
玛丽：现在我看《奥菜丽赫本传》。她真漂亮, 还真聪明！
大卫：多有意思啊。我以为了你喜欢看旧书。
玛丽：不是。我很喜欢看不同书。你想买一本书吗？
大卫：我不想，可是我很饿。你有什么吃饭？
玛丽：真不好意思，我才有路米条，不过你不喜欢吃这个菜。
大卫：没关系。现在我要去饭馆吃米饭。你想一起去吗？
玛丽：去吧，我也很饿！

注意你不应该找语录，而是应该找别的资料。
';
        }

        if (strcmp('it', $lang) == 0) {
            return 'Attenzione! Non scrivete il titolo del libro, ma cercate il autore della farse.
I DUI MATIMONI
In Italia ciò contrarere matrimonio in conicipio, con rito civile, e in chiesa, con rito religioso. Per la Chiesa un uomo ed una donna uniti in matrimonio dal sindaco, invece che dal parroco, non sono marito e molie.
Prima dell’11 febbraio 1929, lo Stato si comportava allo stesso modo nei confronti della Chiesa: il matrimonio religioso non amova alcun valore per la legre dello Stato. I due sposi “religiosi” conservavano lo stato civile precedente al matrimonio: lui rimaneva scapolo, lei nubile, e i figli erano dunque illegittimi.
Con il Concordato fra Stato e Chiesa la situazione è cambiata. Lo Stato riconosce gli effetti civili di matrimonio religioso, sicchè i cittadini sono liberi di scegliere fra un matrimonio civile o quello religiose.
';
        }

        Logger::logMessage('Bad lang (task): ' . $lang);
        return '';
    }

    public static function checkTask5($lang, $answer)
    {
        $lang = trim(mb_strtolower($lang));
        $answer = trim(mb_strtolower($answer));

        if (strcmp('es', $lang) == 0) {
            return strcmp('собака на сене', $answer) == 0;
        }

        if (strcmp('de', $lang) == 0) {
            return strcmp('румпельштильцхен', $answer) == 0;
        }

        if (strcmp('fr', $lang) == 0) {
            return strcmp('Утраченные иллюзии', $answer) == 0;
        }

        if (strcmp('zh', $lang) == 0) {
            return strcmp('Сон в красном тереме', $answer) == 0;
        }

        if (strcmp('it', $lang) == 0) {
            return strcmp('Галилео Галилей', $answer) == 0;
        }

        Logger::logMessage('Bad lang (answer): ' . $lang);
        return false;
    }
}