using System;

using PicoContainer;
using PicoContainer.Defaults;
using NUnit.Framework;

namespace Test.Defaults
{

  [TestFixture]
  public class ChildContainerTestCase 
  {
    public abstract class Xxx : IStartable, IDisposable 
    {

      public static String componentRecorder = "";

      public void Start() 
      {
        componentRecorder += "<" + code();
      }

      public void Stop() 
      {
        componentRecorder += code() + ">";
      }

      public void Dispose() 
      {
        componentRecorder += "!" + code();
      }

      private String code() 
      {
        String name = this.GetType().Name;
        return name.Substring(name.IndexOf('$')+1,name.Length);
      }

      public class A : Xxx {}
      public class B : Xxx 
      {
        public B(A a) 
        {
          Assert.IsNotNull(a);
        }
      }
      public class C : Xxx {}
    }


    public void testFOO() {}

  }
}
