using System;

namespace NumberGuesser
{
    public class JokeManager
    {
        private const int JokesCount = 4;

        private readonly string[] _jokes = new string[JokesCount];
        private readonly Random _random = new Random();

        public JokeManager(string name)
        {
            _jokes[0] = String.Format("Stone smarter than you, {0}", name);
            _jokes[1] = String.Format("{0}, your intuition is bad", name);
            _jokes[2] = String.Format("-_____-, who manage better than {0}? Each!", name);
            _jokes[3] = String.Format("{0}, say you're kidding, please", name);
        }

        public string GetJoke()
        {
            int jokeIndex = _random.Next(_jokes.Length);
            return _jokes[jokeIndex];
        }
    }
}