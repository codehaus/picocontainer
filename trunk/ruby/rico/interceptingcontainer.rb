require 'rico/container'
require 'rico/componentspecification'

#
# This is a proof-of-concept rather than an industrial strength interceptor
#
module Rico
  #
  # A dynamic proxy - subclass this and extend method_missing to add intercepting
  # behaviour to a container
  #
  # TODO - make this a mixin that calls optional do_before() and do_after() methods.
  #
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
  
  #
  # Wraps a component in an Interceptor
  #
  class InterceptingComponentSpecification < ComponentSpecification
    def initialize(component_class, dependencies, create_method, interceptor_class, entry_block)
      super component_class, dependencies, create_method
      @interceptor_class, @entry_block = interceptor_class, entry_block
    end
    
    def create_component_instance(container)
      return @interceptor_class.new(super, @entry_block)
    end
  end
  
  #
  # Container that provides method interception, which is a first step towards aspects
  #
  class InterceptingContainer < ChainedContainer
    def initialize interceptor_class, entry_proc
      super()
      @interceptor_class, @entry_proc = interceptor_class, entry_proc
    end
    
    def create_component_specification(component_class, dependencies, create_method)
      return InterceptingComponentSpecification.new(component_class, dependencies, create_method, @interceptor_class, @entry_proc)
    end
  end
end
