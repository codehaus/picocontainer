require 'rico/container'

module Rico
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
    
    protected
    def spec(key)
      return @specs[key] || @parent.spec(key)
    end
  end
end