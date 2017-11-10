<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('admin/navigationBar', array('currentPage' => '/admin/index/')); ?>
        <div class="substrate">
            <p>
                Последние логи:
            </p>
            <p>
                <textarea style="width: 100%; height: 400px;"><?php
                    if (!empty($logs)) {
                        foreach ($logs as $log) {
                            echo '#' . $log['id'] . "\t" . $log['log_date'] . "\turl:" . $log['url'] . "\tuser: #"
                                . $log['user_id'] . ' ip: ' . $log['user_ip'] . ".\tLog: " . $log['log'] . "\n";
                        }
                    }
                    ?></textarea>
            </p>
        </div>
    </div>
</div>
</body>
</html>`