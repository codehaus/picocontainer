using System.IO;

namespace NanoContainer.Script.JSharp
{
	public class JSharpContainerBuilderFacade : ContainerBuilderFacade
	{
		public JSharpContainerBuilderFacade(StreamReader streamReader) 
			: base(new JSharpContainerBuilder(streamReader))
		{
		}
	}
}
