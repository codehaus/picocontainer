<?php

class LazyIncludingCATests extends UnitTestCase 
{
	
	function testLIWithincludedDependencies()
	{
		
		$pico = new DefaultPicoContainer();		
		$pico->registerComponent(new LazyIncludingComponentAdapter(
			new ConstructorInjectionComponentAdapter('LazyIncludeModel'),
			PICOCONTAINER_TEST_PATH . '/lazyincludemodel.inc.php'));
		$pico->registerComponentImplementation('SimpleTouchable');
				
		$this->assertFalse(class_exists('LazyIncludeModel'));
						
		$ci = $pico->getComponentInstance('LazyIncludeModel');		
		
		$this->assertIsA($ci,'LazyIncludeModel');
    	    	    	
	}
	
	
	function testLIWithoutIncludedDependencies()
	{
		$pico = new DefaultPicoContainer();		
		$pico->registerComponent(new LazyIncludingComponentAdapter(
			new ConstructorInjectionComponentAdapter('LazyIncludeModelWithDpendencies'),
			PICOCONTAINER_TEST_PATH . '/lazyincludemodelwithdpendencies.inc.php'));
		$pico->registerComponent(new LazyIncludingComponentAdapter(
			new ConstructorInjectionComponentAdapter('LazyIncludeModelDependend'),
			PICOCONTAINER_TEST_PATH . '/lazyincludemodeldependend.inc.php'));
		
		$this->assertFalse(class_exists('LazyIncludeModelWithDpendencies'));
		$this->assertFalse(class_exists('LazyIncludeModelDependend'));
		
		$ci = $pico->getComponentInstance('LazyIncludeModelWithDpendencies');
		
		$this->assertTrue(class_exists('LazyIncludeModelWithDpendencies'));
		$this->assertTrue(class_exists('LazyIncludeModelDependend'));
		
		$this->assertIsA($ci,'LazyIncludeModelWithDpendencies');
	}
			
}
   
?>