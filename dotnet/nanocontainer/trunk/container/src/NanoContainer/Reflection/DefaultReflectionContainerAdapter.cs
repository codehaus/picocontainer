using System;
using PicoContainer;
using PicoContainer.Defaults;

namespace NanoContainer.Reflection
{
	public class DefaultReflectionContainerAdapter : IReflectionContainerAdapter
	{
		private readonly IMutablePicoContainer picoContainer;

		public DefaultReflectionContainerAdapter() : this(new DefaultPicoContainer())
		{
		}

		public DefaultReflectionContainerAdapter(IMutablePicoContainer picoContainer)
		{
			this.picoContainer = picoContainer;
		}

		public IComponentAdapter RegisterComponentImplementation(string componentImplementationTypeName)
		{
			return this.RegisterComponentImplementation(new ObjectTypeSettings(componentImplementationTypeName));
		}

		public IComponentAdapter RegisterComponentImplementation(object key, string componentImplementationTypeName)
		{
			return this.RegisterComponentImplementation(key, new ObjectTypeSettings(componentImplementationTypeName));
		}

		public IComponentAdapter RegisterComponentImplementation(string componentImplementationTypeName,
		                                                         string[] parameterTypesAsString,
		                                                         string[] parameterValuesAsString)
		{
			return this.RegisterComponentImplementation(new ObjectTypeSettings(componentImplementationTypeName), parameterTypesAsString, parameterValuesAsString);
		}

		public IComponentAdapter RegisterComponentImplementation(object key,
		                                                         string componentImplementationTypeName,
		                                                         string[] parameterTypesAsString,
		                                                         string[] parameterValuesAsString)
		{
			return this.RegisterComponentImplementation(key, new ObjectTypeSettings(componentImplementationTypeName), parameterTypesAsString, parameterValuesAsString);
		}

		public IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings typeSettings)
		{
			return picoContainer.RegisterComponentImplementation(TypeLoader.GetType(typeSettings));
		}

		public IComponentAdapter RegisterComponentImplementation(object key, ObjectTypeSettings componentImplementationTypeName)
		{
			return picoContainer.RegisterComponentImplementation(key, TypeLoader.GetType(componentImplementationTypeName));
		}

		public IComponentAdapter RegisterComponentImplementation(object key,
		                                                         ObjectTypeSettings componentImplementationTypeName,
		                                                         string[] parameterTypesAsString,
		                                                         string[] parameterValuesAsString)
		{
			Type componentImplementation = TypeLoader.GetType(componentImplementationTypeName);

			return RegisterComponentImplementation(parameterTypesAsString, parameterValuesAsString, key, componentImplementation);
		}

		public IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationTypeName,
		                                                         string[] parameterTypesAsString,
		                                                         string[] parameterValuesAsString)
		{
			Type type = TypeLoader.GetType(componentImplementationTypeName);
			return RegisterComponentImplementation(parameterTypesAsString, parameterValuesAsString, type, type);
		}


		private IComponentAdapter RegisterComponentImplementation(string[] parameterTypesAsString, string[] parameterValuesAsString, object key, Type componentImplementation)
		{
			IParameter[] parameters = new IParameter[parameterTypesAsString.Length];
			for (int i = 0; i < parameters.Length; i++)
			{
				Type paramTypeClass = TypeLoader.GetType(parameterTypesAsString[i]);
				object value = Convert.ChangeType(parameterValuesAsString[i], paramTypeClass);
				parameters[i] = new ConstantParameter(value);
			}
			return picoContainer.RegisterComponentImplementation(key, componentImplementation, parameters);
		}

		public IMutablePicoContainer PicoContainer
		{
			get { return picoContainer; }
		}
	}
}