/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 * C# port by Maarten Grootendorst                                           *
 *****************************************************************************/

using System;
using System.Threading;
using System.Collections;
using csUnit;

using PicoContainer.Extras;
using PicoContainer.Lifecycle;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;
using PicoContainer.Tests.Tck;
namespace PicoContainer.Tests.Extras
{

	/// Summary description for InvokingComponentAdapterFactoryTestCase.
	/// </summary>
	public class InvokingComponentAdapterFactoryTestCase : AbstractComponentAdapterFactoryTestCase {

    protected override ComponentAdapterFactory CreateComponentAdapterFactory() {
        return new InvokingComponentAdapterFactory(new DefaultComponentAdapterFactory(), "setMessage", new Type[]{typeof(String)}, new String[]{"hello"});
    }

    private ComponentAdapter createAdapterCallingSetMessage(Type impl) {
        InvokingComponentAdapterFactory.Adapter adapter =
                (InvokingComponentAdapterFactory.Adapter) CreateComponentAdapterFactory().CreateComponentAdapter("whatever", impl, null);
        return adapter;
    }

    public class Foo {
        public String message;

        public String setMessage(String message) {
            this.message = message;
            return message + " world";
        }
    }

    public class Failing {
        public void setMessage(String message) {
            throw new IndexOutOfRangeException();
        }
    }

    public class NoSetMessage {
    }

    public void testSuccessfulMethod() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(typeof(Foo));
        Foo foo = (Foo) adapter.GetComponentInstance(picoContainer);
        Assert.NotNull(foo);
        Assert.Equals("hello", foo.message);

        Assert.Equals("hello world", ((InvokingComponentAdapterFactory.Adapter)adapter).InvocationResult);
    }

    public void testFailingInvocation() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(typeof(Failing));
        try {
            adapter.GetComponentInstance(picoContainer);
            Assert.Fail();
        } catch (Exception ) {
        }
    }

    public void testNoInvocation() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(typeof(NoSetMessage));
        NoSetMessage noSetMessage = (NoSetMessage) adapter.GetComponentInstance(picoContainer);
        Assert.NotNull(noSetMessage);
    }
}

}
