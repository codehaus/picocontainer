using System;
using System.Collections;
using System.IO;
using System.Reflection;
using Microsoft.CSharp;
using NanoContainer.Reflection;
using NanoContainer.Script.Compiler;
using NUnit.Framework;

namespace Test.Reflection
{
	[TestFixture]
	public class TypeLoaderTestCase
	{
		[Test]
		public void Basic()
		{
			Type expectedType = typeof (string);
			Type t = TypeLoader.GetType(expectedType.FullName);
			Assert.AreEqual(expectedType, t);
		}

		[Test]
		public void FullType()
		{
			Stream strm = Assembly.GetExecutingAssembly()
				.GetManifestResourceStream(@"NanoContainer.Test.TestScripts.test.cs");

			Assembly assembly = FrameworkCompiler.Compile(new CSharpCodeProvider(), new StreamReader(strm), new ArrayList());

			Type t = assembly.GetTypes()[0];
			t = TypeLoader.GetType(t.FullName + "," + assembly.FullName);
		}

		[Test]
		[ExpectedException(typeof (TypeLoadException))]
		public void WrongType()
		{
			Type t = TypeLoader.GetType("aaa,aaa,aaa,aaa,aaa");
		}

		[Test]
		[ExpectedException(typeof (TypeLoadException))]
		public void NonExistingType()
		{
			Stream strm = Assembly.GetExecutingAssembly().GetManifestResourceStream(@"NanoContainer.Test.TestScripts.test.cs");

			Assembly assembly = FrameworkCompiler.Compile(new CSharpCodeProvider(), new StreamReader(strm), new ArrayList());

			Type t = assembly.GetTypes()[0];
			t = TypeLoader.GetType(new ObjectTypeSettings(t.FullName + "axsdf," + assembly.FullName));
		}

	}
}