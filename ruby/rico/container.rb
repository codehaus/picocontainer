require 'rico/exceptions'
require 'rico/componentspecification'

module Rico
  module RunState
    attr_reader :run_state
    
    def initialize(run_state = :stopped)
      @run_state = run_state
    end
    
    def started?; @run_state == :started; end
    def stopped?; @run_state == :stopped; end
    
    def start
      @run_state = :started
      do_start
    end
    
    def stop
   	  @run_state = :stopped
   	  do_stop
   	end
  end
  
  class Container
    def initialize
      @specs = {}
    end
    
    def component_count
      return @specs.length
    end
    
    def register_component(key, type, dependencies = [], create_method = :new)
      dependencies.each { |dep| check_key dep }
      @specs[key] = ComponentSpecification.new type, dependencies, create_method
    end
    
    def component_class(key)
      check_key key
      @specs[key].type
    end
    
    def dependencies(key)
      return component_specification(key).dependencies
    end
    
    def component_specification(key)
      return @specs[check_key(key)]
    end
    
    def component(key)
      return component_specification(key).component(self)
    end
    
    def multicast(method, *args)
      @specs.each_key do |key|
        component(key).send(method, *args) if component(key).respond_to? method
      end
    end
    
    private
    def check_key(key)
      raise NonexistentComponentError, "Missing component #{key.to_s}" unless @specs.has_key? key
      key
    end
  end
end

