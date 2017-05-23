using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NumberGuesser
{
    class Game
    {
        public int AttemptCount { get; private set; }

        private readonly int _minNumber;
        private readonly int _maxNumber;

        private readonly Random _random = new Random();
        private int _generatedNumber;
        private DateTime _startTime;
        private DateTime _endTime;

        public TimeSpan GameDuration
        {
            get
            {
                return _endTime.Subtract(_startTime);
            }
        }

        public Game(int minNumber, int maxNumber)
        {
            _minNumber = minNumber;
            _maxNumber = maxNumber;
        }

        public void StartGame()
        {
            _generatedNumber = _random.Next(_minNumber, _maxNumber + 1);
            AttemptCount = 0;
            _startTime = DateTime.Now;
        }

        public void StopGame()
        {
            _endTime = DateTime.Now;
        }

        public AnswerResult TryAnswer(int answer)
        {
            ++AttemptCount;

            if (_generatedNumber < answer)
            {
                return AnswerResult.Larger;
            }
            else if (_generatedNumber > answer)
            {
                return AnswerResult.Less;
            }
            else
            {
                return AnswerResult.Equal;
            }
        }

        public enum AnswerResult
        {
            Less = 0,
            Larger,
            Equal
        }
    }
}
