using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Batalin.Nsudotnet.LinesCounter
{
    class LinesCounter
    {
        static void Main(string[] args)
        {
            new LinesCounter().Run(args);
        }

        void Run(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("Need file mask");
                return;
            }

            string fileMask = args[0];
            string[] files = Directory.GetFiles(Directory.GetCurrentDirectory(), fileMask, SearchOption.AllDirectories);

            int count = 0;
            foreach (var file in files)
            {
                count += CalcCount(file);
            }

            Console.WriteLine("Total count: {0}", count);

            Console.ReadKey();
        }

        int CalcCount(string file)
        {
            int count = 0;
            using (FileStream fsInput = new FileStream(file, FileMode.Open, FileAccess.Read))
            using (StreamReader reader = new StreamReader(fsInput))
            {
                while (!reader.EndOfStream)
                {
                    string line = reader.ReadLine().Trim();
                    if (IsGoodLine(line))
                    {
                        ++count;
                    }
                }
            }

            return count;
        }
        //ыаыв
        private bool isCommentBlock = false;
        bool IsGoodLine(string line)
        {
            int symbolsCount = 0;
            for (int i = 0; i < line.Length; ++i)
            {
                if (line[i] == '/' && i + 1 < line.Length)
                {
                    ++i;
                    if (line[i] == '/')
                    {
                        return symbolsCount > 0;
                    }
                    else if (line[i] == '*')
                    {
                        isCommentBlock = true;
                    }
                    else
                    {
                        ++symbolsCount;
                    }

                    continue;
                }
                if (isCommentBlock && line[i] == '*' && i + 1 < line.Length)
                {
                    ++i;
                    if (line[i] == '/')
                    {
                        isCommentBlock = false;
                    }
                    else
                    {
                        ++symbolsCount;
                    }
                    continue;
                }

                ++symbolsCount;
            }

            return symbolsCount > 0;
        }
    }
}
