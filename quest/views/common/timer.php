<div class="timer">
    <h1 id="display" align="center" class="display"></h1>

    <script>
        var delay = 30;
        var display = document.getElementById("display");
        var firstCountDownDate = new Date("Dec 3, 2017 22:00:00").getTime();
        var secondCountDownDate = new Date("Dec 1, 2017 20:00:00").getTime();
//        var secondCountDownDate = new Date("Nov 30, 2017 03:02:00").getTime();

//        var switchingTime = new Date("Nov 30, 2017 03:01:30");
        var switchingTime = new Date("Nov 27, 2017 00:00:01");
        var switched = false;

        var lastTickTime = new Date().getTime();
        var distance = firstCountDownDate - lastTickTime;

        var ratio = 1;
        var x = setTimeout(function tick(){
            var delta = new Date().getTime() - lastTickTime;
            lastTickTime = new Date().getTime();

            var hours = Math.floor(distance / (1000 * 60 * 60));
            var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
            var seconds = Math.floor((distance % (1000 * 60)) / 1000);
            var millis = Math.floor(distance % 1000);

            if (new Date().getTime() > secondCountDownDate) {
                display.innerHTML = "00:00:00.000";
                return;
            }

            display.innerHTML = isZero(hours) + ":"
                + isZero(minutes) + ":" + isZero(seconds) + "." + is2Zero(millis);

            if (!switched && distance < (firstCountDownDate - switchingTime)) {
                ratio = (firstCountDownDate - switchingTime) / (secondCountDownDate - switchingTime);
                distance = (firstCountDownDate - switchingTime) - (lastTickTime - switchingTime) * ratio;
                switched = true;
            }

            distance -= delta * ratio;
            if (distance > 0) {
                setTimeout(tick, delay);
            } else {
                display.innerHTML = "00:00:00.000"
            }
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