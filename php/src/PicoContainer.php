<?php

interface PicoContainer
{    
    public function getComponentInstance($componentKey);                
    public function getComponentInstanceOfType($componentType);
    public function getComponentAdapter($componentKey);               
    public function getComponentAdaptersOfType($componentType);    
    public function getComponentAdapterOfType($componentType);
}

interface MutablePicoContainer extends PicoContainer 
{
    public function registerComponent(ComponentAdapter $componentAdapter);
    public function registerComponentImplementation($componentKey, $componentImplementation = '', $componentParams = array());
    public function registerComponentInstance($componentInstance, $componentKey = null);
    public function unregisterComponent($componentKey);
}     

class DefaultPicoContainer implements MutablePicoContainer
{
    private $_componentAdapters = array();
    private $_componentAdapterFactory;
    
    public function __construct($componentAdapterFactory = null)
    {
        if ($componentAdapterFactory == null)
        {                
            $componentAdapterFactory = new CachingComponentAdapterFactory(new ConstructorInjectionComponentAdapterFactory()); 
        }
                
        $this->_componentAdapterFactory = $componentAdapterFactory; 
    }
       
    
    public function getComponentInstance($componentKey)
    {
        $componentAdapter = $this->getComponentAdapter($componentKey);        
        if ($componentAdapter != null) 
        {          
            return $componentAdapter->getComponentInstance($this);
        } 
        else 
        {
            return null;
        }        
    }
    
    public function getComponentInstanceOfType($componentType)
    {
    	$componentAdapter = $this->getComponentAdapterOfType($componentType);    	    	 
    	if ($componentAdapter != null)
    	{
    		return $componentAdapter->getComponentInstance($this);
    	}
    	else
    	{
    		return null;
    	}
    	
    }
            
    public function getComponentAdapter($componentKey)
    {
        return $this->_componentAdapters[$componentKey];
    }
    
    
    public function getComponentAdaptersOfType($componentType) 
    {
        $result = array();        
        $ctReflectionClass = new ReflectionClass($componentType);                                
                
        foreach($this->_componentAdapters as $cadapter)
        {
            if ($ctReflectionClass->isInterface())
            {            
                $catReflectionClass = new ReflectionClass($cadapter->getComponentImplementation());        
                if ($catReflectionClass->implementsInterface($ctReflectionClass->getName()))
                {
                    $result[] = $cadapter;
                }                
            }
            else
            {                                    
                if ($componentType == $cadapter->getComponentImplementation())
                {
                    $result[] = $cadapter;         
                }
                else
                {                
                    $catReflectionClass = new ReflectionClass($cadapter->getComponentImplementation());
                    if($catReflectionClass->isSubclassOf($ctReflectionClass))
                    {
                        $result[] = $cadapter;    
                    } 
                }
            }
        }
        
        return $result;
    }
    
    public function getComponentAdapterOfType($expectedType)
    {
    	$adapterByKey = $this->getComponentAdapter($expectedType);
        if ($adapterByKey != null) {
            return $adapterByKey;
        }
    	
        $result = $this->getComponentAdaptersOfType($expectedType);                
        
        if (count($result) == 1)
        {
            return $result[0];
        }
        elseif (count($result) == 0)
        {
            return null;
        } 
        else
        {                        
            $result_to_ex = array();            
            foreach($result as $cadapter)
            {
                $result_to_ex[] = $cadapter->getComponentKey(); 
            }
            throw new AmbiguousComponentResolutionException($expectedType,$result_to_ex);
        }        
    }                
    
    public function registerComponent(ComponentAdapter $componentAdapter)
    {                        
        if (is_null($this->_componentAdapters[$componentAdapter->getComponentKey()]))
        {                       
            $this->_componentAdapters[$componentAdapter->getComponentKey()] = $componentAdapter;
        }
        else
        {
            throw new DuplicateComponentKeyRegistrationException($componentAdapter->getComponentKey());
        }                  
    }
    
    public function registerComponentImplementation($componentKey, $componentImplementation = '', $componentParams = array())
    {                
        $ca = $this->_componentAdapterFactory->createComponentAdapter($componentKey, $componentImplementation, $componentParams);
        $this->registerComponent($ca);        
    }
    
    public function registerComponentInstance($componentInstance, $componentKey = null)
    {
        $this->registerComponent(new InstanceComponentAdapter($componentInstance, $componentKey));    
    }
    
    public function unregisterComponent($componentKey)
    {
        if ($componentKey != '')
        {                        
            $this->_componentAdapters = array_diff ($this->_componentAdapters, array($componentKey => $this->getComponentAdapter($componentKey)));            
        }
    }                    
}   

?>