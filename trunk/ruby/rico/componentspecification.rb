class ComponentSpecification
  attr_reader :component_class, :dependencies
  
  def initialize(component_class, dependencies, create_method)
    @component_class, @dependencies, @create_method = component_class, dependencies, create_method
  end
  
  def create_component(container)
      check_access # calling by reflection doesn't check access
      args = @dependencies.collect { |dep| container.component(dep) }
      return @component = @component_class.send(@create_method, *args)
  end

  def component(container)
  	return @component || create_component(container)
  end
  
  private
  def check_access
    raise NoMethodError, "undefined method `#{@create_method.to_s}' for #{@component_class.to_s}" \
      unless @component_class.public_methods.include? @create_method.to_s
  end
end

class ConstantComponentSpecification
  def initialize(value)
    @value = value
  end
  
  def component(container)
    return @value
  end
end