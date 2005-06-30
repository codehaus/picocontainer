package org.nanocontainer.nanowar.nanoweb.impl;

import java.util.Iterator;

import org.nanocontainer.nanowar.nanoweb.Converter;
import org.nanocontainer.nanowar.nanoweb.ConverterComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;

public class Helper {

	public static Converter getConverterFor(Class type, PicoContainer in) {
		// Try to get using Visitor, visitor visit only childs container, not parents.
		GetConverterVisitor gcv = new GetConverterVisitor(type);
		in.accept(gcv);
		if (gcv.getConverterCA() != null) {
			return (Converter) gcv.getConverterCA().getComponentInstance(in);
		}

		// If it could not get using Visitor
		return tryToGetConverterUsingGetComponentAdaptersOfType(type, in.getParent());
	}

	private static Converter tryToGetConverterUsingGetComponentAdaptersOfType(Class type, PicoContainer in) {
		if (in == null) {
			return null;
		}

		Iterator i = in.getComponentAdapters().iterator();
		while (i.hasNext()) {
			ComponentAdapter ca = (ComponentAdapter) i.next();
			if (ca instanceof ConverterComponentAdapter) {
				ConverterComponentAdapter cca = (ConverterComponentAdapter) ca;
				if (cca.getType().isAssignableFrom(type)) {
					return (Converter) cca.getComponentInstance(in);
				}
			}
		}

		return tryToGetConverterUsingGetComponentAdaptersOfType(type, in.getParent());
	}

}
