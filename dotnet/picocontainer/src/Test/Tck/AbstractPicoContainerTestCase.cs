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
using System.Text;
using System.Reflection;
using System.Collections;
using NUnit.Framework;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;

namespace PicoContainer.Tests.Tck
{
	/// <summary>
	/// Summary description for AbstractPicoContainerTestCase.
	/// </summary>
	public abstract class AbstractPicoContainerTestCase
	{
		protected abstract IMutablePicoContainer CreatePicoContainer(IPicoContainer parent);
		protected abstract IMutablePicoContainer CreatePicoContainer(IPicoContainer parent, ILifecycleManager lifecycleManager);

		protected IMutablePicoContainer CreatePicoContainer()
		{
			return this.CreatePicoContainer(null);
		}

		protected IMutablePicoContainer CreatePicoContainerWithDependsOnTouchableOnly()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation(typeof (DependsOnTouchable));
			return pico;
		}


		protected IMutablePicoContainer CreatePicoContainerWithTouchableAndDependsOnTouchable()
		{
			IMutablePicoContainer pico = CreatePicoContainerWithDependsOnTouchableOnly();
			pico.RegisterComponentImplementation(typeof (Touchable), typeof (SimpleTouchable));
			return pico;
		}

		[Test]
		public void TestNewContainerIsNotNull()
		{
			Assert.IsNotNull(CreatePicoContainerWithTouchableAndDependsOnTouchable());
		}

		[Test]
		public void TestRegisteredComponentsExistAndAreTheCorrectTypes()
		{
			IPicoContainer pico = CreatePicoContainerWithTouchableAndDependsOnTouchable();

			Assert.IsNotNull(pico.GetComponentAdapter(typeof (Touchable)));
			Assert.IsNotNull(pico.GetComponentAdapter(typeof (DependsOnTouchable)));
			Assert.IsTrue(pico.GetComponentInstance(typeof (Touchable)) is Touchable);
			Assert.IsTrue(pico.GetComponentInstance(typeof (DependsOnTouchable)) is DependsOnTouchable);
			Assert.IsNull(pico.GetComponentAdapter(typeof (System.Collections.ICollection)));
		}

		public void testRegistersSingleInstance()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			StringBuilder sb = new StringBuilder();
			pico.RegisterComponentInstance(sb);
			Assert.AreSame(sb, pico.GetComponentInstance(typeof (StringBuilder)));
		}

		/*
		public void testContainerIsSerializable() {
	PicoContainer pico = createPicoContainerWithTouchableAndDependsOnTouchable();

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ObjectOutputStream oos = new ObjectOutputStream(baos);

	oos.writeObject(pico);
	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

	pico = (PicoContainer) ois.readObject();

	DependsOnTouchable dependsOnTouchable = (DependsOnTouchable) pico.getComponentInstance(DependsOnTouchable.class);
																												assertNotNull(dependsOnTouchable);
																												SimpleTouchable touchable = (SimpleTouchable) pico.getComponentInstance(Touchable.class);

																																																	assertTrue(touchable.wasTouched);
			
																																															  }*/
		[Test]
		public void GettingComponentWithMissingDependencyFails()
		{
			IPicoContainer picoContainer = CreatePicoContainerWithDependsOnTouchableOnly();
			try
			{
				picoContainer.GetComponentInstance(typeof (DependsOnTouchable));
				Assert.Fail("should need a Touchable");
			}
			catch (UnsatisfiableDependenciesException e)
			{
				Assert.AreSame(picoContainer.GetComponentAdapterOfType(typeof (DependsOnTouchable)).ComponentImplementation, e.UnsatisfiableComponentAdapter.ComponentImplementation);
				IList unsatisfiableDependencies = e.UnsatisfiableDependencies;
				Assert.AreEqual(1, unsatisfiableDependencies.Count);
				Assert.AreEqual(typeof (Touchable), unsatisfiableDependencies[0]);
			}
		}

		[Test]
		public void DuplicateRegistration()
		{
			try
			{
				IMutablePicoContainer pico = CreatePicoContainer();
				pico.RegisterComponentImplementation(typeof (object));
				pico.RegisterComponentImplementation(typeof (object));
				Assert.Fail("Should have failed with duplicate registration");
			}
			catch (DuplicateComponentKeyRegistrationException e)
			{
				Assert.IsTrue(e.DuplicateKey == typeof (object));
			}
		}

		[Test]
		public void ExternallyInstantiatedObjectsCanBeRegistgeredAndLookUp()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			Hashtable map = new Hashtable();
			pico.RegisterComponentInstance(typeof (Hashtable), map);
			Assert.AreSame(map, pico.GetComponentInstance(typeof (Hashtable)));
		}

		[Test]
		public void AmbiguousResolution()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation("ping", typeof (string));
			pico.RegisterComponentInstance("pong", "pang");
			try
			{
				pico.GetComponentInstance(typeof (string));
			}
			catch (AmbiguousComponentResolutionException e)
			{
				Assert.IsTrue(e.Message.IndexOf("System.String") != -1);
			}
		}

		[Test]
		public void LookupWithUnregisteredKeyReturnsNull()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			Assert.IsNull(pico.GetComponentInstance(typeof (string)));
		}

		public class ListAdder
		{
			public ListAdder(IList list)
			{
				list.Add("something");
			}
		}

		[Test]
		public void UnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation(typeof (ComponentD));

			try
			{
				pico.GetComponentInstance(typeof (ComponentD));
			}
			catch (UnsatisfiableDependenciesException e)
			{
				IList unsatisfiableDependencies = e.UnsatisfiableDependencies;
				Assert.AreEqual(2, unsatisfiableDependencies.Count);
				Assert.IsTrue(unsatisfiableDependencies.Contains(typeof (ComponentE)));
				Assert.IsTrue(unsatisfiableDependencies.Contains(typeof (ComponentB)));

				Assert.IsTrue(e.Message.IndexOf(typeof (ComponentB).Name) != -1);
				Assert.IsTrue(e.Message.IndexOf(typeof (ComponentB).Name) != -1);
			}
		}

		[Test]
		public void CyclicDependencyThrowsCyclicDependencyException()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation(typeof (ComponentB));
			pico.RegisterComponentImplementation(typeof (ComponentD));
			pico.RegisterComponentImplementation(typeof (ComponentE));

			try
			{
				pico.GetComponentInstance(typeof (ComponentD));
				Assert.Fail();
			}
			catch (CyclicDependencyException e)
			{
				ParameterInfo[] parms = typeof (ComponentD).GetConstructors()[0].GetParameters();
				Type[] dDependencies = new Type[parms.Length];
				int x = 0;
				foreach (ParameterInfo p in parms)
				{
					dDependencies[x++] = p.ParameterType;
				}

				Type[] reportedDependencies = e.Dependencies;
				for (x = 0; x < reportedDependencies.Length; x++)
				{
					Assert.AreEqual(dDependencies[x], reportedDependencies[x]);
				}
			}
			catch (Exception)
			{
				Assert.Fail();
			}
		}

		[Test]
		public void RemovalNonRegisteredIComponentAdapterWorksAndReturnsNull()
		{
			IMutablePicoContainer picoContainer = CreatePicoContainer();
			Assert.IsNull(picoContainer.UnregisterComponent("COMPONENT DOES NOT EXIST"));
		}

		[Test]
		public void IComponentAdapterRegistrationOrderIsMaintained()
		{
			ConstructorInjectionComponentAdapter c1 = new ConstructorInjectionComponentAdapter("1", typeof (object));
			ConstructorInjectionComponentAdapter c2 = new ConstructorInjectionComponentAdapter("2", typeof (MyString));

			IMutablePicoContainer picoContainer = CreatePicoContainer();
			picoContainer.RegisterComponent(c1);
			picoContainer.RegisterComponent(c2);
			ArrayList l = new ArrayList();
			l.Add(c1);
			l.Add(c2);

			object[] org = new object[] {c1, c2};
			for (int x = 0; x < 2; x++)
			{
				Assert.AreEqual(org[x], new ArrayList(picoContainer.ComponentAdapters).ToArray()[x]);
			}

			object o = picoContainer.ComponentInstances; // create all the instances at once
			Assert.IsFalse(picoContainer.ComponentInstances[0] is MyString);
			Assert.IsTrue(picoContainer.ComponentInstances[1] is MyString);

			IMutablePicoContainer reversedPicoContainer = CreatePicoContainer();
			reversedPicoContainer.RegisterComponent(c2);
			reversedPicoContainer.RegisterComponent(c1);

			l.Clear();
			l.Add(c2);
			l.Add(c1);
			org = new object[] {c2, c1};
			for (int x = 0; x < 2; x++)
			{
				Assert.AreEqual(org[x], new ArrayList(reversedPicoContainer.ComponentAdapters).ToArray()[x]);
			}

			object o1 = reversedPicoContainer.ComponentInstances; // create all the instances at once
			Assert.IsTrue(reversedPicoContainer.ComponentInstances[0] is MyString);
			Assert.IsFalse(reversedPicoContainer.ComponentInstances[1] is MyString);
		}

		public class MyString
		{
		}

		public class NeedsTouchable
		{
			public Touchable touchable;

			public NeedsTouchable(Touchable touchable)
			{
				this.touchable = touchable;
			}
		}

		public class NeedsWashable
		{
			public Washable washable;

			public NeedsWashable(Washable washable)
			{
				this.washable = washable;
			}
		}

		[Test]
		public void SameInstanceCanBeUsedAsDifferentType()
		{
			IMutablePicoContainer pico = CreatePicoContainer();
			pico.RegisterComponentImplementation("wt", typeof (WashableTouchable));
			pico.RegisterComponentImplementation("nw", typeof (NeedsWashable));
			pico.RegisterComponentImplementation("nt", typeof (NeedsTouchable));

			NeedsWashable nw = (NeedsWashable) pico.GetComponentInstance("nw");
			NeedsTouchable nt = (NeedsTouchable) pico.GetComponentInstance("nt");
			Assert.AreSame(nw.washable, nt.touchable);
		}

		[Test]
		public void RegisterComponentWithObjectBadType()
		{
			IMutablePicoContainer pico = CreatePicoContainer();

			try
			{
				pico.RegisterComponentInstance(typeof (IList), new Object());
				Assert.Fail("Shouldn't be able to register an Object.class as IList because it is not, " +
					"it does not implement it, Object.class does not implement much.");
			}
			catch (AssignabilityRegistrationException)
			{
			}
		}

		public class JMSService
		{
			public readonly String serverid;
			public readonly String path;

			public JMSService(String serverid, String path)
			{
				this.serverid = serverid;
				this.path = path;
			}
		}

		// http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
		[Test]
		public void PicoIssue52()
		{
			IMutablePicoContainer pico = CreatePicoContainer();

			pico.RegisterComponentImplementation("foo", typeof (JMSService), new IParameter[]
				{
					new ConstantParameter("0"),
					new ConstantParameter("something"),
				});
			JMSService jms = (JMSService) pico.GetComponentInstance("foo");
			Assert.AreEqual("0", jms.serverid);
			Assert.AreEqual("something", jms.path);
		}

		public class ComponentA
		{
			public ComponentA(ComponentB b, ComponentC c)
			{
				Assert.IsNotNull(b);
				Assert.IsNotNull(c);
			}
		}

		public class ComponentB
		{
		}

		public class ComponentC
		{
		}

		public class ComponentD
		{
			public ComponentD(ComponentE e, ComponentB b)
			{
				Assert.IsNotNull(e);
				Assert.IsNotNull(b);
			}
		}

		public class ComponentE
		{
			public ComponentE(ComponentD d)
			{
				Assert.IsNotNull(d);
			}
		}

		public class ComponentF
		{
			public ComponentF(ComponentA a)
			{
				Assert.IsNotNull(a);
			}
		}

		[Test]
		public void MakingOfChildContainerPercolatesLifecycleManager()
		{
			TestLifecycleManager lifecycleManager = new TestLifecycleManager();
			IMutablePicoContainer parent = CreatePicoContainer(null, lifecycleManager);
			parent.RegisterComponentImplementation("one", typeof(TestLifecycleComponent));

			IMutablePicoContainer child = parent.MakeChildContainer();
			Assert.IsNotNull(child);
			child.RegisterComponentImplementation("two", typeof(TestLifecycleComponent));

			parent.Start();

			//TODO - The LifecycleManager reference in child containers is not used. Thus is is almost pointless
			// The reason is becuase DefaultPicoContainer's accept() method visits child containerson its own.
			// This may be file for visiting components in a tree for general cases, but for lifecycle, we
			// should hand to each LifecycleManager's start(..) at each appropriate node. See mail-list discussion.

			Assert.AreEqual(2, lifecycleManager.started.Count);
			//assertEquals(1, lifecycleManager.started.size());
		}

		public class TestLifecycleComponent : IStartable
		{
			public bool started;

			public void Start()
			{
				started = true;
			}

			public void Stop()
			{
			}
		}

		public class TestLifecycleManager : DefaultLifecycleManager
		{
			public ArrayList started = new ArrayList();

			public override void Start(IPicoContainer node)
			{
				started.Add(node);
				base.Start(node);
			}
		}
	}
}