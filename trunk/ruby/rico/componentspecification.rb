    
class ComponentSpecification
  attr_reader :component, :type, :dependencies
  
  def initialize(type, dependencies, create_method)
    @type, @dependencies, @create_method = type, dependencies, create_method
  end
  
  def create_component(container)
      check_access @type, @create_method # calling by reflection doesn't check access
      @dependencies.each { |dep| container.start_component(dep) }
      args = @dependencies.collect { |dep| container.component(dep) }
      @component = @type.send @create_method, *args
  end
  
  def set_startable(start_method, stop_method)
  	@start_method, @stop_method = start_method, stop_method
  end	
  
  def start_component(container)
  	create_component container unless @component
    @component.send @start_method if @start_method
  end
  
  def stop_component
  	@component.send @stop_method if @stop_method
  end
  
  private
  def check_access type, method
    raise NoMethodError, "undefined method `#{method.to_s}' for #{type.to_s}" unless type.methods.include? method.to_s
  end
end
