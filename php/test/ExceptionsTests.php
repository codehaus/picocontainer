<?php

class ExceptionsTests extends UnitTestCase 
{
    function __construct() {
        $this->UnitTestCase();
    }
    
    function testDuplicateComponentKeyRegistrationException()
    {
       $pico = new DefaultPicoContainer();
       $pico->registerComponentImplementation('boykey','Boy');
       
       try
       {
            $pico->registerComponentImplementation('boykey','Boy');
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
        $pico->registerComponentImplementation('C1');
        $pico->registerComponentImplementation('C2');                
       
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
        $pico->registerComponentImplementation('SimpleTouchable');
        $pico->registerComponentImplementation('DerivedTouchable');
        $pico->registerComponentImplementation('DependsOnTouchable');
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