<?php

class ComponentKeyTests extends UnitTestCase 
{
    function __construct() {
        $this->UnitTestCase();
    }
        
    function testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations()
    {
    	$pico = new DefaultPicoContainer();
    	$pico->regComponentImpl('AlternativeTouchable');
    	$pico->regComponentImpl('Touchable','SimpleTouchable');    
    	
    	$this->assertNotNull($pico->getComponentInstanceOfType('Touchable'));
    }
    
}

?>