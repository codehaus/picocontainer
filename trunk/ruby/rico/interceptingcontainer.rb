require 'rico/container'
require 'rico/componentspecification'

module Rico
  class Interceptor
    all_methods = Object.public_instance_methods(true)
    all_methods -= [ "__send__", "__id__" ] # apparently bad news to undefine
    all_methods.each { |m| undef_method m }
    
    def initialize(target, entry_block)
      @target, @entry_block = target, entry_block
    end
    
    def method_missing meth, *args
      @entry_block.call @target
      @target.send(meth, *args)
    end
  end
  
  class InterceptingComponentSpecification < ComponentSpecification
    def initialize(component_class, dependencies, create_method, entry_block)
      super component_class, dependencies, create_method
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
    
    def create_component_specification(component_class, dependencies, create_method)
      return InterceptingComponentSpecification.new(component_class, dependencies, create_method, @entry_proc)
    end
  end
end
