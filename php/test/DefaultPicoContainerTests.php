<?php



class DefaultPicoContainerTests extends UnitTestCase {
    function __construct() {
        $this->UnitTestCase();
    }
    

	function testGetComponentInstanceReturnsWhenNotRegistered()
	{
		$pico = new DefaultPicoContainer();
		$t = $pico->getComponentInstance('SimpleTouchable');
		$this->assertNull($t);
	}

    function testRegisterComponentInstance()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->registerComponentInstance(new SimpleTouchable());        
        $t = $pico->getComponentInstance('SimpleTouchable');        
        $this->assertNotNull($t);
    }
    
    function testRegisterComponentInstanceWithKey()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->registerComponentInstance(new SimpleTouchable(),'st');        
        $t = $pico->getComponentInstance('st');        
        $this->assertNotNull($t);
    }        
    
    function testUnregisterComponent()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->registerComponentInstance(new SimpleTouchable());        
        $t = $pico->getComponentInstance('SimpleTouchable');        
        $this->assertNotNull($t);
        
        $pico->unregisterComponent('SimpleTouchable');
        $t = $pico->getComponentInstance('SimpleTouchable');
        $this->assertNull($t);
    }
    
    
    function testGetDefaultConstructorComponentWithKey()
    {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('boykey','Boy'));
        $boy = $pico->getComponentInstance('boykey');        
        $this->assertNotNull($boy);        
    }
    
    
    function testGetDefaultConstructorComponentWithoutKey()
    {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('Boy'));
        $boy = $pico->getComponentInstance('Boy');        
        $this->assertNotNull($boy);        
    }
    
    
    function testConstantParameter()
    {
        $pico = new DefaultPicoContainer();
        $param_val = 'test parametru';
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('boykey','BoyWithConstantParam',array('param' => new ConstantParameter($param_val))));        
        $boy = $pico->getComponentInstance('boykey');
        $this->assertNotNull($boy);
        $this->assertEqual($boy->getParam(),$param_val);
    }
    
    
    function testBasicComponentParameter()
    {
        $pico = new DefaultPicoContainer();
        $param_val = 'test parametru';
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('boy','Boy'));
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('girl','Girl', null, array(new BasicComponentParameter('boy'))));        
        $girl = $pico->getComponentInstance('girl');
        $this->assertNotNull($girl);
    }
    
    function testGetAllComponentAdaptersOfType() {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('boy','Boy'));
        $this->assertNotNull($pico->getComponentAdaptersOfType('Boy'));                
    }
        
    
    function testGetComponentWithAutoWire() {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('Boy'));
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('Girl'));        
        
        $girl = $pico->getComponentInstance('Girl');        
        $this->assertNotNull($girl);
        
        $girl->kissSomeone();                
    } 
    
    
    
    function testRegisterComponentImplementationWithClassName()
    {
        $pico = new DefaultPicoContainer();
        $pico->registerComponentImplementation('Boy');
        $boy = $pico->getComponentInstance('Boy');
        $this->assertNotNull($boy);   
    }
      
    
    function testNoRegisteredComponentWithKey()
    {
        $pico = new DefaultPicoContainer();        
        $this->assertNull($pico->getComponentInstance('boy'));
    }
    

    function testNonCachingComponentAdapter()
    {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new ConstructorInjectionComponentAdapter('Boy'));
        
        $boy1 = $pico->getComponentInstance('Boy');
        $boy2 = $pico->getComponentInstance('Boy');
        
        if ($boy1 === $boy2)
        {
            $this->fail();
        }
        else
        {
            $this->pass();
        }                
    }                          
    
    function testGetAdapterWithTypeWhereTypeIsSubclass()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->registerComponentImplementation('DerivedTouchable');
        $pico->registerComponentImplementation('DependsOnSimpleTouchable');
        
        $this->assertNotNull($pico->getComponentInstance('DependsOnSimpleTouchable'));        
    }
    
    
	/*         
    function testUnambiguouSelfDependency() {
        
        $pico = new DefaultPicoContainer();
        
        $pico->registerComponentImplementation('SimpleTouchable');
        $pico->registerComponentImplementation('DecoratedTouchable');
        
        $t = $pico->getComponentInstance('DecoratedTouchable');
        $this->assertNotNull($t);
    }
    */
                    
}
?>