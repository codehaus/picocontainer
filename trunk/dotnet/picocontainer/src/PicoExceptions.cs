/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * Ported to .NET by Jeremey Stell-Smith                                     *
 *****************************************************************************/



using System;

namespace PicoContainer
{
	public abstract class PicoRegistrationException : Exception
	{
		public PicoRegistrationException(string message) : base(message) {}
		public PicoRegistrationException(string message, Exception cause) : base(message, cause) {}
	}

	public abstract class PicoStartException : Exception 
	{
		public PicoStartException(string message) : base(message) {}
		public PicoStartException(string message, Exception cause) : base(message, cause) {}
	}

	public abstract class PicoStopException : Exception 
	{
		public PicoStopException(string message) : base(message) {}
		public PicoStopException(string message, Exception cause) : base(message, cause) {}
	}

	public class PicoDisposalException : Exception 
	{
		public PicoDisposalException(string message) : base(message) {}
		public PicoDisposalException(string message, Exception cause) : base(message, cause) {}
	}

	public class PicoInvocationTargetStopException : PicoStopException 
	{
		public PicoInvocationTargetStopException(Exception cause) : base (cause != null ? cause.Message : "", cause) {}
	}

	public class PicoInvocationTargetDisposalException : PicoDisposalException 
	{
		public PicoInvocationTargetDisposalException(Exception cause) : base(cause != null ? cause.Message : "", cause) {}
	}

	public class PicoInvocationTargetStartException : PicoStartException 
	{
		public PicoInvocationTargetStartException(Exception cause) : base(cause != null ? cause.Message : "", cause) {} 
	}

	public class UnsatisfiedDependencyStartupException : PicoStartException
	{
		public readonly Type ClassThatNeedsDeps;

		public UnsatisfiedDependencyStartupException(Type classThatNeedsDeps) 
			: base("Type " + classThatNeedsDeps.Name + " needs unnamed depenencies") 
		{
			this.ClassThatNeedsDeps = classThatNeedsDeps;
		}
	}

	public class WrongNumberOfConstructorsRegistrationException : PicoRegistrationException 
	{
		public WrongNumberOfConstructorsRegistrationException(int numberOfConstructors) 
			: base("Wrong Number of Constructors for Pico Component. Expected 1, found" + numberOfConstructors) {}
	}

	public class DuplicateComponentTypeRegistrationException : PicoRegistrationException
	{
		public readonly Type DuplicateClass;

		public DuplicateComponentTypeRegistrationException(Type type) : base("Type " + type.Name + " duplicated")
		{
			this.DuplicateClass = type;
		}
	}

	public class CircularDependencyRegistrationException : PicoRegistrationException 
	{
		public CircularDependencyRegistrationException() : base("") {}
	}

	public class NotConcreteRegistrationException : PicoRegistrationException 
	{
		public readonly Type ComponentImplementation;

		public NotConcreteRegistrationException(Type componentImplementation) 
			: base("Bad Access: '" + componentImplementation.Name + "' is not instansiable")
		{
			this.ComponentImplementation = componentImplementation;
		}
	}

	public class AssignabilityRegistrationException : PicoRegistrationException
	{
		public AssignabilityRegistrationException(Type a, Type b)
			: base("The type:" + a.Name + "  was not assignable from typeof(the) " + b.Name) {}
	}

	public class AmbiguousComponentResolutionException : PicoStartException 
	{
		public readonly Type[] AmbiguousClasses;

		public AmbiguousComponentResolutionException(Type[] ambiguousClass) : base("")
		{
			this.AmbiguousClasses = ambiguousClass;
		}

		public override string Message 
		{
			get 
			{
				string msg = "Ambiguous Classes:";
				foreach (Type type in AmbiguousClasses)
				{
					msg += " " + type.Name;
				}
				return msg;
			}
		}
	}

	public class PicoNullReferenceException : NullReferenceException 
	{
		public PicoNullReferenceException(string message) : base(message) {}

		public static void AssertNotNull(string message, object obj) 
		{
			if (obj == null) throw new PicoNullReferenceException(message);
		}
	}
}
