using System;
using NUnit.Framework;
using NanoContainer.Reflection;
using NanoContainer.IntegrationKit;

namespace Test.Reflection
{
  [TestFixture]
  public class ObjectTypeSettingsTestCase
	{
    [Test]
    public void TestOneQualifier()
    {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test");
      Assert.IsNotNull(ot.Name);
      Assert.IsNull(ot.Assembly);
    }

    [Test]
    public void TestTwoQualifiers() {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd");
      Assert.IsNotNull(ot.Name);
      Assert.AreEqual("aassd",ot.Assembly);
    }

    [Test]
    [ExpectedException(typeof(PicoCompositionException))]
    public void TestThreeQualifiers() {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd,fdf");
    }

    [Test]
    [ExpectedException(typeof(PicoCompositionException))]
    public void TestFourQualifiers() {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd,fdf,aaa");
    }

    [Test]
    public void TestFiveQualifiers() {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd,fdf,aaa,ads");
      Assert.IsNotNull(ot.Name);
      Assert.AreEqual("aassd,fdf,aaa,ads",ot.Assembly);
    }

    [Test]
    [ExpectedException(typeof(PicoCompositionException))]
    public void TestSixQualifiers() {
      ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd,fdf,aaa,ads,asdsa");
    }

  }
}
