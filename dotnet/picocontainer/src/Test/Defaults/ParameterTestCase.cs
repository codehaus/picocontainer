using System;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for ParameterTestCase.
	/// </summary>
	[TestFixture]
	public class ParameterTestCase
	{
		[Test]
		public void TestComponentParameterFetches()
		{
			DefaultPicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			ComponentParameter parameter = new ComponentParameter(typeof (Touchable));

			Assert.IsNotNull(pico.GetComponentInstance(typeof (Touchable)));
			Touchable touchable = (Touchable) parameter.ResolveAdapter(pico, typeof (Touchable)).ComponentInstance;
			Assert.IsNotNull(touchable);
		}

		[Test]
		public void TestConstantParameter()
		{
			Object value = new Object();
			ConstantParameter parameter = new ConstantParameter(value);
			IMutablePicoContainer picoContainer = new DefaultPicoContainer();
			Assert.AreSame(value, parameter.ResolveAdapter(picoContainer, typeof (object)).ComponentInstance);
		}

		[Test]
		public void TestDependsOnTouchableWithTouchableSpecifiedAsConstant()
		{
			DefaultPicoContainer pico = new DefaultPicoContainer();
			SimpleTouchable touchable = new SimpleTouchable();
			pico.RegisterComponentImplementation(typeof (DependsOnTouchable), typeof (DependsOnTouchable), new IParameter[]
				{
					new ConstantParameter(touchable)
				});
			object o = pico.ComponentInstances;
			Assert.IsTrue(touchable.wasTouched);
		}
	}
}