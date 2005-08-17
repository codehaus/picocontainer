using System;
using PicoContainer;

namespace NanoContainer.Reflection
{
	public interface IReflectionContainerAdapter
	{
		IComponentAdapter RegisterComponentImplementation(string componentImplementationTypeName);

		IComponentAdapter RegisterComponentImplementation(object key, string componentImplementationTypeName);

		IComponentAdapter RegisterComponentImplementation(object key,
		                                                  string componentImplementationTypeName,
		                                                  string[] parameterTypesAsString,
		                                                  string[] parameterValuesAsString);

		IComponentAdapter RegisterComponentImplementation(string componentImplementationTypeName,
		                                                  string[] parameterTypesAsString,
		                                                  string[] parameterValuesAsString);

		IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationTypeName);

		IComponentAdapter RegisterComponentImplementation(object key, ObjectTypeSettings componentImplementationTypeName);

		IComponentAdapter RegisterComponentImplementation(object key,
		                                                  ObjectTypeSettings componentImplementationTypeName,
		                                                  string[] parameterTypesAsString,
		                                                  string[] parameterValuesAsString);

		IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationTypeName,
		                                                  string[] parameterTypesAsString,
		                                                  string[] parameterValuesAsString);

		IMutablePicoContainer PicoContainer { get; }

	}

}