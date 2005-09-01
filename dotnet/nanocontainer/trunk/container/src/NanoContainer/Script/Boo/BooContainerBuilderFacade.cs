using System.IO;

namespace NanoContainer.Script.Boo
{
	public class BooContainerBuilderFacade : ContainerBuilderFacade
	{
		public BooContainerBuilderFacade(StreamReader streamReader) 
			: base(new BooContainerBuilder(streamReader))
		{
		}
	}
}
