package org.picocontainer.gems.jndi;

import java.io.IOException;
import java.io.Serializable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.picocontainer.ObjectReference;
import org.picocontainer.PicoCompositionException;

/**
 * object reference to store and retrieve objects from JNDI
 * 
 * @author ko5tik
 * 
 */
public class JNDIObjectReference<T> implements ObjectReference<T> , Serializable{

	String name;

	transient Context context;

	public JNDIObjectReference(String name, Context ctx) {
		super();
		this.name = name;
		this.context = ctx;
	}

	public JNDIObjectReference(String jndiName) throws NamingException {
		this(jndiName,new InitialContext());
	}

	/**
	 * retrieve object from JNDI if possible
	 */
	public T get() {
		try {
			return (T) context.lookup(name);
		} catch(NameNotFoundException e) {
			// this is not error, but normal situation - nothing
			// was stored yet
			return null;
		} catch (NamingException e) {
			throw new PicoCompositionException("unable to resolve jndi name:"
					+ name, e);
		}
	}

	/**
	 * store object in JNDI under specified name
	 */
	public void set(T item) {
		try {
			if (item == null) {
				context.unbind(name);
			} else {

				Context ctx = context;
				Name n = ctx.getNameParser("").parse(name);
				while (n.size() > 1) {
					String ctxName = n.get(0);
					try {
						ctx = (Context) ctx.lookup(ctxName);
					} catch (NameNotFoundException e) {
						ctx = ctx.createSubcontext(ctxName);
					}
					n = n.getSuffix(1);
				}
				// unbind name just in case
				try {
					if (ctx.lookup(n) != null) {
						ctx.unbind(n);
					}
				} catch (NameNotFoundException e) {
					// that's ok
				}
				ctx.bind(n, item);
			}
		} catch (NamingException e) {
			throw new PicoCompositionException("unable to bind to  jndi name:"
					+ name, e);
		}
	}

	/**
	 * name of this reference
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * here we try to capture (eventual) deserealisation of this reference by
	 * some container (notably JBoss)  and restore context as initial context
	 * I hope this will be sufficient for most puproses
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in)throws IOException, ClassNotFoundException {
		try {
			context = new InitialContext();
		} catch (NamingException e) {
			throw new IOException("unable to create initial context");
		}
		in.defaultReadObject();
	}
}
