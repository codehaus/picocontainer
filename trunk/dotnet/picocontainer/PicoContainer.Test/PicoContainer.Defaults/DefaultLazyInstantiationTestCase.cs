using System;
using NUnit.Framework;
using PicoContainer.Core;
using PicoContainer.Core.Defaults;
using PicoContainer.Core.Tests.Tck;

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