using System;
using System.IO;
using System.Reflection;
using NanoContainer.Script;
using NUnit.Framework;

namespace NanoContainer.Tests.Script
{
	[TestFixture]
	public class AssemblyReferenceCacheTest
	{
		[Test]
		public void AddExternalAssemblyToCache()
		{
			AssemblyReferenceCache arc = new AssemblyReferenceCache();
			FileInfo testCompDll = new FileInfo("../../../TestComp/bin/Debug/TestComp.dll");
			arc.add("TestComp.dll", testCompDll.FullName);

			Assembly assembly = arc.FindAssembly(null, new ResolveEventArgs("TestComp, blah"));

			Assert.IsNotNull(assembly);
			Assert.AreEqual("TestComp", assembly.GetName().Name);
		}

		[Test]
		public void ReturnNullWhenAssemblyNameIsNotFound()
		{
			AssemblyReferenceCache arc = new AssemblyReferenceCache();
			Assembly assembly = arc.FindAssembly(null, new ResolveEventArgs("System, ..."));

			Assert.IsNull(assembly);
		}
	}
}
