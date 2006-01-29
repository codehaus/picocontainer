<?php

class A 
{
	function __construct(B $B) {}
}

class B
{
	function __construct(C $C) {}
}

class C
{
	function __construct(A $A) {}
}


class SeveralDependanciesWithConcencrateClasses
{
	private $simpleTouchable;
	private $alternativeTouchable; 
	
	function __construct(SimpleTouchable $simpleTouchable, AlternativeTouchable $alternativeTouchable)
	{
		$this->simpleTouchable = $simpleTouchable;
		$this->alternativeTouchable = $alternativeTouchable;
	}
	
	function touch()
	{
		$this->simpleTouchable->touch();
		$this->alternativeTouchable->touch();
	}
}

class SeveralDependanciesWithInterfaces
{
	private $simpleTouchable;
	private $alternativeTouchable; 
	
	function __construct(Touchable $simpleTouchable, Touchable $alternativeTouchable)
	{
		$this->simpleTouchable = $simpleTouchable;
		$this->alternativeTouchable = $alternativeTouchable;
	}
	
	function touch()
	{
		$this->simpleTouchable->touch();
		$this->alternativeTouchable->touch();
	}
	
	function getAlternativeTouchable()
	{
		return $this->alternativeTouchable;
	}
}

class SeveralDependanciesWithInterfaceAndConcencrateClass
{
	private $simpleTouchable;
	private $alternativeTouchable; 
	
	function __construct(SimpleTouchable $simpleTouchable, Touchable $alternativeTouchable)
	{
		$this->simpleTouchable = $simpleTouchable;
		$this->alternativeTouchable = $alternativeTouchable;
	}
	
	function touch()
	{
		$this->simpleTouchable->touch();
		$this->alternativeTouchable->touch();
	}
}


class SitePointTests extends UnitTestCase 
{
	function __construct() {
        $this->UnitTestCase();
    }
    
    function testDependsOnTwoDifferentWithoutParams()
    {
    	$pico = new DefaultPicoContainer();
    	
    	
    	$pico->regComponentImpl('SimpleTouchable');
    	$pico->regComponentImpl('AlternativeTouchable');
    	
    	$pico->regComponentImpl('SeveralDependanciesWithConcencrateClasses','SeveralDependanciesWithConcencrateClasses',array('simpleTouchable' => new ConstantParameter(new SimpleTouchable()),'alternativeTouchable' => new ConstantParameter(new AlternativeTouchable())));
    	
    	$ci = $pico->getComponentInstance('SeveralDependanciesWithConcencrateClasses');
    	$this->assertNotNull($ci);
    	
    	$c2 = $pico->getComponentInstance('SeveralDependanciesWithConcencrateClasses');
    	
    	$this->assertTrue($ci === $c2);
    }
    
    function testDependsOnTwoDifferentWithtInterfaceWithoutParams()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('SimpleTouchable');
    	$pico->regComponentImpl('AlternativeTouchable');
    	$pico->regComponentImpl('SeveralDependanciesWithInterfaces');
    	
    	    	
    	
    	try
    	{
    		$this->assertNotNull($pico->getComponentInstance('SeveralDependanciesWithInterfaces'));
    		$this->fail();
    	}
    	catch (AmbiguousComponentResolutionException $e)
    	{
    		$this->pass();
    	}
    }

    
    
    function testDependsOnTwoDifferentWithtParams()    
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('SimpleTouchable');
    	$pico->regComponentImpl('AlternativeTouchable');
    	$pico->regComponentImpl(
			'SeveralDependanciesWithInterfaces', //key 
			'SeveralDependanciesWithInterfaces', //class name
    		array('simpleTouchable' => new BasicComponentParameter('SimpleTouchable'),
    			  'alternativeTouchable' => new BasicComponentParameter('AlternativeTouchable'))); //params - hint
    			  
    	$ci = $pico->getComponentInstance('SeveralDependanciesWithInterfaces'); 		  
    	$this->assertNotNull($ci);
    	$ci->touch();    
    }
    
    function testDependsOnTwoDifferentWithtParamAndAutoWire()    
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('SimpleTouchable');
    	$pico->regComponentImpl('AlternativeTouchable');
    	$pico->regComponentImpl('SeveralDependanciesWithInterfaceAndConcencrateClass',
    										   'SeveralDependanciesWithInterfaceAndConcencrateClass',
    										   array('alternativeTouchable' => new BasicComponentParameter('AlternativeTouchable')));			
    			  
    	$ci = $pico->getComponentInstance('SeveralDependanciesWithInterfaceAndConcencrateClass'); 		  
    	$this->assertNotNull($ci);
    	$ci->touch();    
    } 
    
    function testDependsOnTwoDifferentOnlyWithtAutoWire()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('SimpleTouchable');
    	$pico->regComponentImpl('Touchable','AlternativeTouchable');
    	$pico->regComponentImpl('SeveralDependanciesWithInterfaceAndConcencrateClass');
    	
    	$ci = $pico->getComponentInstance('SeveralDependanciesWithInterfaceAndConcencrateClass'); 		  
    	$this->assertNotNull($ci);
    	$ci->touch();
    }
    
     
    
    function testFailWithCyclicDependency()
    {
    	    	       
        $pico = new DefaultPicoContainer();
        $pico->regComponentImpl('A');
        $pico->regComponentImpl('B');
        $pico->regComponentImpl('C');                              
       
        try
        {
            $c2 = $pico->getComponentInstance('A');
            $this->fail();
        }
        catch (CyclicDependencyException $e)
        {
            $this->pass();
        }               
    }   
    
    
    function testPSzarwasQ1()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('Touchable','SimpleTouchable');
    	$pico->regComponentImpl('AlternativeTouchable');
    	$pico->regComponentImpl('SeveralDependanciesWithInterfaces','SeveralDependanciesWithInterfaces',array('alternativeTouchable' => new BasicComponentParameter('AlternativeTouchable')));
    	
    	$ci = $pico->getComponentInstance('SeveralDependanciesWithInterfaces'); 		  
    	$this->assertNotNull($ci);
    	
    	$this->assertIsA($ci->getAlternativeTouchable(),'AlternativeTouchable');    	
    }

    
    function testNotThrowingReflectionExceptionWhenIteratingThroughtAllTheCA()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('NotDefinedClass');
    	$pico->regComponentImpl('st','SimpleTouchable');
    	$pico->regComponentImpl('DependsOnTouchable');
    	$pico->regComponentImpl('DependsOnSimpleTouchable');
    	
    	
    	$cObj1 = $pico->getComponentInstance('DependsOnTouchable'); 
    	$cObj2 = $pico->getComponentInstance('DependsOnSimpleTouchable');
    	$this->assertNotNull($cObj1);
    	$this->assertNotNull($cObj2);
    }
       
}

?>