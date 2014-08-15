using System.IdentityModel.Selectors;
using System.Security.Cryptography.X509Certificates;
using System.ServiceModel.Description;

namespace KontaktregisteretGateway.WcfCustom
{
    public class MyClientCredentials : ClientCredentials
    {
        private X509Certificate2 _clientSigningCert;
        private X509Certificate2 _clientEncryptingCert;
        private X509Certificate2 _serviceSigningCert;
        private X509Certificate2 _serviceEncryptingCert;

        public MyClientCredentials()
        {
        }

        protected MyClientCredentials(MyClientCredentials other)
            : base(other)
        {
            _clientEncryptingCert = other._clientEncryptingCert;
            _clientSigningCert = other._clientSigningCert;
            _serviceEncryptingCert = other._serviceEncryptingCert;
            _serviceSigningCert = other._serviceSigningCert;
        }

        public X509Certificate2 ClientSigningCertificate
        {
            get
            {
                return _clientSigningCert;
            }
            set
            {
                _clientSigningCert = value;
            }
        }

        public X509Certificate2 ClientEncryptingCertificate
        {
            get
            {
                return _clientEncryptingCert;
            }
            set
            {
                _clientEncryptingCert = value;
            }
        }

        public X509Certificate2 ServiceSigningCertificate
        {
            get
            {
                return _serviceSigningCert;
            }
            set
            {
                _serviceSigningCert = value;
            }
        }

        public X509Certificate2 ServiceEncryptingCertificate
        {
            get
            {
                return _serviceEncryptingCert;
            }
            set
            {
                _serviceEncryptingCert = value;
            }
        }
        
        public override SecurityTokenManager CreateSecurityTokenManager()
        {
            return new MyClientCredentialsSecurityTokenManager(this);
        }

        protected override ClientCredentials CloneCore()
        {
            return new MyClientCredentials(this);
        }
    }
}
