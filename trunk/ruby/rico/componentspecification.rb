module Rico
=begin
  Specification for an individual component. Also responsible for generating
  and delivering the component itself.
  
  Author: Dan North
=end
  class ComponentSpecification
    attr_reader :component_class, :dependencies
    
    def initialize(component_class, dependencies, create_method)
      @component_class, @dependencies, @create_method = component_class, dependencies, create_method
    end
    
    def create_component_instance(container)
        assert_create_method_is_accessible # calling by reflection doesn't check access
        args = @dependencies.collect { |dep|
          begin
            container.component_instance(dep)
          rescue UnresolvableComponentError
            raise NoSatisfiableConstructorsError, component_class.name + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + dep.name + "]"
          end
        }
        return @component_instance = @component_class.send(@create_method, *args)
    end
    
    def component_instance(container)
    	return @component_instance || create_component_instance(container)
    end
    
    private
    def assert_create_method_is_accessible
      raise NoMethodError, "undefined method `#{@create_method.to_s}' for #{@component_class.to_s}" \
        unless @component_class.public_methods.include? @create_method.to_s
    end
  end
  
  #
  # Lightweight component spec that just holds a constant value
  #
  class ConstantComponentSpecification
    def initialize(value)
      @value = value
    end
    
    def component_instance(container)
      return @value
    end
    
    def component_class
      return @value.class
    end
  end
end