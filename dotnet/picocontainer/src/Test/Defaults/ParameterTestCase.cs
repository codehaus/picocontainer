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
using System.Diagnostics;

using NUnit.Framework;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Defaults {
  [TestFixture]
  public class ParameterTestCase {
    public virtual void  testComponentSpecificationHandlesPrimtiveTypes() {
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(typeof(System.Int32), System.Type.GetType("System.Int32")));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Int32"), typeof(System.Int32)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(typeof(string), typeof(string)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Double"), typeof(System.Double)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Int64"), typeof(System.Int64)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Int16"), typeof(System.Int16)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Single"), typeof(System.Single)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Byte"), typeof(System.Byte)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Char"), typeof(System.Char)));
      Assert.IsTrue(TransientComponentAdapter.IsAssignableFrom(System.Type.GetType("System.Boolean"), typeof(System.Boolean)));
      Assert.IsFalse(TransientComponentAdapter.IsAssignableFrom(typeof(System.Int32), typeof(string)));
      Assert.IsFalse(TransientComponentAdapter.IsAssignableFrom(typeof(System.Double), typeof(string)));
    }
		
    internal class TestClass {
      public TestClass(string s1, string s2, string s3) {
      }
    }
		
    public virtual void  testComponentParameterFetches() {
      DefaultPicoContainer pico = new DefaultPicoContainer();
      pico.RegisterComponentImplementation(typeof(Touchable), typeof(SimpleTouchable));
      ComponentParameter parameter = new ComponentParameter(typeof(Touchable));
			
      Assert.IsNotNull(pico.GetComponentInstance(typeof(Touchable)));
      Touchable touchable = (Touchable) parameter.ResolveAdapter(pico).GetComponentInstance(pico);
      Assert.IsNotNull(touchable);
    }
		
    public virtual void  testConstantParameter() {
      object value_Renamed = new object();
      ConstantParameter parameter = new ConstantParameter(value_Renamed);
      Assert.AreEqual(value_Renamed, parameter.ResolveAdapter(null).GetComponentInstance(null));
    }
		
    public virtual void  testDependsOnTouchableWithTouchableSpecifiedAsConstant() {
      DefaultPicoContainer pico = new DefaultPicoContainer();
      SimpleTouchable touchable = new SimpleTouchable();
      pico.RegisterComponentImplementation(typeof(DependsOnTouchable), typeof(DependsOnTouchable), new Parameter[]{new ConstantParameter(touchable)});
      object o = pico.ComponentInstances;
      Assert.IsTrue(touchable._wasTouched);
    }
  }
}