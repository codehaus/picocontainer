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
			pico.RegisterComponentImplementation(typeof (ITouchable), typeof (SimpleTouchable));
			ComponentParameter parameter = new ComponentParameter(typeof (ITouchable));

			Assert.IsNotNull(pico.GetComponentInstance(typeof (ITouchable)));
			ITouchable touchable = (ITouchable) parameter.ResolveInstance(pico, null, typeof (ITouchable));
			Assert.IsNotNull(touchable);
		}

		[Test]
		public void TestConstantParameter()
		{
			Object value = new Object();
			ConstantParameter parameter = new ConstantParameter(value);
			IMutablePicoContainer picoContainer = new DefaultPicoContainer();
			Assert.AreSame(value, parameter.ResolveInstance(picoContainer, null,typeof (object)));
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
			Assert.IsTrue(touchable.WasTouched);
		}
	}
}