using System;
using System.Collections;
using System.IO;
using System.Reflection;

namespace NanoContainer.Reflection
{
	public class TypeLoader
	{
		public TypeLoader()
		{
		}

		private static Hashtable TypeMap = new Hashtable();

		public static Type GetType(string typeSettings)
		{
			return GetType(new ObjectTypeSettings(typeSettings));
		}

		public static Type GetType(ObjectTypeSettings typeSettings)
		{
			Type type = TypeMap[typeSettings.Name] as Type;
			if (type != null)
			{
				return type;
			}

			Assembly assemblyInstance = null;

			try
			{
				if (typeSettings.Assembly != null)
				{
					assemblyInstance = Assembly.Load(typeSettings.Assembly);
				}
				else
				{
					assemblyInstance = Assembly.GetExecutingAssembly();
				}
			}
			catch (FileNotFoundException e)
			{
				// Maybe it is a in memory assembly try it
				foreach (Assembly a in AppDomain.CurrentDomain.GetAssemblies())
				{
					if (a.FullName.Replace(" ", "") == typeSettings.Assembly)
					{
						assemblyInstance = a;
						break;
					}
				}

				if (assemblyInstance == null)
				{
					throw new TypeLoadException("Can not load the assembly " + typeSettings.Assembly + " needed for the type: " + typeSettings.Type, e);
				}
			}

			try
			{
				type = assemblyInstance.GetType(typeSettings.Type, true, false);
				TypeMap.Add(typeSettings.Name, type);

				return type;
			}
			catch (Exception ex)
			{
				Assembly[] assemblies = AppDomain.CurrentDomain.GetAssemblies();
				foreach (Assembly assembly in assemblies)
				{
					try
					{
						type = assembly.GetType(typeSettings.Type, true, false);
						TypeMap.Add(typeSettings.Name, type);

						return type;
					}
					catch (Exception)
					{
					}
				}
				throw ex;
			}

		}
	}
}