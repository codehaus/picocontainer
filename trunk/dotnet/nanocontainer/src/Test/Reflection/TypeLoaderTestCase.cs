using System;
using NUnit.Framework;
using NanoContainer.Reflection;
using NanoContainer.IntegrationKit;
using System.Reflection;
using NanoContainer.Script.Compiler;
using System.Collections;
using System.IO;

namespace Test.Reflection
{
  [TestFixture]
  public class TypeLoaderTestCase
	{
    [Test]
    public void TestBasic()
    {
      Type expectedType = typeof(string);
      Type t = TypeLoader.GetType(expectedType.FullName);
      Assert.AreEqual(expectedType,t);
    }

    [Test]
    public void TestFullType() {
      System.IO.Stream strm = System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream(@"NanoContainer.Test.TestScripts.test.cs");

      Assembly assembly = FrameworkCompiler.Compile(new Microsoft.CSharp.CSharpCodeProvider(),new StreamReader(strm),new ArrayList());

      Type t = assembly.GetTypes()[0];
      t = TypeLoader.GetType(t.FullName+","+assembly.FullName);
    }

    [Test]
    [ExpectedException(typeof(TypeLoadException))]
    public void TestWrongType() {
      Type t = TypeLoader.GetType("aaa,aaa,aaa,aaa,aaa");
    }

    [Test]
    [ExpectedException(typeof(TypeLoadException))]
    public void TestNonExistingType() {
      System.IO.Stream strm = System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream(@"NanoContainer.Test.TestScripts.test.cs");

      Assembly assembly = FrameworkCompiler.Compile(new Microsoft.CSharp.CSharpCodeProvider(),new StreamReader(strm),new ArrayList());

      Type t = assembly.GetTypes()[0];
      t = TypeLoader.GetType(new ObjectTypeSettings(t.FullName+"axsdf,"+assembly.FullName));
    }

  }
}
