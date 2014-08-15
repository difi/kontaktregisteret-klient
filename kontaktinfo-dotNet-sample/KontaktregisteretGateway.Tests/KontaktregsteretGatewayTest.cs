using KontaktregisteretGateway.Difi;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace KontaktregisteretGateway.Tests
{
    [TestClass]
    public class KontaktregsteretGatewayTest
    {
        private KontaktregisteretGateway _kontaktregisteretGateway;

        public DifiGatewaySettings CreateGatewaySettings()
        {
            var service = new System.Security.Cryptography.X509Certificates.X509Certificate2("Certificates\\idporten-ver2.difi.no-v2.crt", "changeit");
            var client = new System.Security.Cryptography.X509Certificates.X509Certificate2("Certificates\\WcfClient.pfx", "changeit");
            var settings = new DifiGatewaySettings(client, service);
            return settings;
        }

        [TestInitialize]
        public void InitializeTest()
        {
            _kontaktregisteretGateway = new KontaktregisteretGateway(CreateGatewaySettings());
        }

        [TestMethod]
        public void HentPersoner()
        {
            var request = new HentPersonerForespoersel();
            request.informasjonsbehov = new informasjonsbehov[1];
            request.informasjonsbehov[0] = informasjonsbehov.Kontaktinfo;
            request.personidentifikator = new string[2];
            request.personidentifikator[0] = "02018090573";
            request.personidentifikator[1] = "02018090301";
            
            var personer = _kontaktregisteretGateway.HentPersoner(request);

            Assert.IsNotNull(personer);
            Assert.AreEqual(2, personer.Length);
            Assert.AreEqual(request.personidentifikator[0], personer[0].personidentifikator);
            Assert.IsNotNull(personer[0].Kontaktinformasjon.Epostadresse.Value);
            Assert.AreEqual(request.personidentifikator[1], personer[1].personidentifikator);
            Assert.IsNotNull(personer[1].Kontaktinformasjon.Epostadresse.Value);
        }

        [TestMethod]
        public void HentKontaktSertifikat()
        {
            var request = new HentPrintSertifikatForespoersel();
            
            var printSertifikatRespons = _kontaktregisteretGateway.HentPrintSeritfikat(request);
            
            Assert.IsTrue(printSertifikatRespons.postkasseleverandoerAdresse.Length > 0);
            Assert.IsTrue(printSertifikatRespons.X509Sertifikat.Length > 0);
        }

        [TestMethod]
        public void HentEndringer()
        {
            var request = new HentEndringerForespoersel();
            request.informasjonsbehov = new informasjonsbehov[1];
            request.informasjonsbehov[0] = informasjonsbehov.Kontaktinfo;
            request.fraEndringsNummer = 600;

            var hentEndringerRespons = _kontaktregisteretGateway.HentEndringer(request);

            Assert.IsNotNull(hentEndringerRespons);
        }
    }
}
