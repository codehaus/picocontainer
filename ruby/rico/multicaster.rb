module Rico
  #
  # Simple dynamic proxy that passes on any method invocations to a container
  #
  class Multicaster
    all_methods = Object.public_instance_methods(true)
    all_methods -= [ "__send__", "__id__" ] # apparently bad news to undefine
    all_methods.each { |m| undef_method m }
  
    def initialize(container)
      @container = container
    end
    
    def method_missing(method, *args)
      @container.multicast method, *args
    end
  end
end
