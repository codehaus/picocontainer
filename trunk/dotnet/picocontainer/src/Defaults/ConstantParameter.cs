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

namespace PicoContainer.Defaults
{
	/**
 *
 * @author Jon Tirs&eacute;n
 * @author Aslak Helles&oslash;y
 * @author J&ouml;rg Schaible
 * @version $Revision$
 */

	/// <summary>
	/// A ConstantParameter should be used to pass in "constant" arguments
	/// to constructors. 
	/// <remarks>This includes Strings, Integers or
	/// any other object that is not registered in the container.</remarks>
	/// </summary>
	[Serializable]
	public class ConstantParameter : IParameter
	{
		private readonly object constantValue;

		/// <summary>
		/// Constructor
		/// </summary>
		/// <param name="constantValue">the value</param>
		public ConstantParameter(object constantValue)
		{
			this.constantValue = constantValue;
		}

		/// <summary>
		/// Get a component for the parameter.
		/// </summary>
		/// <param name="picoContainer">-</param>
		/// <param name="expectedType">-</param>
		/// <returns>The component adapter</returns>
		public IComponentAdapter ResolveAdapter(IPicoContainer picoContainer, Type expectedType)
		{
			return new InstanceComponentAdapter(constantValue, constantValue);
		}
	}
}