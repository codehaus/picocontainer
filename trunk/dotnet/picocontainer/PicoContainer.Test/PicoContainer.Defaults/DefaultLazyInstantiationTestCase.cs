using System;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;

namespace Test.Defaults
{
	[TestFixture]
	public class DefaultLazyInstantiationTestCase : AbstractLazyInstantiationTestCase
	{
		protected override IMutablePicoContainer createPicoContainer()
		{
			return new DefaultPicoContainer();
		}
	}
}