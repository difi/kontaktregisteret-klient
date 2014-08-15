using System;
using System.ServiceModel.Channels;
using System.ServiceModel.Description;
using System.Xml;

namespace KontaktregisteretGateway.WcfCustom
{
    public class CustomTextMessageBindingElement : MessageEncodingBindingElement, IWsdlExportExtension
    {
        public static DifiGatewaySettings DifiGatewaySettings { get; set; }
        private readonly XmlDictionaryReaderQuotas _readerQuotas;

        public CustomTextMessageBindingElement(DifiGatewaySettings difiGatewaySettings)
        {
            DifiGatewaySettings = difiGatewaySettings;
            _readerQuotas = new XmlDictionaryReaderQuotas();
        }

        public override MessageEncoderFactory CreateMessageEncoderFactory()
        {
            return new CustomTextMessageEncoderFactory(DifiGatewaySettings);
        }

        public override MessageVersion MessageVersion
        {
            get { return MessageVersion.Soap11; }
            set { }
        }

        public override BindingElement Clone()
        {
            return new CustomTextMessageBindingElement(DifiGatewaySettings);
        }

        public override IChannelFactory<TChannel> BuildChannelFactory<TChannel>(BindingContext context)
        {
            if (context == null)
                throw new ArgumentNullException("context");

            context.BindingParameters.Add(this);
            return context.BuildInnerChannelFactory<TChannel>();
        }

        public override bool CanBuildChannelFactory<TChannel>(BindingContext context)
        {
            if (context == null)
                throw new ArgumentNullException("context");

            return context.CanBuildInnerChannelFactory<TChannel>();
        }

        public override IChannelListener<TChannel> BuildChannelListener<TChannel>(BindingContext context)
        {
            if (context == null)
                throw new ArgumentNullException("context");

            context.BindingParameters.Add(this);
            return context.BuildInnerChannelListener<TChannel>();
        }

        public override bool CanBuildChannelListener<TChannel>(BindingContext context)
        {
            if (context == null)
                throw new ArgumentNullException("context");

            context.BindingParameters.Add(this);
            return context.CanBuildInnerChannelListener<TChannel>();
        }

        public override T GetProperty<T>(BindingContext context)
        {
            if (typeof(T) == typeof(XmlDictionaryReaderQuotas))
                return (T)(object)this._readerQuotas;
            
            return base.GetProperty<T>(context);
        }

        void IWsdlExportExtension.ExportContract(WsdlExporter exporter, WsdlContractConversionContext context)
        {
        }

        void IWsdlExportExtension.ExportEndpoint(WsdlExporter exporter, WsdlEndpointConversionContext context)
        {
            var mebe = new TextMessageEncodingBindingElement();
            mebe.MessageVersion = this.MessageVersion;
            ((IWsdlExportExtension)mebe).ExportEndpoint(exporter, context);
        }
    }
}
