require 'rico/exceptions'
require 'rico/componentspecification'
require 'rico/multicaster'


module Rico
  #
  # The base container. You should probably subclass ChainedContainer rather than this
  # one so that different containers can decorate (chain to) one another.
  #  
  class Container
    @@next_const_key = 0
    
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
#      register_constant_dependencies dependencies
      raise DuplicateComponentKeyRegistrationError if @specs.has_key? key
      dependencies.each { |dep| ensure_key_exists dep }
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
      return @specs[ensure_key_exists(key)]
    end
    
    def create_component_specification(component_class, dependencies, create_method)
      if component_class.is_a? Class
        result = ComponentSpecification.new component_class, dependencies, create_method
      else
        result = ConstantComponentSpecification.new component_class
      end
      return result
    end
    
    private
    def ensure_key_exists(key)
      raise NonexistentComponentError, "Missing component [#{key.to_s}]" unless @specs.has_key? key
      return key
    end
    
#    def generate_const_key()
#      key = "__key_#{id}_#{@@next_const_key}__".intern
#      @@next_const_key += 1
#      return key
#    end
#    
#    def register_constant_dependencies(dependencies)
##      dependencies.collect! do |dep|
##        if dep.is_a? Symbol
##          dep
##        else
##          key = generate_const_key()
##          @specs[key] = ConstantComponentSpecification.new(dep)
##          key
##        end
##      end
#    end
    
    # TCK changes
    public
    
    alias register_component_implementation register_component
    alias component_instance component
    
    def has_component?(key)
    	return @specs.has_key?(key)
    end
    
    def register_component_instance(key, instance = key)
    	register_component instance.class, instance
    end
    
    def component_instances
    	return @specs.keys.collect { |key| component_instance key }
    end
  end
end