using System;
using System.Collections;
using System.Reflection;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer
{
	public class DefaultNanoContainer
	{
		private Hashtable assemblies = new Hashtable();
		private IMutablePicoContainer picoContainer;

		public DefaultNanoContainer()
		{
			AddAssembly(Assembly.GetCallingAssembly());
			this.picoContainer = new DefaultPicoContainer();
		}

		protected void AddAssembly(Assembly assembly)
		{
			assemblies.Add(assembly.GetName(), assembly);

			foreach(AssemblyName assemblyName in assembly.GetReferencedAssemblies())
			{
				assemblies.Add(assemblyName, Assembly.Load(assemblyName));
			}
		}

		public IComponentAdapter RegisterComponentImplementation(string componentImplementationName)
		{
			Type type = findType(componentImplementationName);
			return picoContainer.RegisterComponentImplementation(type);
		}

		public IComponentAdapter RegisterComponentImplementation(object key, string componentImplementationName)
		{
			Type type = findType(componentImplementationName);
			return picoContainer.RegisterComponentImplementation(key, type);
		}

		/// <summary>
		/// This method should be used to ensure that an exception is thrown if the type is not valid
		/// </summary>
		private Type findType(string typeName)
		{
			Type type = null;

			foreach(Assembly assembly in assemblies.Values)
			{
				type = assembly.GetType(typeName);
				
				if(type != null)
				{
					return type;
				}
			}
			
			throw new TypeLoadException("Unable to find type: " + typeName);
		}

		public IMutablePicoContainer Pico
		{
			get { return picoContainer; }
		}
	}
}
