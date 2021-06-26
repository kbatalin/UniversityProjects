using System;
using System.IO;

namespace NumberGuesser
{
    public class AttemptsHistory
    {
        public static readonly int LastAttemptIndex = -1;

        private readonly int[] _history = new int[1000];
        private int _attemptsCount;

        public void AddAttempt(int number)
        {
            _history[_attemptsCount++] = number;
        }

        public int GetAttempt(int index)
        {
            if (_attemptsCount <= 0)
            {
                return -1;
            }

            if (index == LastAttemptIndex)
            {
                return _history[_attemptsCount - 1];
            }

            return _history[index];
        }

        public void PrintHistory(TextWriter output, int rigthAnswer)
        {
            output.WriteLine("History: ");

            for (var i = 0; i < _attemptsCount; i++)
            {
                string str = String.Format("#{0,-4} {1, -4} {2}", i + 1, _history[i], GetCmpSign(_history[i], rigthAnswer));
                output.WriteLine(str);
            }
        }

        private static string GetCmpSign(int a, int b)
        {
            if (a == b)
            {
                return "=";
            }

            return a < b ? "<" : ">";
        }
    }
}