using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using NUnit.Framework;
using PicoContainer.Core;
using PicoContainer.Core.Defaults;
using PicoContainer.Core.Tests.Tck;

namespace Test.Defaults
{
	[TestFixture]
	public class DefaultPicoContainerTreeSerializationTestCase : AbstractPicoContainerTestCase
	{
		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent)
		{
			return new DefaultPicoContainer(parent);
		}

		protected override IMutablePicoContainer CreatePicoContainer(IPicoContainer parent, ILifecycleManager lifecycleManager)
		{
			return new DefaultPicoContainer(new DefaultComponentAdapterFactory(), parent, lifecycleManager);
		}

		[Test]
		public void ContainerIsDeserializableWithParent()
		{
			IPicoContainer parent = CreatePicoContainer(null);
			IMutablePicoContainer child = CreatePicoContainer(parent);

			using (Stream stream = new MemoryStream())
			{
				// Serialize it to memory
				IFormatter formatter = new BinaryFormatter();
				formatter.Serialize(stream, child);

				// De-Serialize from memory
				stream.Seek(0, 0); // reset stream to begining

				child = null; // make
				child = (IMutablePicoContainer) formatter.Deserialize(stream);
			}

			Assert.IsNotNull(child.Parent);
		}
	}
}
