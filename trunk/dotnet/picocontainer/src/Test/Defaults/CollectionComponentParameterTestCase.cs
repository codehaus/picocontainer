using System;
using System.Collections;
using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;

namespace Test.Defaults
{
	[TestFixture]
	public class CollectionComponentParameterTestCase
	{
		[Test]
		public void WillRemoveComponentsWithMatchingKeyFromParent() 
		{
			IMutablePicoContainer parent = new DefaultPicoContainer();
			parent.RegisterComponentImplementation("Tom", typeof(Cod));
			parent.RegisterComponentImplementation("Dick", typeof(Cod));
			parent.RegisterComponentImplementation("Harry", typeof(Cod));
			
			IMutablePicoContainer child = new DefaultPicoContainer(parent);
			child.RegisterComponentImplementation("Dick", typeof(Shark));
			child.RegisterComponentImplementation(typeof(Bowl));
			Bowl bowl = (Bowl) child.GetComponentInstance(typeof(Bowl));
			Assert.AreEqual(3, bowl.fishes.Length);
			Assert.AreEqual(2, bowl.cods.Length);
		}

		[Test]
		public void BowlWithoutTom()
		{
			IMutablePicoContainer mpc = new DefaultPicoContainer();
			mpc.RegisterComponentImplementation("Tom", typeof (Cod));
			mpc.RegisterComponentImplementation("Dick", typeof (Cod));
			mpc.RegisterComponentImplementation("Harry", typeof (Cod));
			mpc.RegisterComponentImplementation(typeof (Shark));

			IParameter[] parameters = new IParameter[]
				{
					new SampleCollectionComponentParameter(typeof (Cod), false),
					new CollectionComponentParameter(typeof (Fish), false)
				};

			mpc.RegisterComponentImplementation(typeof (CollectedBowl), typeof (CollectedBowl), parameters);

			CollectedBowl bowl = (CollectedBowl) mpc.GetComponentInstance(typeof (CollectedBowl));
			Cod tom = (Cod) mpc.GetComponentInstance("Tom");
			Assert.AreEqual(4, bowl.fishes.Count);
			Assert.AreEqual(2, bowl.cods.Count);
			Assert.IsFalse(new ArrayList(bowl.cods).Contains(tom));
		}

		/*
		[Test]
		public void DifferentCollectiveTypesAreResolved() 
		{
			IMutablePicoContainer pico = new DefaultPicoContainer();
			CollectionComponentParameter parameter = new CollectionComponentParameter(typeof(Fish), true);
			IParameter[] parameters = new IParameter[] {parameter, parameter, parameter, parameter, parameter, parameter };

			pico.RegisterComponentImplementation(typeof(DependsOnAll), typeof(DependsOnAll), parameters);
			Assert.IsNotNull(pico.GetComponentInstance(typeof(DependsOnAll)));
		}*/

		[Test]
		public void Verify()
		{
			IMutablePicoContainer pico = new DefaultPicoContainer();
			CollectionComponentParameter parameterNonEmpty = CollectionComponentParameter.ARRAY;
			pico.RegisterComponentImplementation(typeof (Shark));
			parameterNonEmpty.Verify(pico, null, typeof (Fish[]));

			try
			{
				parameterNonEmpty.Verify(pico, null, typeof (Cod[]));
				Assert.Fail("PicoIntrospectionException expected");
			}
			catch (PicoIntrospectionException e)
			{
				Assert.IsTrue(e.Message.IndexOf(typeof (Cod).Name) > -1);
			}

			CollectionComponentParameter parameterEmpty = CollectionComponentParameter.ARRAY_ALLOW_EMPTY;
			parameterEmpty.Verify(pico, null, typeof (Fish[]));
			parameterEmpty.Verify(pico, null, typeof (Cod[]));
		}

	}

	#region Test Classes

	public interface Fish
	{
	}

	public class Cod : Fish
	{
		public override String ToString()
		{
			return "Cod";
		}
	}

	public class Shark : Fish
	{
		public override String ToString()
		{
			return "Shark";
		}
	}

	public class Bowl
	{
		public Cod[] cods;
		public Fish[] fishes;

		public Bowl(Cod[] cods, Fish[] fishes)
		{
			this.cods = cods;
			this.fishes = fishes;
		}
	}

	public class CollectedBowl 
	{
		public ICollection cods;
		public ICollection fishes;

		public CollectedBowl(ICollection cods, ICollection fishes) 
		{
			this.cods = cods;
			this.fishes = fishes;
		}
	}

	/// <summary>
	/// This would be so much easier with anonymous classes!
	/// </summary>
	public class SampleCollectionComponentParameter : CollectionComponentParameter
	{
		public SampleCollectionComponentParameter(Type componentValueType, bool emptyCollection) 
			: base(componentValueType, emptyCollection)
		{
		}

		protected override bool Evaluate(IComponentAdapter componentAdapter)
		{
			return !"Tom".Equals(componentAdapter.ComponentKey);	
		}
	}

	#endregion
}
