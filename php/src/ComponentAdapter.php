<?php

interface ComponentAdapter
{
    public function getComponentKey();
    public function getComponentImplementation();
    public function getComponentInstance($container);    
}

interface ComponentAdapterFactory 
{
    public function createComponentAdapter($componentKey,
                                            $componentImplementation,                              
                                            $parameters);    
}

class ConstructorInjectionComponentAdapterFactory implements ComponentAdapterFactory
{
    public function createComponentAdapter($componentKey,
                                            $componentImplementation,                                          
                                            $parameters)
    {
        return new ConstructorInjectionComponentAdapter($componentKey,
                                            $componentImplementation,                                            
                                            $parameters);
    }
}

class DecoratingComponentAdapterFactory implements ComponentAdapterFactory
{
    private $_delegate;
    
    public function __construct($delegate)
    {
        $this->_delegate = $delegate;
    }
    
    public function createComponentAdapter($componentKey,
                                            $componentImplementation,                       
                                            $parameters)
    {
        return  $this->_delegate->createComponentAdapter($componentKey,
                                            $componentImplementation,                                            
                                            $parameters);
    }       
}

class CachingComponentAdapterFactory extends DecoratingComponentAdapterFactory
{    
    
    public function createComponentAdapter($componentKey,
                                            $componentImplementation,                              
                                            $parameters)
    {
        return new CachingComponentAdapter(parent::createComponentAdapter($componentKey,
                                            $componentImplementation,                                            
                                            $parameters));
    }
    
}


abstract class AbstractComponentAdapter implements ComponentAdapter
{
    private $_componentKey;
    private $_componentImplementation;        
    
    public function __construct($componentKey, $componentImplementation = null, $componentParams = array())
    {        
        if ($componentImplementation == null)
        {
            $componentImplementation = $componentKey;
        }               
        
        $this->_componentKey = $componentKey;
        $this->_componentImplementation = $componentImplementation;        
                
    }
    
    public function getComponentKey() 
    {     
        return $this->_componentKey;     
    }
    
    public function getComponentImplementation()
    {
        return $this->_componentImplementation;
    }         
}

abstract class InstantiatingComponentAdapter extends AbstractComponentAdapter 
{
    private $_componentParams;
    
    public function __construct($componentKey, $componentImplementation = null, $componentParams = array())
    {
        parent::__construct($componentKey, $componentImplementation);
        $this->_componentParams = $componentParams;
    }
         
    public function getComponentParams()
    {
        return $this->_componentParams;
    }
}


class ConstructorInjectionComponentAdapter extends InstantiatingComponentAdapter 
{
    private $instantiationGuard = false; 
            
    public function getComponentInstance($container)
    {                                
        
        if ($this->instantiationGuard)
        {
            throw new CyclicDependencyException();
        }
        else
        {
            $this->instantiationGuard = true;
            
            $param_to_pass_to_constr = array();
            $to_eval_params = array();                                                                       
            
            $rc = new ReflectionClass($this->getComponentImplementation());        
            $reflection_constr = $rc->getConstructor();
            
            if ($reflection_constr != null)
            {                                
                $param_to_pass_to_constr = $this->getConstructorArguments($container, $reflection_constr);
            }
           
                                        
            foreach($param_to_pass_to_constr as $k=>$v)
            {           
               $to_eval_params[] = '$param_to_pass_to_constr['.$k.']';
            }
            $to_eval_params_str = join(', ',$to_eval_params);
            $objectclass_to_create = $this->getComponentImplementation();
            $to_eval_params_str = '$rv = new $objectclass_to_create('.$to_eval_params_str.');';
            
            eval($to_eval_params_str);
            
            $this->instantiationGuard = false;
            
            return $rv;
        }                                                 
    }                
    
    protected function getConstructorArguments(PicoContainer $container, ReflectionMethod $reflection_constr) 
    {
        $param_to_pass_to_constr = array();
        $instances_to_pass_to_constr = array();
        
        $params = $this->getComponentParams();
        
             $constr_all_params = $reflection_constr->getParameters();                                               
                
             if (is_array($constr_all_params)&&count($constr_all_params))
             {
                foreach($constr_all_params as $k => $v)
                {                                                                                                                   
                    if ($params[$v->getName()]!=null)
                    {
                        //supplied param - hint                        
                        $param_to_pass_to_constr[$k] = $params[$v->getName()];
                        $instances_to_pass_to_constr[$k] = $param_to_pass_to_constr[$k]->resolveInstance($container, $this, null);                         
                    }
                    else
                    {                                                                                   
                        $param_rc = $v->getClass();          
                        if ($param_rc!=null)
                        {                                                        
                            $param_to_pass_to_constr[$k] = new BasicComponentParameter();                                                        
                            $instances_to_pass_to_constr[$k] = $param_to_pass_to_constr[$k]->resolveInstance($container, $this, $param_rc->getName());                                                                                    
                        }
                        elseif ($v->isDefaultValueAvailable())
                        {
                            $param_to_pass_to_constr[$k] = new ConstantParameter($v->getDefaultValue());                                  
                            $instances_to_pass_to_constr[$k] = $param_to_pass_to_constr[$k]->resolveInstance($container, $this, null);
                        }
                        else
                        {                                                       
                            throw new UnsatisfiableDependenciesException($this, array($v->getName()));
                        }
                    }                                        
                }
             
        }                
        
        return $instances_to_pass_to_constr;
    }                
}

class InstanceComponentAdapter extends AbstractComponentAdapter 
{
    private $_componentInstance;
    
    public function __construct($componentInstance, $componentKey = null)
    {
        if ($componentKey == null)
        {
            $componentKey = get_class($componentInstance);
        }
        
        parent::__construct($componentKey, get_class($componentInstance));
        $this->_componentInstance = $componentInstance;
    }
    
    public function getComponentInstance($container)
    {                
        return $this->_componentInstance; 
    }            
}


class DecoratingComponentAdapter implements ComponentAdapter
{
    private $_delegate;
    
    public function __construct($delegate)
    {
        $this->_delegate = $delegate;                
    }
    
    public function getComponentKey()
    {
        return $this->_delegate->getComponentKey();
    }
    public function getComponentImplementation()
    {
        return $this->_delegate->getComponentImplementation();
    }
    public function getComponentInstance($container)
    {        
        return $this->getDelegate()->getComponentInstance($container);
    }
    
    public function getDelegate()
    {
         return $this->_delegate;
    }
    
}

class CachingComponentAdapter extends DecoratingComponentAdapter
{
    private $_instanceReference;
    
    public function getComponentInstance($container)
    {        
        if ($this->_instanceReference == null)
        {
            $component = $this->getDelegate()->getComponentInstance($container);
            $this->_instanceReference = $component; 
        }
        else
        {
            $component = $this->_instanceReference; 
        }
         
         return $component;
    }   
     
}

?>