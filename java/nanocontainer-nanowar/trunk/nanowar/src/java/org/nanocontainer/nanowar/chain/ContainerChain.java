/*
 * Created on May 21, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.nanocontainer.nanowar.chain;

import java.util.ArrayList;
import java.util.Iterator;

import org.picocontainer.PicoContainer;
import org.picocontainer.Startable;

/**
 * represents chain of containers. it can be started an dstopped at once. 
 * 
 * @author Konstantin Pribluda
 */
public class ContainerChain implements Startable {

	ArrayList chain = new ArrayList();

	PicoContainer last = null;

	/**
	 * get hold on last container in chain.
	 * 
	 * @return last container in chain or null
	 */
	public PicoContainer getLast() {
		return last;
	}

	/**
	 * add new container to the end of chain.
	 * 
	 * @param container
	 */
	public void addContainer(PicoContainer container) {
		chain.add(container);
		last = container;
	}

	/**
	 * start chain
	 */
	public void start() {
		for (Iterator iter = chain.iterator(); iter.hasNext();) {
			((Startable) iter.next()).start();
		}
	}

	/**
	 * stop chain
	 */
	public void stop() {
		for (int i = chain.size() - 1; i >= 0; i--) {
			((Startable) chain.get(i)).stop();
		}
	}

}