<?php

class PicoException extends Exception {}

class PicoRegistrationException extends PicoException {}

class DuplicateComponentKeyRegistrationException extends PicoRegistrationException 
{
    private $key;
    
    public function __construct($key)
    {
        $this->key = $key;
    }
    
    public function getDuplicateKey() {
        return $this->key;
    }
}

class PicoIntrospectionException extends PicoException {}

class CyclicDependencyException extends PicoIntrospectionException {}

class UnsatisfiableDependenciesException extends PicoIntrospectionException
{
    private $instantiatingComponentAdapter;
    private $failedDependencies = array();
    
    public function __construct($instantiatingComponentAdapter, $failedDependencies)
    {
        $this->instantiatingComponentAdapter = $instantiatingComponentAdapter;
        $this->failedDependencies = $failedDependencies;
    }
}

class AmbiguousComponentResolutionException extends PicoIntrospectionException 
{
    private $component;
    private $ambiguousDependency;
    private $ambiguousComponentKeys = array();
    
    public function __construct($ambiguousDependency, $componentKeys) 
    {
        $this->ambiguousDependency = $ambiguousDependency;
        $this->ambiguousComponentKeys = $componentKeys;
        
    }
}

?>