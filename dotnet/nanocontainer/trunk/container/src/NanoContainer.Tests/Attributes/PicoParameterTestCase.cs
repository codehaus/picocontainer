using NanoContainer.Attributes;
using NanoContainer.Tests.TestModel;
using NUnit.Framework;
using PicoContainer;

namespace NanoContainer.Tests.Attributes
{
	[TestFixture]
	public class PicoParameterTestCase
	{
		private IPicoContainer picoContainer;

		[SetUp]
		public void SetUp()
		{
			ContainerBuilderFacade containerBuilderFacade = new AttributeBasedContainerBuilderFacade();
			picoContainer = containerBuilderFacade.Build(new string[] {"NanoContainer.Tests.dll"});
		}

		[Test]
		public void BuildStringDependentComponent()
		{
			DependentOnStrings component = picoContainer.GetComponentInstanceOfType(typeof(DependentOnStrings)) as DependentOnStrings;
			Assert.IsNotNull(component);

			Assert.AreEqual("ONE", component.One);
			Assert.AreEqual("TWO", component.Two);
		}

		[Test]
		public void BuildConstantDependentComponent()
		{
			DependentOnConstants doc = picoContainer.GetComponentInstance(typeof(DependentOnConstants)) as DependentOnConstants;

			Assert.AreEqual("Hello World", doc.Name);
			Assert.AreEqual(70, doc.Count);
			Assert.AreEqual(99.9f, doc.Percentage);
		}

		[Test]
		public void TypeWithBothComponentAndConstantParametersDependencies()
		{
			Airplane airplane = picoContainer.GetComponentInstanceOfType(typeof(Airplane)) as Airplane;
			Assert.AreEqual("Boeing", airplane.Manufacturer);
			Assert.AreEqual("Jet Propelled", airplane.Engine.Name);
		}

		[Test]
		public void AttributeBasedComponentWithArrayDependency()
		{
			EngineFactory engineFactory = picoContainer.GetComponentInstanceOfType(typeof(EngineFactory)) as EngineFactory;
			Assert.AreEqual(2, engineFactory.Engines.Length);
		}
	}
}
