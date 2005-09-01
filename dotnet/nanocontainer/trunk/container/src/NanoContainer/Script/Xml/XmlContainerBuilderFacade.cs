using System.IO;

namespace NanoContainer.Script.Xml
{
	public class XmlContainerBuilderFacade : ContainerBuilderFacade
	{
		public XmlContainerBuilderFacade(StreamReader streamReader) 
			: base(new XMLContainerBuilder(streamReader))
		{
		}
	}
}
