using System.ServiceModel.Channels;

namespace KontaktregisteretGateway.WcfCustom
{
    public class CustomTextMessageEncoderFactory : MessageEncoderFactory
    {
        private readonly MessageEncoder _encoder;

        internal CustomTextMessageEncoderFactory(DifiGatewaySettings difiGatewaySettings)
        {
            _encoder = new CustomTextMessageEncoder(this, difiGatewaySettings);
        }

        public override MessageEncoder Encoder
        {
            get
            {
                return _encoder;
            }
        }

        public override MessageVersion MessageVersion
        {
            get
            {
                return MessageVersion.Soap11;
            }
        }
    }
}
