# Make Class YAMLable
# http://blade.nagaokaut.ac.jp/cgi-bin/scat.rb/ruby/ruby-talk/95432
require 'yaml'

class Class
  def to_yaml( opts = {} )
    YAML::quick_emit( nil, opts ) { |out|
      out << "!ruby/class "
      self.name.to_yaml( :Emitter => out )
    }
  end
end

YAML.add_ruby_type(/^class/) do |type, val|
  subtype, subclass = YAML.read_type_class(type, Class)
  val.split(/::/).inject(Object) { |p, n| p.const_get(n)}
end

module Rico
=begin
  Specification for an individual component. Also responsible for generating
  and delivering the component itself.
  
  Author: Dan North
=end
  class ComponentSpecification
    attr_reader :component_class, :dependencies
    
    def initialize(component_class, dependencies, create_method, attrs = nil)
      @component_class, @dependencies, @create_method, @attrs = component_class, dependencies, create_method, attrs
      @component_instance = nil
    end
    
    def create_component_instance(container, unresolved_keys)
        assert_create_method_is_accessible # calling by reflection doesn't check access
        args = resolve_dependencies container, unresolved_keys
        component_instance = @component_class.send(@create_method, *args)
        
        if(!@attrs.nil?)
          @attrs.each do |attr, value|
            writer = "#{attr}=".intern
            component_instance.send(writer, *value)
          end
        end
        
        return component_instance
    end
    
    def resolve_dependencies(container, unresolved_keys)
      @dependencies.collect do |dep|
        begin
          raise CyclicDependencyError if unresolved_keys.include?(dep)
          container.component_instance(dep)
        rescue UnresolvableComponentError
          raise NoSatisfiableConstructorsError, component_class.name + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + dep.name + "]"
        end
      end
    end
    
    def component_instance(container, key)
      return @component_instance ||= create_component_instance(container, [key])
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
    
    def component_instance(container, key)
      return @value
    end
    
    def component_class
      return @value.class
    end
  end
end