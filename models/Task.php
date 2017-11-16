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

//        if ($this->_id == 952) {
//            $answer = mb_ereg_replace('!', '', $answer);
//        }
        return strcmp(mb_strtolower($answer), mb_strtolower($this->_answer)) == 0;
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

        Logger::logMessage('Bad lang (answer): ' . $lang);
        return false;
    }
}