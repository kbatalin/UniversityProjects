<!DOCTYPE html>
<html lang="en">
<head>
    <?php View::render('common/head', $pageInfo); ?>
    <link rel="stylesheet" href="/css/bootstrap-select.min.css">
    <script src="/js/bootstrap-select.min.js"></script>
</head>
<body>
<?php View::render('common/background'); ?>
<div id="content">
    <div class="box">
        <?php View::render('common/navigationBar', array('currentPage' => 'team')); ?>
        <div class="substrate">
            <form action="/team/create/" method="post">
                <?php
                if (!empty($error)) {
                    echo '<p>' . htmlspecialchars($error) . '</p>';
                }
                ?>
                <p>
                    <input type="text" name="name" placeholder="Название команды"
                           value="<?php if (!empty($name)) echo htmlspecialchars($name) ?>">
                </p>
                <p>
                    Напарник:
                    <select name="partner" class="selectpicker" data-live-search="true" data-width="320px">
                        <?php
                        if (!empty($availablePartners)) {
                            $partner = !empty($partner) ? $partner : 0;
                            foreach ($availablePartners as $partner) {
                                echo '<option value="' . htmlspecialchars($partner['id']) . '" '
                                    . ($partner['id'] == $partner ? ' selected' : '') . '>'
                                    . htmlspecialchars($partner['lastname'])
                                    . ' ' . htmlspecialchars($partner['firstname']) . '</option>';
                            }
                        }
                        ?>
                    </select>
                </p>
                <p>
                    <input type="submit" value="Создать">
                </p>
            </form>
        </div>
        <?php View::render('common/footer', null); ?>
    </div>
</div>

</body>
<?php View::render('common/metrics'); ?>
</html>