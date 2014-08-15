using System.IdentityModel.Selectors;
using System.IdentityModel.Tokens;
using System.ServiceModel;
using System.ServiceModel.Description;
using System.ServiceModel.Security.Tokens;

namespace KontaktregisteretGateway.WcfCustom
{
    public class MyClientCredentialsSecurityTokenManager : ClientCredentialsSecurityTokenManager
    {
        private readonly MyClientCredentials _credentials;

        public MyClientCredentialsSecurityTokenManager(
            MyClientCredentials credentials)
            : base(credentials)
        {
            _credentials = credentials;
        }

        public override SecurityTokenProvider CreateSecurityTokenProvider(
            SecurityTokenRequirement requirement)
        {
            SecurityTokenProvider result = null;
            if (requirement.TokenType == SecurityTokenTypes.X509Certificate)
            {
                var direction = requirement.GetProperty<MessageDirection>(ServiceModelSecurityTokenRequirement.MessageDirectionProperty);
                
                if (direction == MessageDirection.Output)
                {
                    if (requirement.KeyUsage == SecurityKeyUsage.Signature)
                        result = new X509SecurityTokenProvider(this._credentials.ClientSigningCertificate);
                    else
                        result = new X509SecurityTokenProvider(this._credentials.ServiceEncryptingCertificate);
                }
                else
                {
                    if (requirement.KeyUsage == SecurityKeyUsage.Signature)
                        result = new X509SecurityTokenProvider(this._credentials.ServiceSigningCertificate);
                    else
                        result = new X509SecurityTokenProvider(_credentials.ClientEncryptingCertificate);
                }
            }
            else
            {
                result = base.CreateSecurityTokenProvider(requirement);
            }

            return result;
        }
    }

}
