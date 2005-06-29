package org.nanocontainer.nanowar.nanoweb.defaults;

import java.io.File;

import junit.framework.TestCase;

import org.nanocontainer.nanowar.nanoweb.MyAction;


public class AbstractFileBasedActionFactoryTestCase extends TestCase {

    public void testCacheSystem() throws Exception {
        MyAbstractFileBasedActionFactory factory = new MyAbstractFileBasedActionFactory(null, null);

        File file = new File("somefile");
        
        assertNull(factory.getFromCache(file));
        factory.setToCache(file, MyAction.class, 100);
        
        assertNotNull(factory.getFromCache(file, 10));
        assertNull(factory.getFromCache(file, 1000));
    }
    
    class MyAbstractFileBasedActionFactory extends AbstractFileBasedActionFactory {

        public MyAbstractFileBasedActionFactory(String rootPath, String extension) {
            super(rootPath, extension);
        }

        protected Class getClass(File actionFile) throws Exception {
            return MyAction.class;
        }

    }
    
}
