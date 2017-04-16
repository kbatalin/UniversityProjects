using System;
using System.Collections.Generic;

namespace Calendar
{
    internal class Program
    {
        public static void Main(string[] args)
        {
            new Program().Run();
        }

        public void Run()
        {
            Console.WriteLine("Enter date: ");
            var dateStr = Console.ReadLine();
            DateTime dateTime;
            if (!DateTime.TryParse(dateStr, out dateTime))
            {
                Console.WriteLine("Bad date");
                return;
            }

            WriteMonth(dateTime);

            Console.ReadKey();
        }

        private void WriteMonth(DateTime selectedDate)
        {
            Console.BackgroundColor = ConsoleColor.White;

            foreach (var dayWeek in new[] {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"})
            {
                Console.ForegroundColor = dayWeek == "Sun" || dayWeek == "Sat" ? ConsoleColor.Red : ConsoleColor.Black;
                Console.Write("{0, 3} ", dayWeek);
            }
            Console.WriteLine();

            var startDate = new DateTime(selectedDate.Year, selectedDate.Month, 1);
            for (DayOfWeek i = DayOfWeek.Sunday; i != startDate.DayOfWeek; ++i)
            {
                Console.Write("{0, 3} ", "");
            }
            for (; startDate.Month == selectedDate.Month; startDate = startDate.AddDays(1))
            {
                Console.ForegroundColor =
                    startDate.DayOfWeek == DayOfWeek.Sunday || startDate.DayOfWeek == DayOfWeek.Saturday
                        ? ConsoleColor.Red
                        : ConsoleColor.Black;
                Console.BackgroundColor = startDate.Day == selectedDate.Day
                    ? ConsoleColor.Blue
                    : (startDate.Day == DateTime.Now.Day
                        ? ConsoleColor.Gray
                        : ConsoleColor.White);

                Console.Write("{0, 3} ", startDate.Day);
                if (startDate.DayOfWeek == DayOfWeek.Saturday)
                {
                    Console.WriteLine();
                }
            }

        }
    }
}