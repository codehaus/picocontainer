using System;
using System.Collections;
using System.Reflection;

namespace NanoContainer.Attributes
{
	public class AssemblyUtil
	{
		public int GetClassCount(Assembly assembly)
		{
			Type[] result = assembly.GetTypes();
			return result.Length;
		}

		public int GetClassCount(Assembly assembly, Type attributeType)
		{
			return GetTypes(assembly, attributeType).Length;
		}

		public Type[] GetTypes(Assembly assembly, Type attributeType)
		{
			Type[] types = assembly.GetTypes();
			ArrayList result = new ArrayList();
			foreach (Type currentType in types)
			{
				if (currentType.GetCustomAttributes(attributeType, false).Length>0)
				{
					result.Add(currentType);
				}
			}
			return result.ToArray(typeof(Type)) as Type[];
		}
	}
}
