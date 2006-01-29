<?php

class ExceptionsTests extends UnitTestCase 
{
    function __construct() {
        $this->UnitTestCase();
    }
    
    function testIncludeFileNameNotDefinedRegistrationException()
    {
    	$pico = new DefaultPicoContainer();
    	
    	try
    	{
    		$pico->regComponentImplWithIncFileName('','AnyNonIncludedClass');
    		$this->fail();
    	}
    	catch(IncludeFileNameNotDefinedRegistrationException $e)
    	{
    		$this->pass();
    	}
    }
    
    function testLazyIncludedClassNotDefinedException()
    {
    	
    	$pico = new DefaultPicoContainer();
    	$ca = new LazyIncludingComponentAdapter(
			new ConstructorInjectionComponentAdapter('NotDefinedInIncludeFile'),
			PICOCONTAINER_TEST_PATH . '/lazyinclude.empty.inc.php');
    	
    	try
    	{
    		$ca->getComponentInstance($pico);
    		$this->fail();
    	}
    	catch (LazyIncludedClassNotDefinedException $e) 
    	{
    		$this->assertEqual($e->getClassName(),'NotDefinedInIncludeFile');
    	}
    	
    }
    
    function testDuplicateComponentKeyRegistrationException()
    {
       $pico = new DefaultPicoContainer();
       $pico->regComponentImpl('boykey','Boy');
       
       try
       {
            $pico->regComponentImpl('boykey','Boy');
            $this->fail();
       }
       catch (DuplicateComponentKeyRegistrationException $e)
       {
            $this->pass();
       } 
    }
            
    function testFailWithCyclicDependency()
    {        
        $pico = new DefaultPicoContainer();
        $pico->regComponentImpl('C1');
        $pico->regComponentImpl('C2');                
       
        try
        {
            $c2 = $pico->getComponentInstance('C2');
            $this->fail();
        }
        catch (CyclicDependencyException $e)
        {
            $this->pass();
        }               
    }      
    
    function testFailWithAmbiguousComponentResolutionException()
    {
        $pico = new DefaultPicoContainer();
        $pico->regComponentImpl('SimpleTouchable');
        $pico->regComponentImpl('DerivedTouchable');
        $pico->regComponentImpl('DependsOnTouchable');
        try
        {
            $pico->getComponentInstance('DependsOnTouchable');                
            $this->fail();
        }
        catch (AmbiguousComponentResolutionException $e)
        {                        
            $this->pass();
        }
    }  
}

?>