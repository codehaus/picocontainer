using System;
using System.Collections;
using System.Reflection;
using System.IO;
namespace PicoContainer.Defaults {
  public class BeanPropertyComponentAdapter : DecoratingComponentAdapter {
    private IDictionary properties;
    private PropertyInfo[] propertyDescriptors;
    private IDictionary propertyDescriptorMap = new Hashtable();

    /// <summary>
    /// Constructor
    /// </summary>
    /// <param name="theDelegate">The component adapter to decorate</param>
    public BeanPropertyComponentAdapter(IComponentAdapter theDelegate) : base(theDelegate) {
      Type implementation = theDelegate.ComponentImplementation;
      propertyDescriptors = implementation.GetProperties();

      for(int i = 0;i < propertyDescriptors.Length;i++) {
        PropertyInfo info = propertyDescriptors[i];
        propertyDescriptorMap.Add(info.Name,info);
      }
    }


    /// <summary>
    /// Returns the component's implementing type.
    /// <remarks>Initializing additional properties using the properties set using the Properties property</remarks>
    /// </summary>
    public override object ComponentInstance {
      get {
        object componentInstance = base.ComponentInstance;

        if (properties != null) {
          ICollection propertyNames = properties.Keys;

          foreach (string propertyName in propertyNames) {
            object propertyValue = properties[propertyName];
            PropertyInfo propertyInfo = (PropertyInfo) propertyDescriptorMap[propertyName];
            if (propertyInfo == null) {
              throw new PicoIntrospectionException("Unknown property '" + propertyName + "' in type " + componentInstance.GetType().Name);
            }
            MethodInfo setter = propertyInfo.GetSetMethod();
            if (setter == null) {
              throw new PicoInitializationException("There is no public setter method for property " + propertyName + " in " + componentInstance.GetType().Name +
                ". Setter: " + propertyInfo.GetSetMethod() +
                ". Getter: " + propertyInfo.GetGetMethod());
            }
            try {
              setter.Invoke(componentInstance, new object[]{convertType(setter, propertyValue)});
            } catch (Exception e) {
              throw new PicoInitializationException("Failed to set property " + propertyName + " to " + propertyValue + ": " + e.Message, e);
            }
          }
        }
        return componentInstance;
      }
    }

    private object convertType(MethodInfo setter, Object propertyValue)  {
      if (propertyValue == null) {
        return null;
      }
      Type type = setter.GetParameters()[0].ParameterType;
      if (type == typeof(bool)) {
        return bool.Parse(propertyValue.ToString());
      } else if (type == typeof(byte)) {
        return byte.Parse(propertyValue.ToString());
      } else if (type == typeof(short)) {
        return short.Parse(propertyValue.ToString());
      } else if (type == typeof(int)) {
        return int.Parse(propertyValue.ToString());
      } else if (type == typeof(long)) {
        return long.Parse(propertyValue.ToString());
      } else if (type == typeof(float)) {
        return float.Parse(propertyValue.ToString());
      } else if (type == typeof(double)) {
        return double.Parse(propertyValue.ToString());
      } else if (type == typeof(char)) {
        return propertyValue.ToString()[0];
      } else {

        // check if the propertyValue is a key of a component in the container
        // if so, the type of the component and the setters parameter type
        // have to be compatible
        if (Container != null) {
          object component = Container.GetComponentInstance(propertyValue);
          if (component != null && type.IsAssignableFrom(component.GetType())) {
            return component;
          }
        }
      }
      return propertyValue;
    }

    /// <summary>
    /// Setter for additional properties not set via PicoContainer
    /// </summary>
    public IDictionary Properties {
      set {
        this.properties = value;
      }
    }

  }
}

