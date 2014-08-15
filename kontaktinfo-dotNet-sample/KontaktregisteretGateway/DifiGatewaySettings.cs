using System.Security.Cryptography.X509Certificates;

namespace KontaktregisteretGateway
{
    public class DifiGatewaySettings
    {
        public X509Certificate2 ClientCertificate { get; private set; }
        public X509Certificate2 ServiceCertificate { get; private set; }

        public DifiGatewaySettings(X509Certificate2 clientCertificate, X509Certificate2 serviceCertificate)
        {
            ClientCertificate = clientCertificate;
            ServiceCertificate = serviceCertificate;
        }
    }
}
