/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Threading;
using System.Collections;
using csUnit;

using PicoContainer.Extras;
using PicoContainer.Lifecycle;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;


namespace PicoContainer.Tests.Extras
{

	/// Summary description for DelegatingPicoContainerTestCase.
	/// </summary>
	[TestFixture]
	public class DelegatingPicoContainerTestCase
	{
    private MutablePicoContainer parent;
    private DefaultPicoContainer child;

    public void SetUp() {
      parent = new DefaultPicoContainer();
      child = new DefaultPicoContainer();
      child.AddParent(parent);
    }

    public void testChildGetsFromParent() {
      parent.RegisterComponentImplementation(typeof(SimpleTouchable));
      child.RegisterComponentImplementation(typeof(DependsOnTouchable));
      DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) child.GetComponentInstance(typeof(DependsOnTouchable));

      Assert.NotNull(dependsOnTouchable);
    }

    public void testParentDoesntGetFromChild() {
      child.RegisterComponentImplementation(typeof(SimpleTouchable));
      parent.RegisterComponentImplementation(typeof(DependsOnTouchable));
      try {
        DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) parent.GetComponentInstance(typeof(DependsOnTouchable));
        Assert.Fail();
      } catch (NoSatisfiableConstructorsException) {
      }
    }

    public void testChildOverridesParent() {
      parent.RegisterComponentImplementation(typeof(SimpleTouchable));
      child.RegisterComponentImplementation(typeof(SimpleTouchable));

      SimpleTouchable parentTouchable = (SimpleTouchable) parent.GetComponentInstance(typeof(SimpleTouchable));
      SimpleTouchable childTouchable = (SimpleTouchable) child.GetComponentInstance(typeof(SimpleTouchable));
      Assert.Equals(1, child.ComponentInstances.Count);
      Assert.True(parentTouchable!=childTouchable);
    }

    public void testMulticaster()  {
      parent.RegisterComponentImplementation(typeof(SimpleTouchable));
      child.RegisterComponentImplementation(typeof(DependsOnTouchable));

      Object multicaster = child.GetComponentMulticaster();
      Assert.True(multicaster is Touchable);
    }
  }
}
