/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.nanoaop.script.groovy

import org.nanocontainer.nanoaop.*
import org.nanocontainer.nanoaop.script.groovy
import org.nanocontainer.script.groovy.PicoBuilderException
import org.picocontainer.defaults.DefaultComponentAdapterFactory

public class NanoAopGroovyBuilderTestCase extends GroovyTestCase {

    builder = new NanoAopGroovyBuilder()
    cuts = builder.pointcuts()

    public void testContainerScopedInterceptor() {
        log = new StringBuffer()
        logger = new LoggingInterceptor(log)

        pico = builder.container() {
            aspect(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods(), interceptor:logger)
            component(key:Dao, class:DaoImpl)
        }

        dao = pico.getComponentInstance(Dao)
        verifyIntercepted(dao, log)
    }

    public void testContainerScopedContainerSuppliedInterceptor() {
        pico = builder.container() {
            aspect(classCut:cuts.instancesOf(Dao), methodCut:cuts.allMethods(), interceptorKey:LoggingInterceptor)
            component(key:'log', class:StringBuffer)
            component(LoggingInterceptor)
            component(key:Dao, class:DaoImpl)
        }

        dao = pico.getComponentInstance(Dao.class)
        log = pico.getComponentInstance('log')
        verifyIntercepted(dao, log)
    }

    public void testComponentScopedInterceptor() {
        log = new StringBuffer()
        logger = new LoggingInterceptor(log)

        pico = builder.container() {
            component(key:'intercepted', class:DaoImpl) {
                aspect(methodCut:cuts.allMethods(), interceptor:logger)
            }
            component(key:'notIntercepted', class:DaoImpl)
        }

        intercepted = pico.getComponentInstance('intercepted')
        notIntercepted = pico.getComponentInstance('notIntercepted')

        verifyIntercepted(intercepted, log)
        verifyNotIntercepted(notIntercepted, log)
    }

    public void testComponentScopedContainerSuppliedInterceptor() {
        pico = builder.container() {
            component(key:'intercepted', class:DaoImpl) {
                aspect(methodCut:cuts.allMethods(), interceptorKey:LoggingInterceptor)
            }
            component(key:'notIntercepted', class:DaoImpl)
            component(key:'log', class:StringBuffer)
            component(LoggingInterceptor)
        }

        intercepted = pico.getComponentInstance('intercepted')
        notIntercepted = pico.getComponentInstance('notIntercepted')
        log = pico.getComponentInstance('log')

        verifyIntercepted(intercepted, log)
        verifyNotIntercepted(notIntercepted, log)
    }

    public void testContainerScopedMixin() {
        pico = builder.container() {
            component(key:Dao, class:DaoImpl) 
            aspect(classCut:cuts.instancesOf(Dao), mixinClass:IdentifiableMixin)
        }
        dao = pico.getComponentInstance(Dao)
        verifyMixin(dao)
    }
  
    public void testComponentScopedMixin() {
        pico = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(mixinClass:IdentifiableMixin)
            }
        }
        dao = pico.getComponentInstance(Dao)
        verifyMixin(dao)
    }
    
    public void testContainerSuppliedMixin() {
        pico = builder.container() {
            component(key:'order1', class:OrderEntityImpl)
            component(key:'order2', class:OrderEntityImpl)
            component(key:IdGenerator, class:IdGeneratorImpl)
            aspect(classCut:cuts.instancesOf(OrderEntity), mixinClass:IdentifiableMixin)
        }
        
        order1 = pico.getComponentInstance('order1')
        order2 = pico.getComponentInstance('order2')
        
        assertTrue(order1 instanceof Identifiable)
        assertTrue(order2 instanceof Identifiable)
        assertEquals(new Integer(1), order1.id)
        assertEquals(new Integer(2), order2.id)
        
        order1.id = new Integer(42)
        assertEquals(new Integer(42), order1.id)
        assertEquals(new Integer(2), order2.id)
    }
  
    public void testContainerScopedMixinExplicitInterfaces() {
        pico = builder.container() {
            component(key:Dao, class:DaoImpl) 
            aspect(classCut:cuts.instancesOf(Dao), mixinInterfaces:[ Identifiable ], mixinClass:IdentifiableMixin)
        }
        dao = pico.getComponentInstance(Dao)
        verifyMixin(dao)
        assertFalse(dao instanceof AnotherInterface)
    }

    public void testComponentScopedMixinExplicitInterfaces() {
        pico = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(mixinClass:IdentifiableMixin, mixinInterfaces:[ Identifiable ])
            }
        }
        dao = pico.getComponentInstance(Dao)
        verifyMixin(dao)
        assertFalse(dao instanceof AnotherInterface)
    }

    public void testMissingRequiredComponentArguments() {
        shouldFail(PicoBuilderException, {
            builder.container() {
                component(value:'whoops')
            }
        })
    }

    public void testClassCutOrComponentCutRequiredForInterceptor() {
        shouldFail(PicoBuilderException, {
            builder.container() {
                aspect(interceptorKey:'whoops')
            }
        })
    }

    public void testMethodCutRequiredForInterceptor() {
        shouldFail(PicoBuilderException, {
            builder.container() {
                aspect(classCut:cuts.instancesOf(Dao), interceptorKey:'whoops')
            }
        })
    }

    public void testClassCutOrComponentCutRequiredForMixin() {
        shouldFail(PicoBuilderException, {
            builder.container() {
                aspect(mixinClass:Dao)
            }
        })
    }
    
    public void testNoAdviceSpecifiedInAspect() {
        shouldFail(PicoBuilderException, {
            builder.container() {
                aspect(classCut:cuts.instancesOf(Dao))
            }
        })
    }
    
    public void testComponentInstance() {
        // Note:  aspecting of instances is not supported, but we just want to make sure we didn't mess anything up.
        pico = builder.container() {
            component(key:'foo', instance:'bar')
        }
        assertEquals('bar', pico.getComponentInstance('foo'))
    }
    
    public void testBean() {
        // Note:  aspecting of beanClass instantiated beans isn't supported either, but again we just want to make sure we didn't mess anything up.
        pico = builder.container() {
            bean(beanClass:StringBean, firstName:'tom', lastName:'jones')
        }
        stringBean = pico.getComponentInstance(StringBean)
        assertNotNull(stringBean)
        assertEquals('tom', stringBean.firstName)
        assertEquals('jones', stringBean.lastName)
    }    
    
    public void testExample() {
        log = new StringBuffer()
        logger = new LoggingInterceptor(log)
        builder = new NanoAopGroovyBuilder()
        cuts = builder.pointcuts()

        pico = builder.container() {
            component(key:Dao, class:DaoImpl) {
                aspect(methodCut:cuts.allMethods(), interceptor:logger)
            }
            component(key:CustomerEntity, class:CustomerEntityImpl)
            component(key:OrderEntity, class:OrderEntityImpl)

            aspect(classCut:cuts.instancesOf(Entity), mixinClass:IdentifiableMixin)
            aspect(classCut:cuts.packageName('org.nanocontainer.nanoaop'), methodCut:cuts.signature('save*'), interceptor:logger)
        }
        
        dao = pico.getComponentInstance(Dao)
        customer = pico.getComponentInstance(CustomerEntity)
        order = pico.getComponentInstance(OrderEntity)
        
        verifyIntercepted(dao, log)
        verifyMixin(customer)
        verifyMixin(order)
        
        before = log.toString()
        customer.saveMe()
        assertEquals(before + 'startend', log.toString())
        
        before = log.toString()
        order.saveMeToo()
        assertEquals(before + 'startend', log.toString())
    }
    
    public void testScriptSuppliedCaf() {
        log = new StringBuffer()
        logger = new LoggingInterceptor(log)
        caf = new DefaultComponentAdapterFactory()
        
        pico = builder.container(adapterFactory:caf) {
            aspect(classCut:cuts.instancesOf(Dao.class), methodCut:cuts.allMethods(), interceptor:logger)
            component(key:Dao, class:DaoImpl)
        }

        dao = pico.getComponentInstance(Dao)
        verifyIntercepted(dao, log)
    }
   
    void verifyIntercepted(dao, log) {
        before = log.toString()
        data = dao.loadData()
        assertEquals('data', data)
        assertEquals(before + 'startend', log.toString())
    }

    void verifyNotIntercepted(dao, log) {
        before = log.toString()
        data = dao.loadData()
        assertEquals('data', data)
        assertEquals(before, log.toString())
    }

    void verifyMixin(Object component) {
        assertTrue(component instanceof Identifiable)
        component.setId("id")
        assertEquals("id", component.getId())
    }

    void verifyNoMixin(Object component) {
        assertFalse(component instanceof Identifiable)
    }

}