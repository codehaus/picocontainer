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

		private static Assembly GetAssemply(ObjectTypeSettings typeSettings)
		{
			try
			{
				if (typeSettings.Assembly != null)
				{
					return Assembly.Load(typeSettings.Assembly);
				}
				else
				{
					return Assembly.GetExecutingAssembly();
				}
			}
			catch (FileNotFoundException e)
			{
				// Maybe it is a in memory assembly try it
				foreach (Assembly assembly in AppDomain.CurrentDomain.GetAssemblies())
				{
					if (assembly.FullName.Replace(" ", "") == typeSettings.Assembly)
					{
						return assembly;
					}
				}
				
				// Assembly wasn't found
				throw new TypeLoadException("Can not load the assembly " + typeSettings.Assembly + " needed for the type: " + typeSettings.Type, e);
			}
		}

		public static Type GetType(ObjectTypeSettings typeSettings)
		{
			Type type = TypeMap[typeSettings.Name] as Type;
			if (type != null)
			{
				return type;
			}

			Assembly assemblyInstance = GetAssemply(typeSettings);

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
						// ignore
					}
				}
				throw ex;
			}
		}
	}
}