using System;
using PicoContainer;
using System.Reflection;
using PicoContainer.Defaults;

namespace NanoContainer.Reflection {

  public class DefaultReflectionContainerAdapter : IReflectionContainerAdapter{
    private readonly IMutablePicoContainer picoContainer;

    public DefaultReflectionContainerAdapter() : this (new DefaultPicoContainer()){
    }

    public DefaultReflectionContainerAdapter(IMutablePicoContainer picoContainer) {
      this.picoContainer = picoContainer;
    }

    public IComponentAdapter RegisterComponentImplementation(string componentImplementationClassName) {
      return this.RegisterComponentImplementation(new ObjectTypeSettings(componentImplementationClassName));
    }

    public IComponentAdapter RegisterComponentImplementation(object key, String componentImplementationClassName) {
      return this.RegisterComponentImplementation(key,new ObjectTypeSettings(componentImplementationClassName));
    }

    public IComponentAdapter RegisterComponentImplementation(String componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString) {
      return this.RegisterComponentImplementation(new ObjectTypeSettings(componentImplementationClassName), parameterTypesAsString,parameterValuesAsString);
    }

    public IComponentAdapter RegisterComponentImplementation(object key,
      string componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString) {
      return this.RegisterComponentImplementation(key,new ObjectTypeSettings(componentImplementationClassName), parameterTypesAsString,parameterValuesAsString);
    }

    public IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings typeSettings) {
      return picoContainer.RegisterComponentImplementation(TypeLoader.GetType(typeSettings));
    }

    public IComponentAdapter RegisterComponentImplementation(object key, ObjectTypeSettings componentImplementationClassName) {
      return picoContainer.RegisterComponentImplementation(key,TypeLoader.GetType(componentImplementationClassName));
    }

    public IComponentAdapter RegisterComponentImplementation(object key,
      ObjectTypeSettings componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString) {

      Type componentImplementation = TypeLoader.GetType(componentImplementationClassName);
      
      return RegisterComponentImplementation(parameterTypesAsString, parameterValuesAsString, key, componentImplementation);
    }

    public IComponentAdapter RegisterComponentImplementation(ObjectTypeSettings componentImplementationClassName,
      string[] parameterTypesAsString,
      string[] parameterValuesAsString) {
      Type type = TypeLoader.GetType(componentImplementationClassName);
      return RegisterComponentImplementation(parameterTypesAsString,parameterValuesAsString,type,type);      
    }


    private IComponentAdapter RegisterComponentImplementation(String[] parameterTypesAsString, String[] parameterValuesAsString, Object key, Type componentImplementation) {
      IParameter[] parameters = new IParameter[parameterTypesAsString.Length];
      for (int i = 0; i < parameters.Length; i++) {
        Type paramTypeClass = TypeLoader.GetType(parameterTypesAsString[i]);
        object value = Convert.ChangeType(parameterValuesAsString[i],paramTypeClass);
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
