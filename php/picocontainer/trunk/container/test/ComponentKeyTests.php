<?php

class ComponentKeyTests extends UnitTestCase 
{
    function __construct() {
        $this->UnitTestCase();
    }
    
    
    function testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->registerComponentImplementation('AlternativeTouchable');
    	$pico->registerComponentImplementation('Touchable','SimpleTouchable');    
    	
    	$this->assertNotNull($pico->getComponentInstanceOfType('Touchable'));
    }
    
}

?>