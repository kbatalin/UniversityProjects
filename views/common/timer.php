<div class="timer">
    <h1 id="display" align="center" class="display"></h1>

    <script>
        var delay = 10;
        var display = document.getElementById("display");
        var firstCountDownDate = new Date("Dec 3, 2017 22:00:00").getTime();
        var secondCountDownDate = new Date("Dec 1, 2017 20:00:00").getTime();

//        var firstCountDownDate = new Date("Nov 23, 2017 22:00:00").getTime();
//        var secondCountDownDate = new Date("Nov 21, 2017 20:31").getTime();
        var switchingTime = new Date("Nov 20, 2017 20:36");
        var switched = false;

        var lastTickTime = new Date().getTime();
        var distance = firstCountDownDate - lastTickTime;

        var ratio = 1;
        var x = setTimeout(function tick(){
            var delta = new Date().getTime() - lastTickTime;
            lastTickTime = new Date().getTime();

//            var days = Math.floor(distance / (1000 * 60 * 60 * 24));
            var hours = Math.floor(distance / (1000 * 60 * 60));
//            var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((distance % (1000 * 60)) / 1000);
            var millis = Math.floor(distance % 1000);

//            display .innerHTML = days + "d " + hours + "h "
//                + minutes + "m " + seconds + "s ";
            display .innerHTML = isZero(hours) + ":"
                + isZero(minutes) + ":" + isZero(seconds) + "." + is2Zero(millis);

            if (!switched && distance < (firstCountDownDate - switchingTime)) {
                ratio = (firstCountDownDate - switchingTime) / (secondCountDownDate - switchingTime);
                switched = true;
                console.log('New delay: ' + delay);
            }

            distance -= delta * ratio;
            setTimeout(tick, delay);

        }, delay);

        function isZero(n) {
            if (n >= 0 && n <= 9) {
                return "0" + n;
            }
            return n;
        }

        function is2Zero(n) {
            if (n >= 10 && n <= 99) {
                return "0" + n;
            }
            if (n >= 0 && n <= 9) {
                return "00" + n;
            }
            return n;
        }


    </script>
</div>