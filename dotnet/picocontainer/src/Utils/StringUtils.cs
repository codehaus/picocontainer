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

namespace PicoContainer.Utils
{

	public class StringUtils
	{
    public static string ArrayToString(object [] array) {
      String retval = "";

      for (int i = 0; i < array.Length; i++) {
        retval = retval + array[i].ToString();
        if (i+1 < array.Length) {
          retval += ",";
        }
      }
      return retval;

    }
	}
}
