using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;

namespace Batalin.Nsudotnet.Enigma
{
    class Crypto
    {
        private readonly Dictionary<string, Func<SymmetricAlgorithm>> _cryptAlgorithms;

        static void Main(string[] args)
        {
            new Crypto().Run(args);
        }

        Crypto()
        {
            _cryptAlgorithms =
                new Dictionary<string, Func<SymmetricAlgorithm>>
                {
                    ["aes"] = () => new AesCryptoServiceProvider(),
                    ["des"] = () => new DESCryptoServiceProvider(),
                    ["rc2"] = () => new RC2CryptoServiceProvider(),
                    ["rijndeal"] = () => new RijndaelManaged()
                };

        }

        void Run(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("encrypt/decrypt");
                return;
            }

            string action = args[0].ToLower();
            if (action.Equals("encrypt"))
            {
                string input = args[1];
                string alg = args[2].ToLower();
                string output = args[3];
                Encrypt(input, alg, output);
            }
            else if (action.Equals("decrypt"))
            {
                string input = args[1];
                string alg = args[2].ToLower();
                string key = args[3];
                string output = args[4];
                Decrypt(input, alg, key, output);
            }
            else
            {
                Console.WriteLine("Bad action");
            }
        }

        //encrypt Crypto.cs rc2 output.bin
        void Encrypt(string input, string alg, string output)
        {
            if (!_cryptAlgorithms.ContainsKey(alg))
            {
                Console.WriteLine("Bad alg");
                return;
            }

            using (SymmetricAlgorithm algorithm = _cryptAlgorithms[alg]())
            {
                algorithm.GenerateKey();
                algorithm.GenerateIV();

                using (ICryptoTransform encryptor = algorithm.CreateEncryptor())
                using (FileStream fsInput = new FileStream(input, FileMode.Open, FileAccess.Read))
                using (FileStream fsCiphered = new FileStream(output, FileMode.Create, FileAccess.Write))
                using (CryptoStream cryptoStream = new CryptoStream(fsCiphered, encryptor, CryptoStreamMode.Write))
                {
                    fsInput.CopyTo(cryptoStream);
                }

                using (FileStream fsKey = new FileStream(input + ".key", FileMode.Create, FileAccess.Write))
                using (StreamWriter writer = new StreamWriter(fsKey))
                {
                    writer.Write(System.Convert.ToBase64String(algorithm.Key));
                    writer.Write("\n");
                    writer.Write(System.Convert.ToBase64String(algorithm.IV));
                }
            }
        }

        //decrypt output.bin rc2 Crypto.cs.key CryptoRestored.cs
        void Decrypt(string input, string alg, string keyFile, string output)
        {
            if (!_cryptAlgorithms.ContainsKey(alg))
            {
                Console.WriteLine("Bad alg");
                return;
            }

            using (SymmetricAlgorithm algorithm = _cryptAlgorithms[alg]())
            {
                using (FileStream fsKey = new FileStream(keyFile, FileMode.Open, FileAccess.Read))
                using (StreamReader reader = new StreamReader(fsKey))
                {
                    string line = reader.ReadLine();
                    byte[] key = Convert.FromBase64String(line);

                    line = reader.ReadLine();
                    byte[] IV = Convert.FromBase64String(line);

                    algorithm.Key = key;
                    algorithm.IV = IV;
                }

                using(ICryptoTransform decryptor = algorithm.CreateDecryptor())
                using (FileStream fsInput = new FileStream(input, FileMode.Open, FileAccess.Read))
                using (FileStream fsCiphered = new FileStream(output, FileMode.Create, FileAccess.Write))
                using (CryptoStream cryptoStream = new CryptoStream(fsCiphered, decryptor, CryptoStreamMode.Write))
                {
                    fsInput.CopyTo(cryptoStream);
                }
            }
        }
    }
}
