using System;

namespace NumberGuesser
{
    public class JokeManager
    {
        private readonly string[] _jokes;
        private readonly Random _random = new Random();

        public JokeManager(string name)
        {
            _jokes = new[]
            {
                $"Stone smarter than you, {name}",
                $"{name}, your intuition is bad",
                $"-_____-, who manage better than {name}? Each!",
                $"{name}, say you're kidding, please",
            };
        }

        public string GetJoke()
        {
            int jokeIndex = _random.Next(_jokes.Length);
            return _jokes[jokeIndex];
        }
    }
}