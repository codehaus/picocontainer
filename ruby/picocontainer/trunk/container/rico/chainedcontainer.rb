require 'rico/container'

module Rico
=begin
  A simple chained container that implements the GoF Decorator pattern
  Author: Dan North
=end
  class ChainedContainer < Container
  
    def initialize parent = Container.new
      super()
      @parent = parent
    end
    
    def component_count
      return @specs.length + @parent.component_count
    end
    
    def multicast(method, *args)
      super
      @parent.multicast(method, *args)
    end
    
    def component_instance(key)
      begin
        return super
      rescue UnresolvableComponentError
        return @parent.component_instance(key)
      end
    end

  end
end