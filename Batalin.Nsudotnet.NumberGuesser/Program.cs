using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NumberGuesser
{
    class Program
    {
        private delegate bool AnswerHandler();

        private const int MinNumber = 0;
        private const int MaxNumber = 100;
        
        private string _userName;
        private Game _currentGame;
        private AttemptsHistory _currentHistory;
        private readonly Dictionary<Game.AnswerResult, AnswerHandler> _handlers = new Dictionary<Game.AnswerResult, AnswerHandler>();

        public static void Main(string[] args)
        {
            new Program().Run();
        }

        public Program()
        {
            _handlers[Game.AnswerResult.Less] = () =>
            {
                Console.WriteLine("No! Try larger!");
                return false;
            };
            _handlers[Game.AnswerResult.Larger] = () =>
            {
                Console.WriteLine("No! Try less!");
                return false;
            };
            _handlers[Game.AnswerResult.Equal] = () =>
            {
                Console.WriteLine("Yes! You are right!");
                return true;
            };
        }

        public void Run()
        {
            Console.WriteLine("Hi! What's your name?");
            _userName = Console.ReadLine();

            while (PlayGame())
            {
                Console.WriteLine("Ok, next round...");
            }

            Console.WriteLine("Goodbuy, " + _userName + "!");
        }

        private void EndGame()
        {
            if (_currentGame == null || _currentHistory == null)
            {
                return;
            }

            Console.WriteLine("Yes!");
            Console.WriteLine("You did " + _currentGame.AttemptCount + " attempt" +
                              (_currentGame.AttemptCount > 1 ? "s" : ""));
            _currentHistory.PrintHistory(Console.Out, _currentHistory.GetAttempt(AttemptsHistory.LastAttemptIndex));

            double duration = _currentGame.GameDuration.TotalMinutes;
            string durationStr = String.Format("You spent {0:F2} m.", duration);
            Console.WriteLine(durationStr);
        }

        private bool PlayGame()
        {
            Console.WriteLine("I want to play a game with you");
            _currentGame = new Game(MinNumber, MaxNumber);
            _currentHistory = new AttemptsHistory();
            var jokeManager = new JokeManager(_userName);

            _currentGame.StartGame();
            bool isRightAnswer = false;
            do
            {
                Console.Write("Enter the number: ");
                string str = Console.ReadLine();
                if (str == null || "q".Equals(str))
                {
                    _currentGame.StopGame();
                    return false;
                }

                try
                {
                    int number = Int32.Parse(str);
                    _currentHistory.AddAttempt(number);

                    var result = _currentGame.TryAnswer(number);
                    isRightAnswer = _handlers[result]();

                    if (!isRightAnswer && _currentGame.AttemptCount % 4 == 0)
                    {
                        Console.WriteLine(jokeManager.GetJoke());
                    }
                }
                catch (FormatException)
                {
                    Console.WriteLine("Bad number format");
                }
                catch (OverflowException)
                {
                    Console.WriteLine("Number is too big");
                }
            }
            while (!isRightAnswer);
            _currentGame.StopGame();

            EndGame();
            return true;
        }
    }
}
