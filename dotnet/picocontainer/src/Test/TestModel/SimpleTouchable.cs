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

namespace PicoContainer.Tests.TestModel
{
	/// Summary description for SimpleTouchable.
	/// </summary>
	public class SimpleTouchable : Touchable
	{
		public SimpleTouchable()
		{
			//
			// TODO: Add constructor logic here
			//
		}

		#region Touchable Members

		public bool wasTouched;

		public void touch()
		{
			wasTouched = true;
		}

		#endregion
	}
}