require 'rico/exceptions'
require 'rico/componentspecification'
require 'rico/multicaster'

module Rico
=begin
  The base container. You should probably subclass ChainedContainer rather than this
  one so that different containers can decorate (chain to) one another.
  
  Author: Dan North
=end
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
    
    def register_component_implementation(key, component_class = key, dependencies = [], create_method = :new, attrs = {})
      raise DuplicateComponentKeyRegistrationError.new(key) if @specs.has_key? key
      @specs[key] = create_component_specification(component_class, dependencies, create_method, attrs)
    end
    
    def component_class(key)
      assert_key_exists key
      return @specs[key].component_class
    end
    
    def dependencies(key)
      return component_specification(key).dependencies
    end
    
    def component_specification(key)
      assert_key_exists key
      return @specs[key]
    end
    
    def component_instance(key)
      begin
        return component_specification(key).component_instance(self, key)
      rescue UnresolvableComponentError
        raise unless key.instance_of? Class
        return matching_component_instance_by_type(key)
      end
    end
    
    def component_instances
    	return @specs.collect { |key, spec| spec.component_instance(self, key) }
    end

    def has_component?(key)
    	return @specs.has_key?(key)
    end
    
    def register_component_instance(key, instance = key)
    	register_component_implementation key, instance
    end
    
    def multicast(meth, *args)
      @specs.each_key do |key|
        component_instance(key).send(meth, *args) if component_instance(key).respond_to? meth
      end
    end
    
    def multicaster
      return Multicaster.new(self)
    end
    
    # Allow for simple API. See containertest.test_simple_intuituve_api
  	def method_missing(name, *args)
  	  eq = name.to_s.index('=')
  	  key = (eq ? name.to_s[0,eq].intern : name)
  	  
  	  if(!args.empty?)
        register_component_implementation key, *args  
      else
        component_instance key
      end
  	end

  private
    
    def create_component_specification(component_class, dependencies, create_method, attrs)
      if component_class.is_a? Class
        result = ComponentSpecification.new component_class, dependencies, create_method, attrs
      else
        result = ConstantComponentSpecification.new component_class
      end
      return result
    end
    
    def assert_key_exists(key)
      raise UnresolvableComponentError, "Missing component [#{key.to_s}]" unless @specs.has_key? key
      return key
    end
    
    def matching_component_instance_by_type(required_type)
      matches = @specs.values.select { |spec| required_type <= spec.component_class }
      case matches.size
        when 0 then raise UnresolvableComponentError, "Missing component [#{required_type.to_s}]"
        when 1 then return matches[0].component_instance(self, matches[0].component_class)
        else raise AmbiguousComponentResolutionError, "Found #{matches.size} matching components for type #{required_type}"
      end
    end
  end
end