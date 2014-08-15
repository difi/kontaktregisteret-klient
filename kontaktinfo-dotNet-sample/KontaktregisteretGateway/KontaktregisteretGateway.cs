using System;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Description;
using KontaktregisteretGateway.Difi;
using KontaktregisteretGateway.WcfCustom;

namespace KontaktregisteretGateway
{
    public class KontaktregisteretGateway
    {
        private const string EndpointConfigurationName = "DifiOppslagstjeneste";
        private readonly DifiGatewaySettings _difiGatewaySettings;

        public KontaktregisteretGateway(DifiGatewaySettings difiGatewaySettings)
        {
            _difiGatewaySettings = difiGatewaySettings;
        }

        public Person[] HentPersoner(HentPersonerForespoersel request)
        {
            Person[] response;
            using (var client = GetOppslagstjeneste1405Client())
            {
                response = client.HentPersoner(request);
                client.Close();
            }
            return response;
        }

        public HentPrintSertifikatRespons HentPrintSeritfikat(HentPrintSertifikatForespoersel request)
        {
            HentPrintSertifikatRespons response;
            using (var client = GetOppslagstjeneste1405Client())
            {
                response = client.HentPrintSertifikat(request);
                client.Close();
            }
            return response;
        }
        
        public HentEndringerRespons HentEndringer(HentEndringerForespoersel request)
        {
            HentEndringerRespons response;
            using (var client = GetOppslagstjeneste1405Client())
            {
                response = client.HentEndringer(request);
                client.Close();
            }
            return response;
        }

        private oppslagstjeneste1405Client GetOppslagstjeneste1405Client()
        {
            var client = new oppslagstjeneste1405Client(EndpointConfigurationName);
            client.Endpoint.Binding = CreateCustomBinding();
            SetCredentials(client.ChannelFactory);
            return client;
        }

        private CustomBinding CreateCustomBinding()
        {
            var binding = new CustomBinding();
            var securityBinding =
                SecurityBindingElement.CreateCertificateOverTransportBindingElement(
                    MessageSecurityVersion
                        .WSSecurity10WSTrustFebruary2005WSSecureConversationFebruary2005WSSecurityPolicy11BasicSecurityProfile10);
            securityBinding.IncludeTimestamp = true;
            securityBinding.AllowInsecureTransport = true;
            securityBinding.EnableUnsecuredResponse = true;
            binding.Elements.Add(securityBinding);
            binding.Elements.Add(new CustomTextMessageBindingElement(_difiGatewaySettings));
            var httpsTransportBinding = new HttpsTransportBindingElement();
            httpsTransportBinding.MaxReceivedMessageSize = Int32.MaxValue;
            binding.Elements.Add(httpsTransportBinding);
            return binding;
        }

        private void SetCredentials(ChannelFactory factory)
        {
            var credentials = new MyClientCredentials();
            credentials.ClientEncryptingCertificate = _difiGatewaySettings.ServiceCertificate;
            credentials.ClientSigningCertificate = _difiGatewaySettings.ClientCertificate;
            credentials.ServiceEncryptingCertificate = _difiGatewaySettings.ServiceCertificate;
            credentials.ServiceSigningCertificate = _difiGatewaySettings.ClientCertificate;
            factory.Endpoint.Behaviors.Remove(typeof(ClientCredentials));
            factory.Endpoint.Behaviors.Add(credentials);
        }
    }
}
