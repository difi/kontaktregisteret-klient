using System;
using System.IO;
using System.Security.Cryptography;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel.Channels;
using System.Text;
using System.Xml;

namespace KontaktregisteretGateway.WcfCustom
{
    public class CustomTextMessageEncoder : MessageEncoder
    {
        private readonly CustomTextMessageEncoderFactory _factory;
        private readonly DifiGatewaySettings _difiGatewaySettings;
        private readonly XmlWriterSettings _writerSettings;

        public CustomTextMessageEncoder(CustomTextMessageEncoderFactory factory,
            DifiGatewaySettings difiGatewaySettings)
        {
            _factory = factory;
            _difiGatewaySettings = difiGatewaySettings;
            _writerSettings = new XmlWriterSettings();
        }

        public override string ContentType
        {
            get
            {
                return "text/xml";
            }
        }

        public override string MediaType
        {
            get
            {
                return "text/xml";
            }
        }

        public override MessageVersion MessageVersion
        {
            get
            {
                return this._factory.MessageVersion;
            }
        }

        public override Message ReadMessage(ArraySegment<byte> buffer, BufferManager bufferManager, string contentType)
        {
            var msgContents = new byte[buffer.Count];

            try
            {
                Array.Copy(buffer.Array, buffer.Offset, msgContents, 0, msgContents.Length);
            }
            catch (Exception ex)
            {
                if (ex.InnerException != null)
                    Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message + " InnerException: " + ex.InnerException.Message);
                else
                    Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message);
            }

            bufferManager.ReturnBuffer(buffer.Array);

            var stream = new MemoryStream(msgContents);
            return ReadMessage(stream, int.MaxValue);
        }

        public override Message ReadMessage(Stream stream, int maxSizeOfHeaders, string contentType)
        {
            var sr = new StreamReader(stream);
            var wireResponse = sr.ReadToEnd();

            var logicalResponse = GetDecryptedResponse(wireResponse);
            logicalResponse = String.Format(
                    @"<s:Envelope xmlns:s=""http://schemas.xmlsoap.org/soap/envelope/"">
	                        <s:Body>
		                        {0}
	                        </s:Body>
                        </s:Envelope>",
                    logicalResponse);

            XmlReader reader = XmlReader.Create(new StringReader(logicalResponse));
            return Message.CreateMessage(reader, maxSizeOfHeaders, MessageVersion.Soap11);
        }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before losing scope"), System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Maintainability", "CA1500:VariableNamesShouldNotMatchFieldNames", MessageId = "key")]
        private string GetDecryptedResponse(string encryptedResponse)
        {
            var doc = new XmlDocument();
            doc.LoadXml(encryptedResponse);
            var cipherNode = doc.SelectSingleNode("//*[local-name(.)='Body']//*[local-name(.)='CipherValue']");
            var wrappingKeyNode = doc.SelectSingleNode("//*[local-name(.)='Header']//*[local-name(.)='CipherValue']");
            var cypher = Convert.FromBase64String(cipherNode.InnerText);
            var wrappingKey = Convert.FromBase64String(wrappingKeyNode.InnerText);
            var key = GetEncryptingKey(wrappingKey);
            var iv = GetIV(cypher);

            var aesManagedAlg = new AesCryptoServiceProvider();
            aesManagedAlg.BlockSize = 128;
            aesManagedAlg.Key = key;
            aesManagedAlg.IV = iv;

            var body = ExtractIvAndDecrypt(aesManagedAlg, cypher, 0, cypher.Length);
            return Encoding.UTF8.GetString(body);
        }

        internal static byte[] ExtractIvAndDecrypt(SymmetricAlgorithm algorithm, byte[] cipherText, int offset, int count)
        {
            byte[] buffer2;
            if (cipherText == null)
                throw new ArgumentNullException();
            if ((count < 0) || (count > cipherText.Length))
                throw new ArgumentOutOfRangeException();
            if ((offset < 0) || (offset > (cipherText.Length - count)))
                throw new ArgumentOutOfRangeException();
            var num = algorithm.BlockSize / 8;
            var dst = new byte[num];
            Buffer.BlockCopy(cipherText, offset, dst, 0, dst.Length);
            algorithm.Padding = PaddingMode.ISO10126;
            algorithm.Mode = CipherMode.CBC;
            
            using (var transform = algorithm.CreateDecryptor(algorithm.Key, dst))
            {
                buffer2 = transform.TransformFinalBlock(cipherText, offset + dst.Length, count - dst.Length);
            }
            
            return buffer2;
        }

        private byte[] GetIV(byte[] cypher)
        {
            var iv = new byte[16];
            Array.Copy(cypher, iv, 16);
            return iv;
        }

        private byte[] GetEncryptingKey(byte[] wrappingKey)
        {
            var rsa = (RSACryptoServiceProvider)_difiGatewaySettings.ClientCertificate.PrivateKey;
            var enckey = rsa.Decrypt(wrappingKey, false);
            return enckey;
        }

        public string GetDecryptedText(string encryptedStringToDecrypt)
        {
            var certStore = new X509Store(StoreName.My, StoreLocation.CurrentUser);
            var certificate = new X509Certificate2();
            certStore.Open(OpenFlags.ReadWrite);
            foreach (var cert in certStore.Certificates)
            {
                try
                {
                    if (!cert.Thumbprint.ToLower().Equals(_difiGatewaySettings.ClientCertificate))
                        continue;
                    certificate = cert;
                    break;
                }
                catch (Exception ex)
                {
                    if (ex.InnerException != null)
                    {
                        Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message + " InnerException: " + ex.InnerException.Message);
                    }
                    else
                    {
                        Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message);
                    }
                }
            }

            try
            {
                var cipherbytes = Convert.FromBase64String(encryptedStringToDecrypt);

                if (certificate.HasPrivateKey)
                {
                    var rsa = (RSACryptoServiceProvider)certificate.PrivateKey;
                    var plainbytes = rsa.Decrypt(cipherbytes, false);
                    var encoding = new ASCIIEncoding();

                    return encoding.GetString(plainbytes);
                }

                throw new Exception("Certificate used for decryption has no private key.");
            }
            catch (Exception e)
            {
                throw e;
            }
        }

        public override ArraySegment<byte> WriteMessage(Message message, int maxMessageSize, BufferManager bufferManager, int messageOffset)
        {
            var stream = new MemoryStream();
            var writer = XmlWriter.Create(stream, this._writerSettings);
            message.WriteMessage(writer);
            writer.Close();

            var messageBytes = stream.GetBuffer();
            var messageLength = (int)stream.Position;
            stream.Close();

            var totalLength = messageLength + messageOffset;
            var totalBytes = bufferManager.TakeBuffer(totalLength);
            try
            {
                Array.Copy(messageBytes, 0, totalBytes, messageOffset, messageLength);
            }
            catch (Exception ex)
            {
                if (ex.InnerException != null)
                {
                    Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message + " InnerException: " + ex.InnerException.Message);
                }
                else
                {
                    Console.WriteLine("There has been an critical error in the decryption process: " + ex.Message);
                }
            }

            var byteArray = new ArraySegment<byte>(totalBytes, messageOffset, messageLength);
            return byteArray;
        }

        public override void WriteMessage(Message message, Stream stream)
        {
            var writer = XmlWriter.Create(stream, _writerSettings);
            message.WriteMessage(writer);
            writer.Close();
        }
    }
}
