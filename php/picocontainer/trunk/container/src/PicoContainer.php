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
 * The Standard {@link PicoContainer}/{@link MutablePicoContainer} implementation.
 *
 * Using Class name as keys to the {@link registerComponentImplementation()}
 * method makes a subtle semantic difference:
 *
 * If there are more than one registered components of the same type and one of them are
 * registered with a Class name key of the corresponding type, this component
 * will take precedence over other components during type resolution.
 * 
 * @author Java version authors
 * @author Pawel Kozlowski
 * @version $Revision$
 */
class DefaultPicoContainer implements MutablePicoContainer
{
    private $_componentAdapters = array();
    private $_componentAdapterFactory;
    
    
    /**
     * Handle instantiation
     *
     * @param null|ComponentAdapterFactory
     *    The {@link ComponentAdapterFactory} to utilize while loading 
     *    components.
     */
    public function __construct($componentAdapterFactory = null)
    {
        assert('is_null($componentAdapterFactory) || 
            $componentAdapterFactory instanceof ComponentAdapterFactory');
        if (is_null($componentAdapterFactory))
        {                
            $componentAdapterFactory = new CachingComponentAdapterFactory(
                new ConstructorInjectionComponentAdapterFactory()); 
        }
                
        $this->_componentAdapterFactory = $componentAdapterFactory; 
    }
       
    
    /**
     * Returns an instance of a given component based on the provided
     * <i>$componentKey</i>.
     *
     * Returns <i>null</i> if unable to find component.
     *
     * @see getComponentAdapter()
     * @param string
     * @return object|null
     */
    public function getComponentInstance($componentKey)
    {
        $componentAdapter = $this->getComponentAdapter($componentKey);
        if (!is_null($componentAdapter)) {
            return $componentAdapter->getComponentInstance($this);
        }
    }
    
    
    /**
     * Returns an instance of a given component based on the provided
     * <i>$componentType</i>.
     *
     * Returns <i>null</i> if unable to find component.
     *
     * @see getComponentAdapterOfType()
     * @param string
     * @return object|null
     */
    public function getComponentInstanceOfType($componentType)
    {
        $componentAdapter = $this->getComponentAdapterOfType($componentType);
        if (!is_null($componentAdapter)) {
            return $componentAdapter->getComponentInstance($this);
        }
    }
    
    
    /**
     * Returns a component adapater based on the provide <i>$componentKey</i>.
     *
     * @param string
     * @return string|null
     */
    public function getComponentAdapter($componentKey)
    {
        if (array_key_exists($componentKey, $this->_componentAdapters))
        {
            return $this->_componentAdapters[$componentKey];
        }
    }
    
    
    /**
     * Returns an array of component adapters based on the provided 
     * <i>$componentType</i>.
     *
     * @param array
     */
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
    
    
    /**
     * Returns a specific component adapter based on the type
     *
     * If multiple component adapters are found, this will throw an
     * {@link AmbiguousComponentResolutionException}.
     *
     * @param string
     * @param object|null
     */
    public function getComponentAdapterOfType($expectedType)
    {
    	   $adapterByKey = $this->getComponentAdapter($expectedType);
        if (!is_null($adapterByKey)) {
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
    
    
    /**
     * Register a new component adapter
     *
     * @see ComponentAdapter
     * @param ComponentAdapter
     */
    public function registerComponent(ComponentAdapter $componentAdapter)
    {                        
        if (array_key_exists($componentAdapter->getComponentKey(),$this->_componentAdapters))
        {
            throw new DuplicateComponentKeyRegistrationException($componentAdapter->getComponentKey());
        }
        $this->_componentAdapters[$componentAdapter->getComponentKey()] = $componentAdapter;
    }
    
    
    /**
     * Register a new component implementation
     *
     * @see registerComponent()
     * @param string
     * @param string
     * @param array
     */
    public function registerComponentImplementation($componentKey, $componentImplementation = '', $componentParams = array())
    {                
        $ca = $this->_createComponentAdapter($componentKey, $componentImplementation, $componentParams);
        $this->registerComponent($ca);        
    }
    
    
    /**
     * Register a new component implementation for lazy loading
     *
     * @see registerComponent()
     * @param string
     * @param string
     * @param string
     * @param array
     */
    public function registerComponentImplementationWithIncFileName($includeFileName, $componentKey, $componentImplementation = '', $componentParams = array())
    {
        if (empty($includeFileName)) {
            throw new IncludeFileNameNotDefinedRegistrationException();
        }
        
    		$ca = $this->_createComponentAdapter($componentKey, $componentImplementation, $componentParams);
        	$this->registerComponent(new LazyIncludingComponentAdapter($ca,$includeFileName));
    }
    
    /**
     * Register a specific instance of a class
     *
     * @param object
     * @param string|null
     */
    public function registerComponentInstance($componentInstance, $componentKey = null)
    {
        if (!is_object($componentInstance)) {
            throw new PicoRegistrationException('$componentInstance is not an object');
        }
        $this->registerComponent(new InstanceComponentAdapter($componentInstance, $componentKey));    
    }
    
    
    /**
     * Removes a registered component based on the provided 
     * <i>$componentKey</i>.
     *
     * @param string
     */
    public function unregisterComponent($componentKey)
    {
        if (isset($this->_componentAdapters[$componentKey]))
        {
            unset($this->_componentAdapters[$componentKey]);
        }
    }
    
    
    /**
     * Used to create a component adapter using the internal 
     * {@link $_componentAdapterFactory}.
     *
     * @param string
     * @param string
     * @param array
     * @return ComponentAdapter
     */
    private function _createComponentAdapter($key, $implementation, $params) {
        return $this->_componentAdapterFactory->createComponentAdapter($key, $implementation, $params);
    }
}   

?>