<?

class ClassWithoutConstructor
{    
}

class ClassWithUntyppedConstructorAndDefaultValue
{    
    public function __construct($param1 = 'default value1')
    {
        
    }
}

class ClassWithUntyppedConstructorAndNoDefaultValue
{    
    public function __construct($param1)
    {
        
    }
}    

class ComponentAdapterTests extends UnitTestCase 
{
    function __construct() {
        $this->UnitTestCase();
    }    
    
    function testGetComponentInstanceWithDefaultConstructor()
    {
        $class_name = 'ClassWithoutConstructor';
        $ca = new ConstructorInjectionComponentAdapter($class_name);
        $this->assertNotNull($ca->getComponentInstance(new DefaultPicoContainer()));        
    }

    function testGetComponentInstanceWithConstructorWithDefaultValue()
    {
        $class_name = 'ClassWithUntyppedConstructorAndDefaultValue';
        $ca = new ConstructorInjectionComponentAdapter($class_name);
        $this->assertNotNull($ca->getComponentInstance(new DefaultPicoContainer()));        
    }

    
    function testClassWithUntyppedConstructorAndNoDefaultValue()
    {
        $class_name = 'ClassWithUntyppedConstructorAndNoDefaultValue';
        $ca = new ConstructorInjectionComponentAdapter($class_name);
        
        try
        {
            $ca->getComponentInstance(new DefaultPicoContainer());
            $this->fail();
        } catch (UnsatisfiableDependenciesException $e)
        {
            $this->pass();
        }        
    }

    function testGetComponentInstanceWithParamHint()
    {        
        $class_name = 'Girl';
        $ca = new ConstructorInjectionComponentAdapter($class_name, $class_name, array('boy' => new ConstantParameter(new Boy)));    
        $this->assertNotNull($ca->getComponentInstance(new DefaultPicoContainer()));        
    }
    
    function testGetComponentInstanceWithoutParamHint()
    {                   
        $pc = new DefaultPicoContainer();
        $pc->registerComponent(new InstanceComponentAdapter(new Boy()));
        $pc->registerComponent(new ConstructorInjectionComponentAdapter('Girl'));
        
        $girl = $pc->getComponentInstance('Girl');                
        $girl->kissSomeone();
    }


/*
    function testGetKeyFromClassName()
    {
        $class_name = 'Boy';
        $ca = new ConstructorInjectionComponentAdapter($class_name);               
        $this->assertEqual($ca->getComponentKey(),$class_name);
    }

    function testComponentAdapterImplementation()
    {
        $ca = new ConstructorInjectionComponentAdapter('boyFromInclude','BoyFromInclude','BoyFromInclude.php',array(new ConstantParameter($param_val)));
        $this->assertEqual($ca->getComponentImplementation(),'BoyFromInclude');
    }
    
    function testDecoratingComponentAdapterGetDelagate()
    {        
        $ca_decorated = new ConstructorInjectionComponentAdapter('Boy');
        $ca_decorating =  new DecoratingComponentAdapter($ca_decorated);
        
        if ($ca_decorating->getDelegate() !== $ca_decorated)
        {
            $this->fail();            
        }                 
        else
        {
            $this->pass();
        }       
    }
    
    
    function testCachingComponentAdapter()
    {
        $pico = new DefaultPicoContainer();
        $pico->registerComponent(new CachingComponentAdapter(new ConstructorInjectionComponentAdapter('Boy')));
        
        $boy1 = $pico->getComponentInstance('Boy');
        $boy2 = $pico->getComponentInstance('Boy');
        
        if ($boy1 !== $boy2)
        {
            $this->fail();
        }
        else
        {
            $this->pass();
        }
        
    }


*/
}    
?>