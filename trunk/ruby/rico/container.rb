require 'rico/exceptions'
require 'rico/componentspecification'
require 'rico/multicaster'


module Rico
  #
  # The base container. You should probably subclass ChainedContainer rather than this
  # one so that different containers can decorate (chain to) one another.
  #
  class Container
    def initialize
      @specs = {}
    end
    
    def component_count
      return @specs.length
    end
    
    def is_empty?
      return component_count == 0
    end
    
    def register_component(key, component_class, dependencies = [], create_method = :new)
      dependencies.collect! { |dep| wrap_constant dep } 
      dependencies.each { |dep| check_key dep }
      @specs[key] = create_component_specification component_class, dependencies, create_method
    end
    
    def component_class(key)
      return spec(key).component_class
    end
    
    def dependencies(key)
      return component_specification(key).dependencies
    end
    
    def component_specification(key)
      return spec(key)
    end
    
    def component(key)
      
      return component_specification(key).component(self)
    end
    
    def multicast(meth, *args)
      @specs.each_key do |key|
        component(key).send(meth, *args) if component(key).respond_to? meth
      end
    end
    
    def multicaster
      return Multicaster.new(self)
    end
    
    protected
    def spec(key)
      return @specs[check_key(key)]
    end
    
    def create_component_specification(component_class, dependencies, create_method)
      return ComponentSpecification.new(component_class, dependencies, create_method)
    end
    
    private
    def check_key(key)
      raise NonexistentComponentError, "Missing component [#{key.to_s}]" unless @specs.has_key? key
      return key
    end
    
    def wrap_constant value
      return value if value.instance_of? Symbol
      
      key = "__key__#{value}".intern
      @specs[key] = ConstantComponentSpecification.new(value)
      return key
    end
  end
end