using System.IO;

namespace NanoContainer.Script.JS
{
	public class JSContainerBuilderFacade : ContainerBuilderFacade
	{
		public JSContainerBuilderFacade(StreamReader streamReader)
			: base(new JSContainerBuilder(streamReader))
		{
		}
	}
}
