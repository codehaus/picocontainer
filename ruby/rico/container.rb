require 'rico/exceptions'
require 'rico/componentspecification'
require 'rico/multicaster'

module Rico
  class Container
    def initialize
      @specs = {}
    end
    
    def component_count
      return @specs.length
    end
    
    def register_component(key, type, dependencies = [], create_method = :new)
      dependencies.each { |dep| check_key dep }
      @specs[key] = create_component_specification type, dependencies, create_method
    end
    
    def component_class(key)
      return spec(key).type
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
    
    def create_component_specification(type, dependencies, create_method)
      return ComponentSpecification.new(type, dependencies, create_method)
    end
    
    private
    def check_key(key)
      raise NonexistentComponentError, "Missing component #{key.to_s}" unless @specs.has_key? key
      key
    end
  end
end