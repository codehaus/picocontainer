using System.IO;

namespace NanoContainer.Script.CSharp
{
	public class CSharpContainerBuilderFacade : ContainerBuilderFacade
	{
		public CSharpContainerBuilderFacade(StreamReader streamReader)
			: base(new CSharpContainerBuilder(streamReader))
		{
		}
	}
}