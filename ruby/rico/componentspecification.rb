class ComponentSpecification
  attr_reader :type, :dependencies
  
  def initialize(type, dependencies, create_method)
    @type, @dependencies, @create_method = type, dependencies, create_method
  end
  
  def create_component(container)
      check_access @type, @create_method # calling by reflection doesn't check access
      args = @dependencies.collect { |dep| container.component(dep) }
      @component = @type.send(@create_method, *args)
  end
  
  def component(container)
  	return @component || create_component(container)
  end
  
  private
  def check_access type, meth
    raise NoMethodError, "undefined method `#{meth.to_s}' for #{type.to_s}" unless type.methods.include? meth.to_s
  end
end
