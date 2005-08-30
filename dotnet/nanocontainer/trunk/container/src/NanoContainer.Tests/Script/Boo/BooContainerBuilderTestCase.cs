using System;
using System.Collections;
using System.Collections.Specialized;
using System.IO;
using System.Text;
using NanoContainer.IntegrationKit;
using NanoContainer.Script.Boo;
using NanoContainer.Test.TestModel;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using Test.Script;

namespace NanoContainer.Tests.Script.Boo
{
	/// <summary>
	/// Summary description for BooContainerBuilderTestCase.
	/// </summary>
	[TestFixture]
	public class BooContainerBuilderTestCase : AbstractScriptedContainerBuilderTestCase
	{
		[Test]
		public void ContainerCanBeBuiltWithParent()
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new BooContainerBuilder(GetStreamReader(@"NanoContainer.Tests.TestScripts.test.boo")), parent, new ArrayList());
			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual("Boo", pico.GetComponentInstance("foo"));
		}

		[Test]
		public void NanoContainerCanBeBuiltFromCodeOnTheFly()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string code = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentInstance(10, 1000)
		return p

			";

			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilder containerBuilder = new BooContainerBuilder(ScriptFixture.BuildStreamReader(code));
			IPicoContainer pico = BuildContainer(containerBuilder, parent, new ArrayList());

			Assert.IsNotNull(pico);
			Assert.AreSame(parent, pico.Parent);
			Assert.AreEqual(1000, pico.GetComponentInstance(10));
		}

		[Test]
		public void SimpleContent()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentInstance(10, ""ten"")
		p.RegisterComponentInstance(""Hello"", ""Boo"")
		p.RegisterComponentInstance(""double"", 100.0)
		return p

			";

			IPicoContainer pico = BuildContainer(script);
			Assert.AreEqual("ten", pico.GetComponentInstance(10));
			Assert.AreEqual("Boo", pico.GetComponentInstance("Hello"));
			Assert.AreEqual(100.0, pico.GetComponentInstance("double"));
		}

		[Test]
		public void CreateSimpleContainer()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(typeof(System.Text.StringBuilder))
		p.RegisterComponentImplementation(typeof(NanoContainer.Test.TestModel.DefaultWebServerConfig))
		// autowires using the two objects objects above
		p.RegisterComponentImplementation(""WebServer"", typeof(NanoContainer.Test.TestModel.DefaultWebServer))
		return p
			";

			IPicoContainer pico = BuildContainer(script);
			Assert.AreEqual(typeof (StringBuilder), pico.GetComponentInstance(typeof (StringBuilder)).GetType());
			Assert.AreEqual(typeof (DefaultWebServerConfig), pico.GetComponentInstance(typeof (DefaultWebServerConfig)).GetType());
			Assert.AreEqual(typeof (DefaultWebServer), pico.GetComponentInstance("WebServer").GetType());
		}

		[Test]
		public void CreateSimpleContainerWithExplicitKeysAndParameters()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(""buffer"", typeof(System.Text.StringBuilder))
		p.RegisterComponentImplementation(""config"", typeof(NanoContainer.Test.TestModel.DefaultWebServerConfig))
		p.RegisterComponentImplementation(""server"", typeof(NanoContainer.Test.TestModel.DefaultWebServer), 
			array(IParameter, (ComponentParameter(""config""), ComponentParameter(""buffer""))))
		return p
			";

			IPicoContainer pico = BuildContainer(script);

			Assert.AreEqual(3, pico.ComponentInstances.Count);
			Assert.IsNotNull(pico.GetComponentInstance("buffer"));
			Assert.IsNotNull(pico.GetComponentInstance("config"));
			Assert.IsNotNull(pico.GetComponentInstance("server"));
		}


		[Test]
		[ExpectedException(typeof (UnsatisfiableDependenciesException))]
		public void CreateWithInvalidKey()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(""buffer"", typeof(System.Text.StringBuilder))
		p.RegisterComponentImplementation(""config"", typeof(NanoContainer.Test.TestModel.DefaultWebServerConfig))
		p.RegisterComponentImplementation(""server"", typeof(NanoContainer.Test.TestModel.DefaultWebServer), 
			array(IParameter, (ComponentParameter(""config2""), ComponentParameter(""buffer""))))
		return p
			";

			IPicoContainer pico = BuildContainer(script);

			Assert.AreEqual(3, pico.ComponentInstances.Count);
			Assert.IsNotNull(pico.GetComponentInstance("buffer"));
			Assert.IsNotNull(pico.GetComponentInstance("config"));
			Assert.IsNotNull(pico.GetComponentInstance("server"));
		}

		[Test]
		public void ContainerCanHostAChild()
		{
			// Boo relies on indentation so start at column 1. MUST NOT BE REFORMATTED
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(typeof(System.Text.StringBuilder))
		p.RegisterComponentImplementation(""config"", typeof(NanoContainer.Test.TestModel.DefaultWebServerConfig))

		child = DefaultPicoContainer(p)
		p.RegisterComponentInstance(child)
		child.RegisterComponentImplementation(""server"", typeof(NanoContainer.Test.TestModel.DefaultWebServer))
		return p
			";

			IPicoContainer pico = BuildContainer(script);

			Assert.IsNotNull(pico.GetComponentInstance("config"));

			IPicoContainer childcontainer = (DefaultPicoContainer) pico.GetComponentInstance(typeof (DefaultPicoContainer));

			Assert.IsNotNull(childcontainer);
			Assert.IsNotNull(childcontainer.GetComponentInstance("server"));

			StringBuilder sb = (StringBuilder) pico.GetComponentInstance(typeof (StringBuilder));
			Assert.IsTrue(sb.ToString().IndexOf("-WebServer") != -1);
		}

		/// <summary>
		/// not sure if this is a good idea, .NET has a predefined way of finding assemblies
		///
		/// Boo doesn't allow you reference an external assembly not found in the normal ways.
		/// The workaround is to pass an array of assemblies to ContainerBuilder.BuildContainer() in code
		/// defeating the purpose using a Boo script an an injector. 
		///
		/// intentionally not marked as Test
		/// </summary>
		public void LoadFromAnExternalAssembly()
		{
			// not implemented
			FileInfo testCompDll = new FileInfo("../../../TestComp/bin/Debug/TestComp.dll");
			Assert.IsTrue(testCompDll.Exists);

			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer
import TestComp from TestComp

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(""foo"", typeof(TestComp))
		return p
			";

			// collect the external assemblies to pass into BuildContainer
			IList assemblies = new ArrayList();
			assemblies.Add(testCompDll.FullName);

			StreamReader scriptStream = new StreamReader(new MemoryStream(Encoding.UTF8.GetBytes(script)));
			IMutablePicoContainer parent = new DefaultPicoContainer();
			IPicoContainer pico = BuildContainer(new BooContainerBuilder(scriptStream), parent, assemblies);

			object fooTestComp = pico.GetComponentInstance("foo");
			Assert.IsNotNull(fooTestComp, "Container should have a 'foo' component");
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void UnknownClassThrowsPicoCompositionException()
		{
			string script = @"
import PicoContainer from PicoContainer
import PicoContainer.Defaults from PicoContainer

class BooInjector:
	
	[Property(Parent)]
	_parent as IPicoContainer
	
	def Compose() as IMutablePicoContainer:
		p = DefaultPicoContainer(_parent)
		p.RegisterComponentImplementation(typeof(Foo))
		return p
			";

			BuildContainer(script);
		}

		/// <summary>
		/// The XmlScriptContainerBuilder does not thow an exception for an empty script.
		/// 
		/// The Boo compiler returns an error resulting in a PicoCompositionException. If the desired behavior 
		/// is not to throw an exception on an empty script then this behavior
		/// should be coded in AbstractFrameworkContainerBuilder.
		/// </summary>
		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void EmptyScriptThrowsPicoCompositionException()
		{
			string script = @"";
			BuildContainer(script);
		}

		[Test]
		[ExpectedException(typeof (ArgumentNullException))]
		public void CreateContainerFromNullScriptThrowsArgumentNullException()
		{
			string script = null;
			StreamReader scriptStream = new StreamReader(new MemoryStream(new ASCIIEncoding().GetBytes(script)));
			BuildContainer(new BooContainerBuilder(scriptStream), null, new ArrayList());
		}

		private IPicoContainer BuildContainer(string script)
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			ContainerBuilder containerBuilder = new BooContainerBuilder(ScriptFixture.BuildStreamReader(script));
			
			// register "this" test dll as an assembly to use
			StringCollection assemblies = new StringCollection();
			assemblies.Add("NanoContainer.Tests.dll");

			return base.BuildContainer(containerBuilder, parent, assemblies);
		}
	}
}