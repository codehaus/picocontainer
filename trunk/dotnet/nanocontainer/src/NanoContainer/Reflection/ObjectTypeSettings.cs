using System;
using NanoContainer.IntegrationKit;

namespace NanoContainer.Reflection
{
	/// <summary>
	/// Base class for all providers settings within the UIP configuration settings in the config file.
	/// </summary>
	public class ObjectTypeSettings
	{
		#region Declares Variables

		private const string Comma = ",";

		private string name;
		private string type;
		private string assembly;

		#endregion

		public ObjectTypeSettings(string name, string type, string assembly)
		{
			this.name = name;
			this.type = type;
			this.assembly = assembly;
		}

		public ObjectTypeSettings(string fullType)
		{
			//  fix up type/asm strings
			SplitType(fullType);
			name = fullType;
		}

		private void SplitType(string fullType)
		{
			string[] parts = fullType.Split(Comma.ToCharArray());

			if (parts.Length == 1)
				type = fullType;
			else if (parts.Length == 2)
			{
				type = parts[0].Trim();
				this.assembly = parts[1].Trim();
			}
			else if (parts.Length == 5)
			{
				//  set the object type name
				this.type = parts[0].Trim();

				//  set the object assembly name
				this.assembly = String.Concat(parts[1].Trim() + Comma,
				                               parts[2].Trim() + Comma,
				                               parts[3].Trim() + Comma,
				                               parts[4].Trim());
			}
			else
				throw new PicoCompositionException("The type " + fullType + "is not qualified enough to load or has an invalid format.");
		}

		#region Properties

		/// <summary>
		/// Gets the object name
		/// </summary>
		public string Name
		{
			get { return name; }
		}

		/// <summary>
		/// Gets the object full qualified type name
		/// </summary>
		public string Type
		{
			get { return type; }
		}

		/// <summary>
		/// Gets the fully qualified assembly name of the object.
		/// </summary>
		public string Assembly
		{
			get { return assembly; }
		}

		#endregion
	}
}