require 'rico/container'
require 'rico/componentspecification'

module Rico
  class Interceptor
    undef_method(:type)
    
    def initialize(target, entry_block)
      @target, @entry_block = target, entry_block
    end
    
    def method_missing meth, *args
      @entry_block.call @target
      @target.send(meth, *args)
    end
  end
  
  class InterceptingComponentSpecification < ComponentSpecification
    def initialize(type, dependencies, create_method, entry_block)
      super type, dependencies, create_method
      @entry_block = entry_block
    end
    
    def create_component(container)
      return Interceptor.new(super, @entry_block)
    end
  end
  
  class InterceptingContainer < ChainedContainer
    def initialize entry_proc
      super()
      @entry_proc = entry_proc
    end
    
    def create_component_specification(type, dependencies, create_method)
      return InterceptingComponentSpecification.new(type, dependencies, create_method, @entry_proc)
    end
  end
end
