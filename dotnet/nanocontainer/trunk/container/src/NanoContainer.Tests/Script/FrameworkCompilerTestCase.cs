using System;
using System.CodeDom.Compiler;
using System.Collections;
using System.Reflection;
using Microsoft.CSharp;
using NanoContainer.IntegrationKit;
using NanoContainer.Script;
using NUnit.Framework;

namespace NanoContainer.Tests.Script
{
	[TestFixture]
	public class FrameworkCompilerTestCase
	{
		[Test]
		public void ValidCodeCompilesSuccessfully()
		{
			FrameworkCompiler frameworkCompiler = new FrameworkCompiler();
			CodeDomProvider codeDomProvider = new CSharpCodeProvider();

			string script = @"
				public class ClassFromScript
				{
					public int zero = 0;
				}";

			Assembly assembly = frameworkCompiler.Compile(codeDomProvider, script, new ArrayList());
			Type type = assembly.GetType("ClassFromScript");

			Assert.AreEqual(1, assembly.GetTypes().Length);
			Assert.AreEqual("ClassFromScript", type.FullName);
		}

		[Test]
		[ExpectedException(typeof(PicoCompositionException))]
		public void InvalidCodeFailsToCompile()
		{
			FrameworkCompiler frameworkCompiler = new FrameworkCompiler();
			CodeDomProvider codeDomProvider = new CSharpCodeProvider();

			string script = @"
				public class ClassFromScript
				{
					this should cause code to fail
				}";

			frameworkCompiler.Compile(codeDomProvider, script, new ArrayList());
		}

		[Test]
		public void AddAssembliesFromWorkingDirectory()
		{
			CompilerParameters compilerParameters = new CompilerParameters();
			Assert.AreEqual(0, compilerParameters.ReferencedAssemblies.Count);

			MockFrameworkCompiler frameworkCompiler = new MockFrameworkCompiler();
			frameworkCompiler.CallAddAssemblies(compilerParameters);

			Assert.IsTrue(compilerParameters.ReferencedAssemblies.Count > 0);
		}

		public class MockFrameworkCompiler : FrameworkCompiler
		{
			public void CallAddAssemblies(CompilerParameters compilerParameters)
			{
				AddAssemblies(compilerParameters, new ArrayList());
			}
		}
	}
}
