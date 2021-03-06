using System;
using PicoContainer.Defaults;

namespace NanoContainer.Script.Xml
{
	public class Constants
	{
		public static readonly string REGISTER_COMPONENT_INSTANCE = "RegisterComponentInstance";
		public static readonly string REGISTER_COMPONENT_IMPLEMENTATION = "RegisterComponentImplementation";
		private static readonly Type defaultPicoContainerType = typeof(DefaultPicoContainer);

		public static Type DefaultPicoContainerType
		{
			get { return defaultPicoContainerType; }
		}

	}
}
