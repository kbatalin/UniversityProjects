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
        private Dictionary<string, SymmetricAlgorithm> cryptAlgorithms;

        static void Main(string[] args)
        {
            new Crypto().Run(args);
        }

        Crypto()
        {
            cryptAlgorithms = new Dictionary<string, SymmetricAlgorithm>();

            cryptAlgorithms["aes"] = new AesCryptoServiceProvider();
            cryptAlgorithms["des"] = new DESCryptoServiceProvider();
            cryptAlgorithms["rc2"] = new RC2CryptoServiceProvider();
            cryptAlgorithms["rijndeal"] = new RijndaelManaged();
        }

        void Run(string[] args)
        {
            if (args.Length < 2)
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
                encrypt(input, alg, output);
            }
            else if (action.Equals("decrypt"))
            {
                string input = args[1];
                string alg = args[2].ToLower();
                string key = args[3];
                string output = args[4];
                decrypt(input, alg, key, output);
            }
            else
            {
                Console.WriteLine("Bad action");
            }
        }

        //encrypt Crypto.cs rc2 output.bin
        void encrypt(string input, string alg, string output)
        {
            if (!cryptAlgorithms.ContainsKey(alg))
            {
                Console.WriteLine("Bad alg");
                return;
            }

            SymmetricAlgorithm algorithm = cryptAlgorithms[alg];
            algorithm.GenerateKey();
            algorithm.GenerateIV();
            ICryptoTransform encryptor = algorithm.CreateEncryptor();

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

        //decrypt output.bin rc2 Crypto.cs.key CryptoRestored.cs
        void decrypt(string input, string alg, string keyFile, string output)
        {
            if (!cryptAlgorithms.ContainsKey(alg))
            {
                Console.WriteLine("Bad alg");
                return;
            }

            SymmetricAlgorithm algorithm = cryptAlgorithms[alg];

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

            ICryptoTransform decryptor = algorithm.CreateDecryptor();

            using (FileStream fsInput = new FileStream(input, FileMode.Open, FileAccess.Read))
            using (FileStream fsCiphered = new FileStream(output, FileMode.Create, FileAccess.Write))
            using (CryptoStream cryptoStream = new CryptoStream(fsCiphered, decryptor, CryptoStreamMode.Write))
            {
                fsInput.CopyTo(cryptoStream);
            }
        }
    }
}
