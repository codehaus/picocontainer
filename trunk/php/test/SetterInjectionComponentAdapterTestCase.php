<?php

SimpleTestOptions::ignore('AbstractComponentAdapterTestCase'); 
abstract class AbstractComponentAdapterTestCase extends UnitTestCase
{
	abstract protected function getComponentAdapterType();			
	
	protected function getDefaultPico()
    {
    	return new DefaultPicoContainer($this->createDefaultComponentAdapterFactory());
    }
	
	function testDEF_createsNewInstanceWithoutParams()
    {    	
    	$picoContainer = $this->getDefaultPico();
    	$componentAdapter = $this->prepDEF_createsNewInstanceWithoutParams();
    	$this->assertEqual($this->getComponentAdapterType(), get_class($componentAdapter));
    	$this->assertNotNull($componentAdapter->getComponentInstance($picoContainer));    
    }
    
    function testDEF_createsNewInstanceWithConstantParamAndWithHint()
    {
    	$picoContainer = $this->getDefaultPico();
    	$componentAdapter = $this->prepDEF_createsNewInstanceWithConstantParamAndWithHint();    	
    	$this->assertNotNull($ci = $componentAdapter->getComponentInstance($picoContainer));    	
    }                    	            
}



class SetterInjectionComponentAdapterTestCase extends AbstractComponentAdapterTestCase 
{	    
	
	
	
	protected function getComponentAdapterType()
	{
		return 'SetterInjectionComponentAdapter';
	}
	
    protected function createDefaultComponentAdapterFactory()
    {
    	return new CachingComponentAdapterFactory(new SetterInjectionComponentAdapterFactory()); 
    }        
       
    function prepDEF_createsNewInstanceWithoutParams()
    {
    	return new SetterInjectionComponentAdapter('EmptyBean');
    }
    
    function prepDEF_createsNewInstanceWithConstantParamAndWithHint()
    {
    	return new SetterInjectionComponentAdapter('PurseBean','PurseBean', array('owner' => new ConstantParameter(new PersonBean()))); 
    }
                                                              
}

class ConstructorInjectionComponentAdapterTestCase extends AbstractComponentAdapterTestCase 
{	    
	protected function getComponentAdapterType()
	{
		return 'ConstructorInjectionComponentAdapter';
	}
	
    protected function createDefaultComponentAdapterFactory()
    {
    	return new CachingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()); 
    }
            
    
    function prepDEF_createsNewInstanceWithoutParams()
    {
    	return new ConstructorInjectionComponentAdapter('SimpleTouchable');
    }
    
    function prepDEF_createsNewInstanceWithConstantParamAndWithHint()
    {
    	return new ConstructorInjectionComponentAdapter('DependsOnTouchable','DependsOnTouchable', array('touchable' => new ConstantParameter(new SimpleTouchable()))); 
    }                    
}

?>