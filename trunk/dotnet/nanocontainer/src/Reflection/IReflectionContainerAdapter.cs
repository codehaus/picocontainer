using System;
using PicoContainer;

namespace NanoContainer.Reflection {
  /// <summary>
  /// Summary description for IReflectionContainerAdapter.
  /// </summary>
  public interface IReflectionContainerAdapter {
    IComponentAdapter RegisterComponentImplementation(string componentImplementationClassName);

    IComponentAdapter RegisterComponentImplementation(object key, String componentImplementationClassName);

    IComponentAdapter RegisterComponentImplementation(object key,
      string componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString);

    IComponentAdapter RegisterComponentImplementation(String componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString);
  
    IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationClassName);

    IComponentAdapter RegisterComponentImplementation(object key, ObjectTypeSettings componentImplementationClassName);

    IComponentAdapter RegisterComponentImplementation(object key,
      ObjectTypeSettings componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString);

    IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString);

    IMutablePicoContainer PicoContainer {
      get;
    }

  }

}

