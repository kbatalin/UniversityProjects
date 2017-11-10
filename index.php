<?php
include_once 'config.php';

$ips = array(
    '84.237.53.114',
    '127.0.0.1'
);

if (!in_array($_SERVER['REMOTE_ADDR'], $ips)) {
    exit('Maintenance... Try later');
}

spl_autoload_register(function ($className) {
    $pos = mb_strrpos($className, '/');
    if ($pos === false) {
        $pos = 0;
    } else {
        ++$pos;
    }
    $fileName = mb_substr($className, $pos);

    $cats = array(
        'controllers',
        'utils',
        'models',
        'views',
    );
    foreach ($cats as $cat) {
        $path = $cat . '/' . $fileName . '.php';
        if (is_file($path)) {
            include_once $path;
            return;
        }
    }

    throw new Exception('Bad class name');
});

function getName($name)
{
    $arr = explode('-', $name);
    $result = '';
    foreach ($arr as $v) {
        $v = mb_strtolower($v);
        $result .= ucfirst($v);
    }

    return $result;
}

$c = !empty($_GET['c']) ? trim($_GET['c']) : 'index';
$a = !empty($_GET['a']) ? trim($_GET['a']) : 'index';

$c = getName($c) . 'Controller';
$a = 'action' . getName($a);

try {
    $controller = new $c();
    if (!method_exists($controller, $a)) {
        throw new Exception('404');
    }
} catch (Exception $e) {
    header('Location: /error/404/');
    exit;
}

if (!empty($controller)) {
    $controller->$a();
}

