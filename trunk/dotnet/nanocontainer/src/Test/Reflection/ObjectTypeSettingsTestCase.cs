using NanoContainer.IntegrationKit;
using NanoContainer.Reflection;
using NUnit.Framework;

namespace Test.Reflection
{
	[TestFixture]
	public class ObjectTypeSettingsTestCase
	{
		[Test]
		public void OneQualifier()
		{
			ObjectTypeSettings ot = new ObjectTypeSettings("Test");
			Assert.IsNotNull(ot.Name);
			Assert.IsNull(ot.Assembly);
		}

		[Test]
		public void TwoQualifiers()
		{
			ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd");
			Assert.IsNotNull(ot.Name);
			Assert.AreEqual("aassd", ot.Assembly);
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void ThreeQualifiers()
		{
			new ObjectTypeSettings("Test, aassd,fdf");
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void FourQualifiers()
		{
			new ObjectTypeSettings("Test, aassd,fdf,aaa");
		}

		[Test]
		public void FiveQualifiers()
		{
			ObjectTypeSettings ot = new ObjectTypeSettings("Test, aassd,fdf,aaa,ads");
			Assert.IsNotNull(ot.Name);
			Assert.AreEqual("aassd,fdf,aaa,ads", ot.Assembly);
		}

		[Test]
		[ExpectedException(typeof (PicoCompositionException))]
		public void SixQualifiers()
		{
			new ObjectTypeSettings("Test, aassd,fdf,aaa,ads,asdsa");
		}

	}
}