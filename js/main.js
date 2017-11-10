var imgDir = '/img/bg/';
var imgCount = 2;
var nextBgImg = Math.floor(Math.random() * imgCount);
var currentBg = 0;
var changeSpeed = 'slow';

function changeBg() {
    var otherBg = (currentBg + 1) % 2;

    $('#bg' + currentBg).animate({'opacity': '0'}, changeSpeed, function () {
        $('#bg' + currentBg).css({'z-index': '1'});
        $('#bg' + otherBg).css({'z-index': '2'});
        $('#bg' + currentBg).css({'opacity': '1'});

        nextBgImg = (nextBgImg + 1) % imgCount;
        $('#bg' + currentBg).css({'background-image': 'url(' + imgDir + nextBgImg + '.jpg' + ')'});
        currentBg = otherBg;
    });
}

setInterval(changeBg, 8000);

window.onload = function () {
    $('#bg1').css({'background-image': 'url(' + imgDir + nextBgImg + '.jpg' + ')'});
    nextBgImg = (nextBgImg + 1) % imgCount;
};

