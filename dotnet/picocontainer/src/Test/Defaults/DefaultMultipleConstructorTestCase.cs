using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for DefaultMultipleConstructorTestCase.
	/// </summary>
	[TestFixture]
	public class DefaultMultipleConstructorTestCase : AbstractMultipleConstructorTestCase
	{
		protected override IMutablePicoContainer createPicoContainer()
		{
			return new DefaultPicoContainer();
		}
	}
}