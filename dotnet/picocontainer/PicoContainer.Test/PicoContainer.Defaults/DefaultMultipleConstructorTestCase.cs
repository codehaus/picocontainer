using NUnit.Framework;
using PicoContainer.Core;
using PicoContainer.Defaults;
using PicoContainer.Tck;

namespace PicoContainer.Defaults
{
	[TestFixture]
	public class DefaultMultipleConstructorTestCase : AbstractMultipleConstructorTestCase
	{
		protected override IMutablePicoContainer createPicoContainer()
		{
			return new DefaultPicoContainer();
		}
	}
}