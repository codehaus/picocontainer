using System;
using NanoContainer.Attributes;
using NUnit.Framework;

namespace NanoContainer.Tests.Attributes
{
	[TestFixture]
	public class AssemblyUtilTestCase
	{
		[Test]
		public void FindFooAttribute()
		{
			AssemblyUtil util = new AssemblyUtil();
			Assert.AreEqual(util.GetClassCount(typeof(Bar).Assembly, typeof(FooAttribute)),1);
		}

		[Test]
		public void FindBarType()
		{
			AssemblyUtil util = new AssemblyUtil();
			Type barType = typeof(Bar);
			Type actualType = util.GetTypes(barType.Assembly, typeof(FooAttribute))[0];

			Assert.AreEqual(barType, actualType);
		}
	}

	/// <summary>
	/// attribute for testing only
	/// </summary>
	public class FooAttribute : Attribute
	{
		// test attribute
	}

	/// <summary>
	/// class tagged with an attribute for testing only
	/// </summary>
	[Foo]
	public class Bar
	{
	}
}
