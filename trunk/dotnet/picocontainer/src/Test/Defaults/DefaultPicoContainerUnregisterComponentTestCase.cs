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
using System.Diagnostics;

using csUnit;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Defaults
{
	
  [TestFixture]
  public class DefaultPicoContainerUnRegisterComponentTestCase
  {
    private DefaultPicoContainer picoContainer;
		
    [SetUp]
    public virtual void  setUp()
    {
      picoContainer = new DefaultPicoContainer();
    }
		
    public virtual void  testCannotInstantiateAnUnRegisteredComponent()
    {
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      picoContainer.UnRegisterComponent(typeof(Touchable));
			
      Assert.True(picoContainer.ComponentInstances.Count ==0);
    }
		
    public virtual void  testCanInstantiateReplacedComponent()
    {
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      picoContainer.UnRegisterComponent(typeof(Touchable));
			
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(AlternativeTouchable));
			
      Assert.Equals(1, picoContainer.ComponentInstances.Count,"Container should container 1 component");
    }
		
    public virtual void  testUnRegisterAfterInstantiateComponents()
    {
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      picoContainer.UnRegisterComponent(typeof(Touchable));
      Assert.Null(picoContainer.GetComponentInstance(typeof(Touchable)));
    }
		
    public virtual void  testReplacedInstantiatedComponentHasCorrectClass()
    {
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      picoContainer.UnRegisterComponent(typeof(Touchable));
			
      picoContainer.RegisterComponentImplementation(typeof(Touchable), typeof(AlternativeTouchable));
      object component = picoContainer.ComponentInstances[0];
			
      Assert.Equals(typeof(AlternativeTouchable), component.GetType());
    }
  }
}