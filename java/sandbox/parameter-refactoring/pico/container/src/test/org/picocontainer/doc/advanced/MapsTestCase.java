/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.doc.advanced;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.parameters.ByClass;
import org.picocontainer.parameters.ByKeyClass;
import org.picocontainer.parameters.Filter;

/**
 * @author J&ouml;rg Schaible
 */
public class MapsTestCase extends TestCase implements CollectionDemoClasses {
	private MutablePicoContainer pico;

	protected void setUp() throws Exception {
		pico = new DefaultPicoContainer(new Caching());
	}

	// START SNIPPET: bowl

	public static class Bowl {
		private final TreeMap fishes;

		private final Map cods;

		public Bowl(TreeMap fishes, Map cods) {
			this.fishes = fishes;
			this.cods = cods;
		}

		public Map getFishes() {
			return fishes;
		}

		public Map getCods() {
			return cods;
		}
	}

	// END SNIPPET: bowl

	public void testShouldCreateBowlWithFishCollection() {

		// START SNIPPET: usage

		pico.addComponent("Shark", Shark.class);
		pico.addComponent("Cod", Cod.class);
		pico.addComponent(Bowl.class, Bowl.class,
				new org.picocontainer.parameters.Map(new ByClass(Fish.class),
						TreeMap.class), new org.picocontainer.parameters.Map(
						new ByClass(Cod.class)));
		// END SNIPPET: usage

		Shark shark = pico.getComponent(Shark.class);
		Cod cod = pico.getComponent(Cod.class);
		Bowl bowl = pico.getComponent(Bowl.class);

		Map fishMap = bowl.getFishes();
		assertEquals(2, fishMap.size());
		Collection fishes = fishMap.values();
		assertTrue(fishes.contains(shark));
		assertTrue(fishes.contains(cod));

		Map codMap = bowl.getCods();
		assertEquals(1, codMap.size());
		Collection cods = fishMap.values();
		assertTrue(cods.contains(cod));
	}

	public void testShouldCreateBowlWithNamedFishesOnly() {

		// START SNIPPET: useKeyType

		pico.addComponent(Shark.class);
		pico.addComponent("Nemo", Cod.class);
		pico.addComponent(Bowl.class, Bowl.class,
				new org.picocontainer.parameters.Map(new ByKeyClass(
						String.class), TreeMap.class),
				new org.picocontainer.parameters.Map(new ByClass(Cod.class)));

		Bowl bowl = pico.getComponent(Bowl.class);
		// END SNIPPET: useKeyType

		// START SNIPPET: ensureKeyType

		Cod cod = pico.getComponent(Cod.class);
		Map fishMap = bowl.getFishes();
		Map codMap = bowl.getCods();
		assertEquals(1, fishMap.size());
		assertEquals(1, codMap.size());
		assertEquals(fishMap, codMap);
		assertSame(cod, fishMap.get("Nemo"));
		// END SNIPPET: ensureKeyType
	}

	public void testShouldCreateBowlWithFishesFromParent() {

		// START SNIPPET: scope

		MutablePicoContainer parent = new DefaultPicoContainer();
		parent.addComponent("Tom", Cod.class);
		parent.addComponent("Harry", Cod.class);
		MutablePicoContainer child = new DefaultPicoContainer(parent);
		child.addComponent("Dick", Cod.class);
		child.addComponent(Bowl.class, Bowl.class,
				new org.picocontainer.parameters.Map(new ByClass(Fish.class),
						TreeMap.class), new org.picocontainer.parameters.Map(
						new ByClass(Cod.class)));

		Bowl bowl = child.getComponent(Bowl.class);
		assertEquals(3, bowl.fishes.size());
		assertEquals(3, bowl.cods.size());

		// END SNIPPET: scope
	}

	public void testShouldCreateBowlWith2CodsOnly() {

		// START SNIPPET: scopeOverlay

		MutablePicoContainer parent = new DefaultPicoContainer();
		parent.addComponent("Tom", Cod.class);
		parent.addComponent("Dick", Cod.class);
		parent.addComponent("Harry", Cod.class);
		MutablePicoContainer child = new DefaultPicoContainer(parent);
		child.addComponent("Dick", Shark.class);
		child.addComponent(Bowl.class, Bowl.class,
				new org.picocontainer.parameters.Map(new ByClass(Fish.class),
						TreeMap.class), new org.picocontainer.parameters.Map(
						new ByClass(Cod.class)));

		Bowl bowl = child.getComponent(Bowl.class);
		assertEquals(3, bowl.fishes.size());
		assertEquals(2, bowl.cods.size());

		// END SNIPPET: scopeOverlay
	}

	public void testShouldCreateBowlWithoutTom() {

		// START SNIPPET: individualSelection

		MutablePicoContainer mpc = new DefaultPicoContainer(new Caching());
		mpc.addComponent("Tom", Cod.class);
		mpc.addComponent("Dick", Cod.class);
		mpc.addComponent("Harry", Cod.class);
		mpc.addComponent("Sharky", Shark.class);
		mpc.addComponent(Bowl.class, Bowl.class,
				new org.picocontainer.parameters.Map(new ByClass(Fish.class),TreeMap.class), 
				new org.picocontainer.parameters.Map(new Filter(new ByClass(Cod.class)) 
				{
					public boolean evaluate(ComponentAdapter adapter) {
						return !"Tom".equals(adapter.getComponentKey());
					}
				}));
		Cod tom = (Cod) mpc.getComponent("Tom");
		Bowl bowl = mpc.getComponent(Bowl.class);
		assertTrue(bowl.fishes.values().contains(tom));
		assertFalse(bowl.cods.values().contains(tom));

		// END SNIPPET: individualSelection
	}
}