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
using System.Collections;

using NUnit.Framework;

using PicoContainer;
using PicoContainer.Test.TestModel;

namespace PicoContainer.Test
{
	[TestFixture]
	public class ReflectionUsingLifecycleManagerTestCase : Assertion 
	{
		class Startable 
		{
			public ArrayList messages = new ArrayList();
			public void Start() 
			{
				messages.Add("started");
			}
			public void Stop() 
			{
				messages.Add("stopped");
			}
		}
		
		[Test]
		public void TestStartInvocation() 
		{
			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			Startable startable = new Startable();

			try 
			{
				lm.StartComponent(startable);
			} 
			catch (PicoStartException) 
			{
				Fail("Should have started just fine");
			}

			AssertEquals(1, startable.messages.Count);
			AssertEquals("started", startable.messages[0]);
		}

		class StartableThrowsException 
		{
			public void Start() 
			{
				throw new ArgumentException();
			}

			public void Stop() 
			{
				throw new ArgumentException();
			}
		}
	
		[Test]
		public void TestFailingStartInvocation() 
		{

			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			try 
			{
				lm.StartComponent(new StartableThrowsException());
				Fail("Should have barfed");
			} 
			catch (PicoInvocationTargetStartException e) 
			{
				AssertEquals(typeof(ArgumentException), e.InnerException.GetType());
				// expected
			}
		}

		[Test]
		public void TestStopInvocation() 
		{
			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			Startable startable = new Startable();
		
			try 
			{
				lm.StopComponent(startable);
			} 
			catch (PicoStopException) 
			{
				Fail("Should have stopped just fine");
			}

			AssertEquals(1, startable.messages.Count);
			AssertEquals("stopped", startable.messages[0]);
		}

		[Test]
		public void TestFailingStopInvocation() 
		{

			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			StartableThrowsException startable = new StartableThrowsException();

			try 
			{
				lm.StopComponent(startable);
				Fail("Should have barfed");
			} 
			catch (PicoInvocationTargetStopException e) 
			{
				AssertEquals(typeof(ArgumentException), e.InnerException.GetType());
				AssertEquals(e.InnerException.Message, e.Message);
				// expected
			}
		}

		[Test]
		public void TestPrivateStartInvocation() 
		{

			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			ArrayList messages = new ArrayList();

			lm.StartComponent(new UnaccessibleStartComponent(messages));
			AssertEquals("Should not have started", 0, messages.Count);

		}

		class StartableWithProtectedStop
		{
			public ArrayList messages = new ArrayList();
			protected void startable() 
			{
				messages.Add("stopped");
			}
		}

		[Test]
		public void TestNonPublicStopInvocation() 
		{

			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			StartableWithProtectedStop startable = new StartableWithProtectedStop();

			lm.StopComponent(startable);
			AssertEquals(0, startable.messages.Count);

		}

		[Test]
		public void TestNoStartOrStopInvocation() 
		{
			ReflectionUsingLifecycleManager lm = new ReflectionUsingLifecycleManager();

			try 
			{
				lm.StartComponent(new object());
				lm.StopComponent(new object());
			} 
			catch (PicoStartException) 
			{
				Fail("Should have started just fine");
			} 
			catch (PicoStopException) 
			{
				Fail("Should have stopped just fine");
			}
		}

		[Test]
		public void TestInvocationTargetBasics() 
		{
			try 
			{
				new PicoInvocationTargetStartException(null);
			} 
			catch (ArgumentException)
			{
				// expected
			}
			try 
			{
				new PicoInvocationTargetStopException(null);
			} 
			catch (ArgumentException)
			{
				// expected
			}

		}
	}
}

