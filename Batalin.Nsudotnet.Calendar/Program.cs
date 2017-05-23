using System;
using System.Collections.Generic;
using System.Globalization;

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

            DayOfWeek week = DayOfWeek.Sunday;
            CultureInfo ci = new CultureInfo("en-US");
            string[] names = ci.DateTimeFormat.AbbreviatedDayNames;
            do
            {
                Console.ForegroundColor = week == DayOfWeek.Sunday || week == DayOfWeek.Saturday ? ConsoleColor.Red : ConsoleColor.Black;
                Console.Write("{0, 3} ", names[(int)week]);
                week = (DayOfWeek) (((int)week + 1) % 7);
            } while (week != DayOfWeek.Sunday);
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