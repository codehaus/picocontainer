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
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests
{
	
  [TestFixture]
	public class NoPicoTestCase
	{
		
	 	public virtual void  testTouchableWithoutPicoTestCase()
		{
			
			SimpleTouchable touchable = new SimpleTouchable();
			new DependsOnTouchable(touchable);
			
			Assert.True(touchable._wasTouched,"Touchable should have had its wasTouched method called");
		}
	}
}