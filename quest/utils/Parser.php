<?php

class Parser
{
    private $_users = array();

    public function parse($fit, $fija)
    {
        foreach ($fit as $url) {
            $url = trim($url);
            if (empty($url)) {
                continue;
            }

            if (!$this->parseFit($url)) {
                return false;
            }

            usleep(500);
        }

        foreach ($fija as $url) {
            $url = trim($url);
            if (empty($url)) {
                continue;
            }

            if (!$this->parseFija($url)) {
                return false;
            }

            usleep(500);
        }

        return true;
    }

    private function parseFit($url)
    {
        $data = file_get_contents(trim($url));
        if (!$data) {
            return false;
        }

        $res = preg_match_all('/>Группа ([^<]+)<.+<ol>(.+)<\/ol>/iusU', $data, $out, PREG_SET_ORDER);
        if (!$res) {
            return false;
        }

        foreach ($out as $val) {
            $res = preg_match_all('/<li>.+>([а-яА-ЯёЁ -]+)[^а-яА-ЯёЁ -]/iusU', $val[0], $out2, PREG_SET_ORDER);
            if (!$res) {
                return false;
            }

            foreach ($out2 as $userInfo) {

                $name = explode(' ', $userInfo[1]);
                $user = array(
                    'group' => $val[1],
                    'firstname' => $name[1],
                    'lastname' => $name[0],
                    'department' => 1,
                );

                $this->_users[] = $user;
            }
        }

        return true;
    }

    private function parseFija($url)
    {
        $data = file_get_contents(trim($url));
        if (!$data) {
            return false;
        }

        if (!preg_match('/mw-headline(.+)medialine/iusU', $data, $out)) {
            return false;
        }
        $out[1] .= '<h3';

        $res = preg_match_all('/>Группа ([0-9]+) .+<ol>(.+)<h3/iusU', $out[1], $out2, PREG_SET_ORDER);
        if (!$res) {
            return false;
        }

        foreach ($out2 as $val) {
            $res = preg_match_all('/<li>([^<]+)</iusU', $val[2], $out3, PREG_SET_ORDER);
            if (!$res) {
                return false;
            }

            foreach ($out3 as $userInfo) {

                $name = explode(' ', $userInfo[1]);
                $user = array(
                    'group' => $val[1],
                    'firstname' => $name[1],
                    'lastname' => $name[0],
                    'department' => 2,
                );

                $this->_users[] = $user;
            }
        }

        return true;
    }

    public function getUsers()
    {
        return $this->_users;
    }
}