/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Collections;
using csUnit;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Tck {
  /// <summary>
  /// Summary description for AbstractPicoContainerTestCase.
  /// </summary>
  public abstract class AbstractPicoContainerTestCase {
    protected abstract MutablePicoContainer createPicoContainer();

    protected MutablePicoContainer createPicoContainerWithTouchableAndDependency()  {
      MutablePicoContainer pico = createPicoContainer();

      pico.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
      return pico;
    }

    protected PicoContainer createPicoContainerWithDependsOnTouchableOnly() {
      MutablePicoContainer pico = createPicoContainer();
      pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
      return pico;

    }

    public void testNotNull()  {
      Assert.NotNull(createPicoContainerWithTouchableAndDependency(),"Are you calling super.setUp() in your setUp method?");
    }

    public virtual void testBasicInstantiationAndContainment() {
      PicoContainer pico = createPicoContainerWithTouchableAndDependency();

      Assert.True(pico.HasComponent(typeof(Touchable)), "Container should have Touchable component");
      Assert.True(pico.HasComponent(typeof(DependsOnTouchable)),"Container should have DependsOnTouchable component");
      Assert.True(pico.GetComponentInstance(typeof(Touchable)) is Touchable,"Component should be instance of Touchable");
      Assert.True(pico.GetComponentInstance(typeof(DependsOnTouchable)) is DependsOnTouchable,"Component should be instance of DependsOnTouchable");
      Assert.True(!pico.HasComponent(typeof(Hashtable)),"should not have non existent component");
    }

    public void testInstanceRegistration() {
      MutablePicoContainer pico = createPicoContainer();
      System.Text.StringBuilder sb = new System.Text.StringBuilder();
      pico.RegisterComponentInstance(sb);
      Assert.Equals(sb, pico.GetComponentInstance(typeof(System.Text.StringBuilder)));
    }

    public void testSerializabilityOfContainer() {
    }

    public void testDoubleInstantiation() {
      PicoContainer pico = createPicoContainerWithTouchableAndDependency();
      Assert.Equals(
        pico.GetComponentInstance(typeof(DependsOnTouchable)),
        pico.GetComponentInstance(typeof(DependsOnTouchable))
        );
    }

    public void testDuplicateRegistration() {
      MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
      try {
        pico.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
        Assert.Fail("Should have barfed with dupe registration");
      } catch (DuplicateComponentKeyRegistrationException e) {
        Assert.True(e.DuplicateKey == typeof(Touchable),"Wrong key");
      }
    }

    public void testByInstanceRegistration() {
      MutablePicoContainer pico = createPicoContainerWithTouchableAndDependency();
      pico.RegisterComponentInstance(typeof(Hashtable), new Hashtable());
      Assert.Equals(3, pico.ComponentInstances.Count,"Wrong number of comps in the internals");
      Assert.Equals( typeof(Hashtable), pico.GetComponentInstance(typeof(Hashtable)).GetType(), "Key - Map, Impl - HashMap should be in internals");
    }

    public void testNoResolution()  {
      MutablePicoContainer pico = createPicoContainer();
      Assert.Null(pico.GetComponentInstance(typeof(String)));
    }

    public class ListAdder {
      public ListAdder(ArrayList list) {
        list.Add("something");
      }
    }

    public void TODOtestMulticasterResolution() {
    }
    public class A {
      public A(B b, C c){}
    };
    public class B {};
    public class C {};

    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
      MutablePicoContainer pico = createPicoContainer();
      pico.RegisterComponentImplementation(typeof(A));
      pico.RegisterComponentImplementation(typeof(B));

      try {
        pico.GetComponentInstance(typeof(A));
      } catch (NoSatisfiableConstructorsException e) {
        Assert.Equals( typeof(A).Name + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: " + typeof(C).Name , e.Message );
        ArrayList unsatisfiableDependencies = e.UnsatisfiableDependencies;
        Assert.Equals(1, unsatisfiableDependencies.Count);
        Assert.True(unsatisfiableDependencies.Contains(typeof(C)));
                                                        }
  }

    public class D {
      public D(E e, B b){}
    };
    public class E {
      public E(D d){}
    };

    public void testCyclicDependencyThrowsCyclicDependencyException() {
      MutablePicoContainer pico = createPicoContainer();
      pico.RegisterComponentImplementation(typeof(B));
      pico.RegisterComponentImplementation(typeof(D));
      pico.RegisterComponentImplementation(typeof(E));

      try {
        pico.GetComponentInstance(typeof(D));
        Assert.Fail();
      } catch (CyclicDependencyException e) {
        Assert.Equals("Cyclic dependency: " + typeof(D).GetConstructors()[0].Name + "(" + typeof(E).Name + "," + typeof(B).Name + ")", e.Message);
        Assert.Equals(typeof(D).GetConstructors()[0], e.Constructor);
      } catch (System.StackOverflowException ) {
        Assert.Fail();
      }
    }

    public class NeedsTouchable {
      public Touchable touchable;

      public NeedsTouchable(Touchable touchable) {
        this.touchable = touchable;
      }
    }

    public class NeedsWashable {
      public Washable washable;

      public NeedsWashable(Washable washable) {
        this.washable = washable;
      }
    }

    public void testSameInstanceCanBeUsedAsDifferentType() {
      MutablePicoContainer pico = createPicoContainer();
      pico.RegisterComponentImplementation("aa", typeof(WashableTouchable));
      pico.RegisterComponentImplementation("nw", typeof(NeedsWashable));
      pico.RegisterComponentImplementation("nt", typeof(NeedsTouchable));

      NeedsWashable nw = (NeedsWashable) pico.GetComponentInstance("nw");
      NeedsTouchable nt = (NeedsTouchable) pico.GetComponentInstance("nt");
      Assert.Equals(nw.washable.GetType(), nt.touchable.GetType());
    }

    public void testRegisterComponentWithObjectBadType() {
      MutablePicoContainer pico = createPicoContainer();

      try {
        pico.RegisterComponentInstance(typeof(ICollection), new Object());
        Assert.Fail("Shouldn't be able to Register an Object.class as Serializable because it is not, " +
          "it does not implement it, Object.class does not implement much.");
      } catch (AssignabilityRegistrationException ) {
      }

    }

    public class JMSService {
      public String serverid;
      public String path;

      public JMSService(String serverid, String path) {
        this.serverid = serverid;
        this.path = path;
      }
    }
    // http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
    public void testPico52() {
      MutablePicoContainer pico = createPicoContainer();

      pico.RegisterComponentImplementation("foo", typeof(JMSService), new Parameter[] {
                                                               new ConstantParameter("0"),
                                                               new ConstantParameter("something"),
                                                             });
    JMSService jms = (JMSService) pico.GetComponentInstance("foo");
    Assert.Equals("0", jms.serverid);
    Assert.Equals("something", jms.path);
  }


}

}
