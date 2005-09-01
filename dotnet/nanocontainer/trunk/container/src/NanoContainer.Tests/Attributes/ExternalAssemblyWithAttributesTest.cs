using System.Collections.Specialized;
using System.Text;
using NanoContainer.Attributes;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Tests.Attributes
{
	[TestFixture]
	public class ExternalAssemblyWithAttributesTest
	{
		[Test]
		public void InstantiateATypeFromAssemblyThatHasReferenceToAnotherAssembly()
		{
			StringCollection assemblies = new StringCollection();
			assemblies.Add("../../../TestCompWithAttributes/bin/Debug/TestCompWithAttributes.dll");
			
			ContainerBuilderFacade cbf = new AttributeBasedContainerBuilderFacade();
			IMutablePicoContainer picoContainer = cbf.Build(assemblies);
			Assert.AreEqual(1, picoContainer.ComponentInstances.Count);
			object instance = picoContainer.GetComponentInstance("testcomp3-key");
			
			Assert.AreEqual("TestComp3", instance.GetType().FullName); // from the assembly passed in
			Assert.AreEqual("TestComp", instance.GetType().BaseType.FullName); // base type from a second externally assembly
		}

		[Test]
		public void BuildContainerFromMoreThanOneAssembly()
		{
			StringCollection assemblies = new StringCollection();
			assemblies.Add("../../../TestCompWithAttributes/bin/Debug/TestCompWithAttributes.dll");
			assemblies.Add("../../../NotStartable/bin/Debug/NotStartable.dll");
			
			IMutablePicoContainer parent = new DefaultPicoContainer();
			parent.RegisterComponentInstance(new StringBuilder("This is needed for type NotStartable"));

			ContainerBuilderFacade cbf = new AttributeBasedContainerBuilderFacade();
			IMutablePicoContainer picoContainer = cbf.Build(parent, assemblies);

			Assert.IsNotNull(picoContainer.GetComponentInstance("testcomp3-key"));
			Assert.IsNotNull(picoContainer.GetComponentInstance("notstartable"));
		}
	}
}
