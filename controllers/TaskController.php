<?php

class TaskController extends Controller
{
    public function actionIndex()
    {
        $loginModel = new Login();
        if (!$loginModel->checkAuth()) {
            $this->redirect('/login/');
        }

        $user = App::getInstance()->get('user');
        if (!$user->hasEmail()) {
            $this->redirect('/user/email/');
        } else if (!$user->isActive()) {
            $this->redirect('/user/confirm/');
        }

        $teamTask = new TeamTask();
        if (empty($_GET['id']) || !$teamTask->init($user->getTeam()->getId(), $_GET['id'])) {
            $this->redirect('/');
        }

        $data = array(
            'taskId' => $teamTask->getTask()->getId(),
            'taskName' => $teamTask->getTask()->getName(),
            'taskText' => $teamTask->getTask()->getText(),
            'taskStatus' => $teamTask->getStatus(),
        );

        $nowDate = new DateTime();
        $taskDeadline = new DateTime($teamTask->getTask()->getDeadline());
        $isDeadline = $nowDate > $taskDeadline;
        if ((!empty($_POST['answer']) || !empty($_POST['answers'])) && $isDeadline) {
            $this->redirect('/');
        }
        $data['isDeadline'] = $isDeadline;

        //6 задача. Несколько вариантов ответа
        if ($teamTask->getTask()->getId() == 6 && !empty($_POST['answer'])) {
            $answers = json_decode($teamTask->getTask()->getAnswer());
            if (!$answers) {
                Logger::logMessage('Can\' parse answers for 6 task');
                $this->redirect('/');
            }

            $userAnswer = mb_ereg_replace('ё', 'е', mb_ereg_replace(' ', '', mb_strtolower(trim($_POST['answer']))));
            $userAnswer = mb_ereg_replace('-', '', $userAnswer);

            foreach ($answers as $answer) {
                $answer = str_replace(' ', '', mb_strtolower(trim($answer)));
                if (strcmp($answer, $userAnswer) == 0) {
                    Logger::logMessage('Good answer. Task #6. User\'s answer: ' . trim($_POST['answer']));
                    if ($teamTask->setAnswer($userAnswer, TeamTask::$TASK_STATUS['SUCCESS'][0])) {
                        $user->getTeam()->addPoints($teamTask->getTask()->getPoints());
                    }
                    $this->redirect('/');
                    break;
                }
            }

            Logger::logMessage('Bad answer. Task #6. User\'s answer: ' . trim($_POST['answer']));
            $teamTask->setAnswer($userAnswer, TeamTask::$TASK_STATUS['FAIL'][0]);
            $this->redirect('/');
        }

        //7 задача. После первого ответа скачивается файл
        if ($teamTask->getTask()->getId() == 7 && !empty($_POST['answer'])) {
            $answers = json_decode($teamTask->getTask()->getAnswer());
            if (!$answers) {
                Logger::logMessage('Can\' parse answers for 7 task');
                $this->redirect('/');
            }

            $userAnswer = mb_ereg_replace('[, ]', '', mb_strtolower(trim($_POST['answer'])));

            if (strcmp(trim($answers[0]), trim($_POST['answer'])) == 0) {
                Logger::logMessage('Good first answer for task #7. User\'s answer: ' . trim($_POST['answer']));
                $teamTask->setAnswer(trim($_POST['answer']), TeamTask::$TASK_STATUS['EMPTY'][0]);
                $this->redirect('/files/libraryofbabel.txt');
            } else if (strcmp(trim($answers[1]), $userAnswer) == 0) {
                Logger::logMessage('Good final answer. Task #7. User\'s answer: ' . trim($_POST['answer']));
                if ($teamTask->setAnswer($userAnswer, TeamTask::$TASK_STATUS['SUCCESS'][0])) {
                    $user->getTeam()->addPoints($teamTask->getTask()->getPoints());
                }
                $this->redirect('/');
            } else {
                Logger::logMessage('Bad answer. Task #7. User\'s answer: ' . trim($_POST['answer']));
                $teamTask->setAnswer($userAnswer, TeamTask::$TASK_STATUS['FAIL'][0]);
                $this->redirect('/');
            }
        }

        //У этой таски надо несколько ответов. За каждый правильный давать +балл.
        if ($teamTask->getTask()->getId() == 3 && $teamTask->getStatus() != TeamTask::$TASK_STATUS['EMPTY'][0]) {
            $data['taskText'] = 'Вы уже отправляли решение этого задания.';
        }
        if (!empty($_POST['answers']) && $teamTask->getTask()->getId() == 3) {
            if ($teamTask->getStatus() != TeamTask::$TASK_STATUS['EMPTY'][0]) {
                $this->redirect('/');
            }

            $lastAnswers = json_decode($teamTask->getLastAnswer(), true);

            if ($lastAnswers == null && !empty($teamTask->getLastAnswer())) {
                $this->redirect('/');
            }

            $dbAnswers = json_decode($teamTask->getTask()->getAnswer(), true);
            if ($dbAnswers == null) {
                $this->redirect('/');
            }

            $totalPoints = 0;
            $points = 0;
            $answers = $_POST['answers'];
            $r1 = !empty($_POST['radio1']) ? $_POST['radio1'] : '';
            $r2 = !empty($_POST['radio2']) ? $_POST['radio2'] : '';
            $r3 = !empty($_POST['radio3']) ? $_POST['radio3'] : '';
            $r4 = !empty($_POST['radio4']) ? $_POST['radio4'] : '';
            if ($answers != null) {

                if ($lastAnswers != null) {
                    for ($i = 0; $i < count($lastAnswers["a"]); ++$i) {
                        if (strcmp(mb_strtolower(trim($lastAnswers['a'][$i])), mb_strtolower(trim($dbAnswers['a'][$i]))) == 0) {
                            $answers[$i] = $lastAnswers['a'][$i];
                            --$points;
                        }
                    }
                    if (strcmp(mb_strtolower(trim($lastAnswers['1'])), mb_strtolower(trim($dbAnswers["1"]))) == 0) {
                        $r1 = $lastAnswers['1'];
                        --$points;
                    }
                    if (strcmp(mb_strtolower(trim($lastAnswers['2'])), mb_strtolower(trim($dbAnswers["2"]))) == 0) {
                        $r2 = $lastAnswers['2'];
                        --$points;
                    }
                    if (strcmp(mb_strtolower(trim($lastAnswers['3'])), mb_strtolower(trim($dbAnswers["3"]))) == 0) {
                        $r3 = $lastAnswers['3'];
                        --$points;
                    }
                    if (strcmp(mb_strtolower(trim($lastAnswers['4'])), mb_strtolower(trim($dbAnswers["4"]))) == 0) {
                        $r4 = $lastAnswers['4'];
                        --$points;
                    }
                }

                for ($i = 0; $i < count($answers); ++$i) {
                    if (strcmp(mb_strtolower(trim($answers[$i])), mb_strtolower(trim($dbAnswers['a'][$i]))) == 0) {
                        ++$points;
                        ++$totalPoints;
                    }
                }
                if (!empty($r1) && strcmp(mb_strtolower(trim($r1)), mb_strtolower(trim($dbAnswers['1']))) == 0) {
                    ++$points;
                    ++$totalPoints;
                }
                if (!empty($r2) && strcmp(mb_strtolower(trim($r2)), mb_strtolower(trim($dbAnswers['2']))) == 0) {
                    ++$points;
                    ++$totalPoints;
                }
                if (!empty($r3) && strcmp(mb_strtolower(trim($r3)), mb_strtolower(trim($dbAnswers['3']))) == 0) {
                    ++$points;
                    ++$totalPoints;
                }
                if (!empty($r4) && strcmp(mb_strtolower(trim($r4)), mb_strtolower(trim($dbAnswers['4']))) == 0) {
                    ++$points;
                    ++$totalPoints;
                }


                App::getInstance()->getDataBase()->beginTransaction();

                if (!$user->getTeam()->addPoints($points)) {
                    App::getInstance()->getDataBase()->rollBack();
                }
                $strAnswer = json_encode(array('a' => $answers, '1' => trim($r1), '2' => trim($r2), '3' => trim($r3), '4' => trim($r4)));

                $STH = App::getInstance()->getDataBase()->prepare("UPDATE `team_tasks` SET `last_answer`=?, `status`=? WHERE `team_id`=? AND `task_id`=?");
                $STH->bindValue(1, $strAnswer, PDO::PARAM_STR);
                $STH->bindValue(2, TeamTask::$TASK_STATUS['SUCCESS'][0]);
                $STH->bindValue(3, $user->getTeam()->getId());
                $STH->bindValue(4, $teamTask->getTask()->getId());
                if (!$STH->execute() || $STH->rowCount() == 0) {
                    Logger::logMessage('Blyyyyyyyattt2');
                    App::getInstance()->getDataBase()->rollBack();
                    return false;
                } else {
                    Logger::logMessage('Eblana suka. team: #' . $user->getTeam()->getId());
                    App::getInstance()->getDataBase()->commit();
                }

            }

            $this->redirect('/');
        }

        if (!empty($_POST['answer'])) {
            if ($teamTask->getStatus() != TeamTask::$TASK_STATUS['SUCCESS'] && $teamTask->tryAnswer($_POST['answer'])) {
                $user->getTeam()->addPoints($teamTask->getTask()->getPoints());
            }
            $this->redirect('/');
        }

        if (!$teamTask->getTask()->isVisible()) {
            Logger::logMessage('Find secret task #' . $teamTask->getTask()->getId());
        }

        $pageInfo = new PageInfo();
        $pageInfo->init('task');
        $data['pageInfo'] = $pageInfo->getInfo();

        $this->render('index', $data);
    }
}