<?php

require_once SIMPLETEST_PATH . '/mock_objects.php';
Mock::generate('PicoContainer');
Mock::generate('ComponentAdapter');

class ConstantParameterTests extends UnitTestCase
{
    public function testStringParameters() {
        $container = new MockPicoContainer($this);
        $adapter = new MockComponentAdapter($this);
        
        $string = (string)rand(10000, 20000);
        $parameter = new ConstantParameter($string);
        $this->assertEqual($string, $parameter->resolveInstance($container, $adapter, ''));
    }
    
    public function testIntParameters() {
        $container = new MockPicoContainer($this);
        $adapter = new MockComponentAdapter($this);
        
        $integer = rand(10000, 20000);
        $parameter = new ConstantParameter($integer);
        $this->assertEqual($integer, $parameter->resolveInstance($container, $adapter, ''));
    }            
}

interface TestInterfaceForCollectiveTypeParameter {}

class TestClassForCollectiveTypeParameter1 implements TestInterfaceForCollectiveTypeParameter {}
class TestClassForCollectiveTypeParameter2 implements TestInterfaceForCollectiveTypeParameter {}

class AcceptsCollectiveTypeParameter{
	
	private $arr_values;
	
	public function __construct($collectiveTypeParameter){		
		$this->arr_values = $collectiveTypeParameter;
	}	
	
	public function countObjectsInArray(){
		return count($this->arr_values);
	}
}


class CollectiveTypeParameterTest extends UnitTestCase {
	
	public function testCollectiveTypeParameter(){
		
			$container = new DefaultPicoContainer();
    	
    		$container->registerComponentImplementation('TestClassForCollectiveTypeParameter1');
    		$container->registerComponentImplementation('TestClassForCollectiveTypeParameter2');
    		
    		$container->registerComponentImplementation(
						'AcceptsCollectiveTypeParameter',
						'AcceptsCollectiveTypeParameter',
						array('collectiveTypeParameter' => new CollectiveTypeParameter('TestInterfaceForCollectiveTypeParameter')));
						
			$obj = $container->getComponentInstance('AcceptsCollectiveTypeParameter');
						
			$this->assertEqual(2, $obj->countObjectsInArray());		
	}
	
}