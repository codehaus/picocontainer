using System.IO;

namespace NanoContainer.Script.VB
{
	public class VBContainerBuilderFacade : ContainerBuilderFacade
	{
		public VBContainerBuilderFacade(StreamReader streamReader) 
			: base(new VBContainerBuilder(streamReader))
		{
		}
	}
}
