<?php


interface Parameter
{
    public function resolveInstance(PicoContainer $container, ComponentAdapter $adapter, $expectedType);
}

class ConstantParameter implements Parameter
{
    private $_value;
    
    public function __construct($value)
    {
        $this->_value = $value;
    }
    
    public function resolveInstance(PicoContainer $container, ComponentAdapter $adapter, $expectedType)
    {
        return $this->_value;
    }
}

class BasicComponentParameter implements Parameter
{
    private $_componentKey;
    
    public function __construct($componentKey = null)
    {
        $this->_componentKey = $componentKey;
    }    
    
    public function resolveInstance(PicoContainer $container, ComponentAdapter $adapter, $expectedType)
    {                        
        $adapter = $container->getComponentAdapterOfType($expectedType);
                        
        if ($adapter!=null)
        {                    
            return $adapter->getComponentInstance($container);
        }             
    }   
    
    private function getTargetAdapter(PicoContainer $container, $expectedType) 
    {
        if ($this->_componentKey != null) 
        {            
            return $container->getComponentAdapter($this->_componentKey);
        } 
        else 
        {
            return $container->getComponentAdapterOfType($expectedType);
        }
    } 
}

?>