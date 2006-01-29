<?php

class DefaultPicoContainerTests extends UnitTestCase {
    function __construct() {
        $this->UnitTestCase();
    }
    
   function testregComponentImplWithIncFileName()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImplWithIncFileName('any.filename.inc.php','AnyNonIncludedClass');
    	$ca = $pico->getComponentAdapter('AnyNonIncludedClass');
    	
    	$this->assertIsA($ca,'LazyIncludingComponentAdapter');
    }

	function testGetComponentInstanceReturnsNullWhenNotRegistered()
	{
		$pico = new DefaultPicoContainer();
		$t = $pico->getComponentInstance('SimpleTouchable');
		$this->assertNull($t);
	}

    function testRegisterComponentInstance()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->regComponentInstance(new SimpleTouchable());        
        $t = $pico->getComponentInstance('SimpleTouchable');        
        $this->assertNotNull($t);
    }
    
    function testRegisterComponentInstanceWithKey()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->regComponentInstance(new SimpleTouchable(),'st');        
        $t = $pico->getComponentInstance('st');        
        $this->assertNotNull($t);
    }        
    
    function testUnregComponent()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->regComponentInstance(new SimpleTouchable());        
        $t = $pico->getComponentInstance('SimpleTouchable');        
        $this->assertNotNull($t);
        
        $pico->unregComponent('SimpleTouchable');
        $t = $pico->getComponentInstance('SimpleTouchable');
        $this->assertNull($t);
    }
    
    
   function testGetDefaultConstructorComponentWithKey()
    {
        $pico = new DefaultPicoContainer();
        $pico->regComponent(new ConstructorInjectionComponentAdapter('boykey','Boy'));
        $boy = $pico->getComponentInstance('boykey');        
        $this->assertNotNull($boy);        
    }
    
    
    function testGetDefaultConstructorComponentWithoutKey()
    {
        $pico = new DefaultPicoContainer();
        $pico->regComponent(new ConstructorInjectionComponentAdapter('Boy'));
        $boy = $pico->getComponentInstance('Boy');        
        $this->assertNotNull($boy);        
    }
    
    
    function testConstantParameter()
    {
        $pico = new DefaultPicoContainer();
        $param_val = 'param test';
        $pico->regComponent(new ConstructorInjectionComponentAdapter('boykey','BoyWithConstantParam',array('param' => new ConstantParameter($param_val))));        
        $boy = $pico->getComponentInstance('boykey');
        $this->assertNotNull($boy);
        $this->assertEqual($boy->getParam(),$param_val);
    }
 
    
    function testConstantParameterAssumedWhenParameterRegisteredAsASimpleValue(){
    	
    	$pico = new DefaultPicoContainer();
        $param_val = 'param test';
        $pico->regComponentImpl('boykey','BoyWithConstantParam',array('param' => $param_val));        
        $boy = $pico->getComponentInstance('boykey');
        $this->assertNotNull($boy);
        $this->assertEqual($boy->getParam(),$param_val);
    }
    
    
    function testBasicComponentParameter()
    {
        $pico = new DefaultPicoContainer();        
        $pico->regComponent(new ConstructorInjectionComponentAdapter('boy','Boy'));
        $pico->regComponent(new ConstructorInjectionComponentAdapter('girl','Girl', array(new BasicComponentParameter('boy'))));        
        $girl = $pico->getComponentInstance('girl');
        $this->assertNotNull($girl);
    }
        
    
    function testGetAllComponentAdaptersOfType() {
        $pico = new DefaultPicoContainer();
        $pico->regComponent(new ConstructorInjectionComponentAdapter('boy','Boy'));
        $this->assertNotNull($pico->getComponentAdaptersOfType('Boy'));                
    }
        
    
    function testGetComponentWithAutoWire() {
        $pico = new DefaultPicoContainer();
        $pico->regComponent(new ConstructorInjectionComponentAdapter('Boy'));
        $pico->regComponent(new ConstructorInjectionComponentAdapter('Girl'));        
        
        $girl = $pico->getComponentInstance('Girl');        
        $this->assertNotNull($girl);
 		$this->assertWantedPattern('/kissed.*girl/i',$girl->kissSomeone());                           
    } 
    
    function testRegisterComponentImplementationWithClassName()
    {
        $pico = new DefaultPicoContainer();
        $pico->regComponentImpl('Boy');
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
        $pico->regComponent(new ConstructorInjectionComponentAdapter('Boy'));
        
        $boy1 = $pico->getComponentInstance('Boy');
        $boy2 = $pico->getComponentInstance('Boy');
        
        $this->assertCopy($boy1, $boy2);                       
    }
                              
    
    function testGetAdapterWithTypeWhereTypeIsSubclass()
    {
        $pico = new DefaultPicoContainer();
        
        $pico->regComponentImpl('DerivedTouchable');
        $pico->regComponentImpl('DependsOnSimpleTouchable');
        
        $this->assertNotNull($pico->getComponentInstance('DependsOnSimpleTouchable'));        
    }
    
    
    function testInvalidParameterForConstructOfDefaultPicoContainer() {
        assert_options(ASSERT_ACTIVE, 1);
        new DefaultPicoContainer('string');
        $this->assertError();
        
        new DefaultPicoContainer(12345);
        $this->assertError();
        
        new DefaultPicoContainer(2.51);
        $this->assertError();
        
        new DefaultPicoContainer(array());
        $this->assertError();
        
        new DefaultPicoContainer(true);
        $this->assertError();
    }
    
    function testNonObjectRegisterInstance() {
        $pico = new DefaultPicoContainer();
        $pass = false;
        
        try {
            $pico->regComponentInstance('string');
        }
        catch (PicoRegistrationException $e) {
            $pass = true;
        }
        $this->assertTrue($pass, 'Caught exception');
    }
    
    //TODO:
	/*
    function testUnambiguouSelfDependency() {
        
        $pico = new DefaultPicoContainer();
        
        $pico->regComponentImpl('SimpleTouchable');
        $pico->regComponentImpl('DecoratedTouchable');
        
        $t = $pico->getComponentInstance('DecoratedTouchable');
        $this->assertNotNull($t);
    }
    */
                    
}
?>