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

		public DefaultNanoContainer() : this(Assembly.GetCallingAssembly())
		{
		}

		public DefaultNanoContainer(Assembly assembly) 
			: this(assembly, new DefaultPicoContainer())
		{
			AddAssembly(assembly);
			this.picoContainer = new DefaultPicoContainer();
		}

		public DefaultNanoContainer(IMutablePicoContainer picoContainer) 
			: this(Assembly.GetCallingAssembly(), picoContainer)
		{
		}

		public DefaultNanoContainer(Assembly assembly, IMutablePicoContainer picoContainer)
		{
			AddAssembly(assembly);
			this.picoContainer = picoContainer;
		}

		public IMutablePicoContainer Pico
		{
			get { return picoContainer; }
		}

		/// <summary>
		/// When adding an assembly it is important to also include its referenced assemblies.
		/// If this is not done then standard Types will NOT be returned, which would cause all 
		/// kinds of problems.
		/// </summary>
		public virtual void AddAssembly(Assembly assembly)
		{
			assemblies.Add(assembly.GetName(), assembly);

			foreach(AssemblyName assemblyName in assembly.GetReferencedAssemblies())
			{
				assemblies.Add(assemblyName, Assembly.Load(assemblyName));
			}
		}

		public virtual IComponentAdapter RegisterComponentImplementation(string componentImplementationName)
		{
			Type type = findType(componentImplementationName);
			return picoContainer.RegisterComponentImplementation(type);
		}

		public virtual IComponentAdapter RegisterComponentImplementation(object key, string componentImplementationName)
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
	}
}
