using System;
using NUnit.Framework;
using PicoContainer;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;

using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for DefaultLazyInstantiationTestCase.
	/// </summary>
	[TestFixture]
	public class DefaultLazyInstantiationTestCase : AbstractLazyInstantiationTestCase 
	{
    protected override IMutablePicoContainer createPicoContainer() 
    {
      return new DefaultPicoContainer();
    }	
  }
}
