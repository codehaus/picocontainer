<?php

/**
 * This is the core interface for PicoContainer. It is used to retrieve component instances from the container; 
 * it only has accessor methods. In order to register components in a PicoContainer, use a {@link MutablePicoContainer}.
 *
 * @author Java version authors
 * @author Pawel Kozlowski <pawel.kozlowski@gmail.com>
 * @version $Revision$
 */
interface PicoContainer
{    
    public function getComponentInstance($componentKey);                
    public function getComponentInstanceOfType($componentType);
    public function getComponentAdapter($componentKey);               
    public function getComponentAdaptersOfType($componentType);    
    public function getComponentAdapterOfType($componentType);
}

/**
 * This is the core interface used for registration of components with a container. It is possible to register 
 * an implementation class, an instance or a ComponentAdapter.
 *
 * @author Java version authors
 * @author Pawel Kozlowski
 * @version $Revision$
 */
interface MutablePicoContainer extends PicoContainer 
{
    public function registerComponent(ComponentAdapter $componentAdapter);
    public function registerComponentImplementation($componentKey, $componentImplementation = '', $componentParams = array());
    public function registerComponentImplementationWithIncFileName($includeFileName, $componentKey, $componentImplementation = '', $componentParams = array());   
    public function registerComponentInstance($componentInstance, $componentKey = null);
    public function unregisterComponent($componentKey);
}


/**
 * <p/>
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 * </p>
 * <p/>
 * Using Class name as keys to the registerComponentImplementation method makes
 * a subtle semantic difference:
 * </p>
 * <p/>
 * If there are more than one registered components of the same type and one of them are
 * registered with a Class name key of the corresponding type, this component
 * will take precedence over other components during type resolution.
 * </p>
 * 
 * @author Java version authors
 * @author Pawel Kozlowski
 * @version $Revision$
 */
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
    	if (array_key_exists($componentKey,$this->_componentAdapters))
    	{
        	return $this->_componentAdapters[$componentKey];
    	}
    	else
    	{
    		return null;
    	}
    }
    
    
    public function getComponentAdaptersOfType($componentType) 
    {
        $result = array();
        
        $ctReflectionClass = new ReflectionClass($componentType);                                        
                
        foreach($this->_componentAdapters as $cadapter)
        {
            if ($ctReflectionClass->isInterface()&&class_exists($cadapter->getComponentImplementation()))
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
                	if (class_exists($cadapter->getComponentImplementation()))
                	{             
	                    $catReflectionClass = new ReflectionClass($cadapter->getComponentImplementation());
	                    if($catReflectionClass->isSubclassOf($ctReflectionClass))
	                    {
	                        $result[] = $cadapter;    
	                    }
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
        if (!array_key_exists($componentAdapter->getComponentKey(),$this->_componentAdapters))
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
    
    public function registerComponentImplementationWithIncFileName($includeFileName, $componentKey, $componentImplementation = '', $componentParams = array())
    {
    	if ($includeFileName!='')
    	{
    		$ca = $this->_componentAdapterFactory->createComponentAdapter($componentKey, $componentImplementation, $componentParams);
        	$this->registerComponent(new LazyIncludingComponentAdapter($ca,$includeFileName));
    	}
    	else
    	{
    		throw new IncludeFileNameNotDefinedRegistrationException();
    	}
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