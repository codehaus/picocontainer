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

namespace PicoContainer.Tests.Extras
{

	/// Summary description for DefaultLifecyclePicoAdaptorTestCase.
	/// </summary>
  [TestFixture]
  public class DefaultLifecyclePicoAdaptorTestCase
	{


    public class One : Startable, Stoppable, IDisposable {

        ArrayList instantiating = new ArrayList();
        ArrayList Starting = new ArrayList();
        ArrayList Stopping = new ArrayList();
        ArrayList disposing = new ArrayList();

        public One() {
            instantiation("One");
        }

        public void instantiation(String s) {
            instantiating.Add(s);
        }

        public ArrayList getInstantiating() {
            return instantiating;
        }

        public ArrayList getStarting() {
            return Starting;
        }

        public ArrayList getStopping() {
            return Stopping;
        }

        public ArrayList getDisposing() {
            return disposing;
        }

        public void Start() {
            StartCalled("One");
        }

        public void Stop() {
            StopCalled("One");
        }

        public void Dispose() {
            disposeCalled("One");
        }

        public void StartCalled(String msg) {
            Starting.Add(msg);
        }

        public void StopCalled(String msg) {
            Stopping.Add(msg);
        }

        public void disposeCalled(String msg) {
            disposing.Add(msg);
        }

    }

    public class Two : Startable, Stoppable,IDisposable {
        One one;

        public Two(One one) {
            one.instantiation("Two");
            this.one = one;
        }

        public void Start() {
            one.StartCalled("Two");
        }

        public void Stop() {
            one.StopCalled("Two");
        }

        public void Dispose() {
            one.disposeCalled("Two");
        }
    }

    public class Three : Startable, Stoppable, IDisposable {
        One one;

        public Three(One one, Two two) {
            one.instantiation("Three");
            this.one = one;
        }

        public void Start() {
            one.StartCalled("Three");
        }

        public void Stop() {
            one.StopCalled("Three");
        }

        public void Dispose() {
            one.disposeCalled("Three");
        }
    }

    public class Four : Startable, Stoppable, IDisposable {
        One one;

        public Four(Two two, Three three, One one) {
            one.instantiation("Four");
            this.one = one;
        }

        public void Start() {
            one.StartCalled("Four");
        }

        public void Stop() {
            one.StopCalled("Four");
        }

        public void Dispose() {
            one.disposeCalled("Four");
        }
    }


    public void testOrderOfInstantiationWithoutAdapter() {

        DefaultPicoContainer pico = new DefaultPicoContainer();

        pico.RegisterComponentImplementation(typeof(Four));
        pico.RegisterComponentImplementation(typeof(Two));
        pico.RegisterComponentImplementation(typeof(One));
        pico.RegisterComponentImplementation(typeof(Three));

        Assert.Equals(4, pico.ComponentInstances.Count);

        Startable Startup = (Startable) pico.GetComponentMulticaster(true, false);
        Stoppable shutdown = (Stoppable) pico.GetComponentMulticaster(false, false);
        IDisposable disposal = (IDisposable) pico.GetComponentMulticaster(false, false);

        Assert.True(pico.HasComponent(typeof(One)),"There should have been a 'One' in the internals");

        One one = (One) pico.GetComponentInstance(typeof(One));

        // instantiation - would be difficult to do these in the wrong order!!
        Assert.Equals( 4, one.getInstantiating().Count,"Should be four elems");
        Assert.Equals( "One", one.getInstantiating()[0],"Incorrect Order of Instantiation");
        Assert.Equals("Two", one.getInstantiating()[1],"Incorrect Order of Instantiation");
        Assert.Equals("Three", one.getInstantiating()[2],"Incorrect Order of Instantiation");
        Assert.Equals("Four", one.getInstantiating()[3],"Incorrect Order of Instantiation");

        StartStopDisposeLifecycleComps(Startup, shutdown, disposal, one);

    }


    public void testStartStopStartStopAndDispose() {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);

        pico.RegisterComponentImplementation(typeof(One));
        pico.RegisterComponentImplementation(typeof(Two));
        pico.RegisterComponentImplementation(typeof(Three));
        pico.RegisterComponentImplementation(typeof(Four));

        object o = pico.ComponentInstances;

        One one = (One) pico.GetComponentInstance(typeof(One));

        // instantiation - would be difficult to do these in the wrong order!!
        Assert.Equals(4, one.getInstantiating().Count,"Should be four elems");
        Assert.Equals("One", one.getInstantiating()[0],"Incorrect Order of Instantiation");
        Assert.Equals("Two", one.getInstantiating()[1],"Incorrect Order of Instantiation");
        Assert.Equals("Three", one.getInstantiating()[2],"Incorrect Order of Instantiation");
        Assert.Equals("Four", one.getInstantiating()[3],"Incorrect Order of Instantiation");

        StartStopDisposeLifecycleComps(lifecycle, lifecycle, lifecycle, one);

    }

    private void StartStopDisposeLifecycleComps(Startable Start, Stoppable Stop, IDisposable disp, One one) {
        Start.Start();

        // post instantiation Startup
        Assert.Equals(4, one.getStarting().Count,"Should be four elems");
        Assert.Equals( "One", one.getStarting()[0],"Incorrect Order of Starting");
        Assert.Equals("Two", one.getStarting()[1],"Incorrect Order of Starting");
        Assert.Equals("Three", one.getStarting()[2],"Incorrect Order of Starting");
        Assert.Equals("Four", one.getStarting()[3],"Incorrect Order of Starting");

        Stop.Stop();

        // post instantiation shutdown - REVERSE order.
        Assert.Equals(4, one.getStopping().Count,"Should be four elems");
        Assert.Equals("Four", one.getStopping()[0],"Incorrect Order of Stopping");
        Assert.Equals("Three", one.getStopping()[1],"Incorrect Order of Stopping");
        Assert.Equals("Two", one.getStopping()[2],"Incorrect Order of Stopping");
        Assert.Equals("One", one.getStopping()[3],"Incorrect Order of Stopping");

        disp.Dispose();

        // post instantiation shutdown - REVERSE order.
        Assert.Equals(4, one.getDisposing().Count,"Should be four elems");
        Assert.Equals("Four", one.getDisposing()[0],"Incorrect Order of Stopping");
        Assert.Equals("Three", one.getDisposing()[1],"Incorrect Order of Stopping");
        Assert.Equals("Two", one.getDisposing()[2],"Incorrect Order of Stopping");
        Assert.Equals("One", one.getDisposing()[3],"Incorrect Order of Stopping");
    }


    public void testStartStartCausingBarf() {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));

        object o = pico.ComponentInstances;

        Assert.True(lifecycle.Stopped);
        lifecycle.Start();
        Assert.True(lifecycle.Started);
        try {
            lifecycle.Start();
            Assert.Fail("Should have barfed");
        } catch (Exception ) {
            // expected;
            Assert.True(lifecycle.Started);
        }
    }

    public void testStartStopStopCausingBarf()  {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));

        object o = pico.ComponentInstances;
        Assert.True(lifecycle.Stopped);
        lifecycle.Start();
        Assert.True(lifecycle.Started);
        lifecycle.Stop();
        Assert.True(lifecycle.Stopped);
        try {
            lifecycle.Stop();
            Assert.Fail("Should have barfed");
        } catch (Exception) {
            // expected;
            Assert.True(lifecycle.Stopped);
        }
    }

    public void testDisposeDisposeCausingBarf() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));

        object o = pico.ComponentInstances;
        lifecycle.Start();
        lifecycle.Stop();
        Assert.False(lifecycle.Disposed);
        lifecycle.Dispose();
        Assert.True(lifecycle.Disposed);
        try {
            lifecycle.Dispose();
            Assert.Fail("Should have barfed");
        } catch (Exception ) {
            // expected;
            Assert.True(lifecycle.Disposed);
        }
    }


    public void testStartStopDisposeDisposeCausingBarf() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));

        object o = pico.ComponentInstances;
        lifecycle.Start();
        lifecycle.Stop();
        lifecycle.Dispose();
        try {
            lifecycle.Dispose();
            Assert.Fail("Should have barfed");
        } catch (Exception ) {
            // expected;
        }
    }


    public class FooRunnable :  Startable, Stoppable {
        private int rCount;
        private Thread thread ;
        

        
        public FooRunnable() {
        }

        public int runCount() {
            return rCount;
        }


        public void Start() {
          
            thread = new Thread(new ThreadStart(this.run));
            thread.Start();
        }

        public void Stop() {
            thread.Interrupt();
        }

        // this would do something a bit more concrete
        // than counting in real life !
        public void run() {
            rCount++;
            try {
                Thread.Sleep(10000);
            } catch (Exception) {

            }
        }
    }

    public void testStartStopOfDaemonizedThread() {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        pico.RegisterComponentImplementation(typeof(DependsOnTouchable));
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));
        pico.RegisterComponentImplementation(typeof(FooRunnable));

object o = pico.ComponentInstances;
        lifecycle.Start();
        Assert.True(lifecycle.Started);
        Thread.Sleep(100);
        lifecycle.Stop();
        Assert.True(lifecycle.Stopped);

        FooRunnable foo = (FooRunnable) pico.GetComponentInstance(typeof(FooRunnable));
        Assert.Equals(1, foo.runCount());
        lifecycle.Start();
        Assert.True(lifecycle.Started);
        Thread.Sleep(100);
        lifecycle.Stop();
        Assert.True(lifecycle.Stopped);
        Assert.Equals(2, foo.runCount());

    }

    public void testForgivingNatureOfLifecycleAdapter() {

        DefaultPicoContainer pico = new DefaultPicoContainer();
        LifecyclePicoAdapter lifecycle = new DefaultLifecyclePicoAdapter(pico);


        // Touchable is not Startable (etc). This internals should be able to handle the
        // fact that none of the comps are Startable (etc).
        pico.RegisterComponentImplementation(typeof(SimpleTouchable));

object o = pico.ComponentInstances;

        Assert.True(lifecycle.Stopped);
        lifecycle.Start();
        Assert.True(lifecycle.Started);

    }

}
}
