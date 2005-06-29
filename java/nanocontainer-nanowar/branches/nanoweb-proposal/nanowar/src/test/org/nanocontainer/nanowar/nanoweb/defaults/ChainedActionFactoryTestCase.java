package org.nanocontainer.nanowar.nanoweb.defaults;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.nanocontainer.nanowar.nanoweb.ActionFactory;
import org.nanocontainer.nanowar.nanoweb.MyAction;
import org.picocontainer.defaults.DefaultPicoContainer;

public class ChainedActionFactoryTestCase extends MockObjectTestCase {

    public void testBasic() throws Exception {

        DefaultPicoContainer emptyPico = new DefaultPicoContainer();

        Mock factory1 = mock(ActionFactory.class);
        Mock factory2 = mock(ActionFactory.class);
        Mock factory3 = mock(ActionFactory.class);
        Mock factory4 = mock(ActionFactory.class);

        Object expectedAction = new MyAction();

        factory1.expects(once()).method("getInstance").with(same(emptyPico), eq("/path/toget")).will(returnValue(null));
        factory2.expects(once()).method("getInstance").with(same(emptyPico), eq("/path/toget")).will(returnValue(null));
        factory3.expects(once()).method("getInstance").with(same(emptyPico), eq("/path/toget")).will(returnValue(expectedAction));
        // nothing from loader4

        ChainedActionFactory chainedLoader = new ChainedActionFactory(new ActionFactory[] { (ActionFactory) factory1.proxy(), (ActionFactory) factory2.proxy(), (ActionFactory) factory3.proxy(),
                (ActionFactory) factory4.proxy() });

        Object action = chainedLoader.getInstance(emptyPico, "/path/toget");
        assertSame(expectedAction, action);
    }

}
