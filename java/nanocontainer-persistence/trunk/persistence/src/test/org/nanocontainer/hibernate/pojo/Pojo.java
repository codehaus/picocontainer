/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.hibernate.pojo;

/**
 * just a pojo to make hibernate happy. 
 * @author Konstantin Pribluda
 * @version $Revision$
 */

public class Pojo {
	
	Integer id;
	String foo;
	
	public Pojo() {
	}
	
	public Integer getId() {
		return id;
	}
	
	
	public String getFoo() {
		return foo;
	}
	
	public void setFoo(String foo) {
		this.foo = foo;
	}
}