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
	include RunState
    
    def initialize
      super
      @specs = {}
    end
    
    def component_count
      return @specs.length
    end
    
    def register_component(key, type, dependencies = [], create_method = :new)
      check_stopped
      @specs.each { |dep| check_key dep }
      @specs[key] = ComponentSpecification.new type, dependencies, create_method
    end
    
    def register_startable(key, start_method, stop_method)
      #check_stopped (need test)
      @specs[key].set_startable start_method, stop_method
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
      check_started
      return component_specification(key).component
    end
    
    def start_component(key)
    	@specs[key].start_component self
    end
    
    private
    def do_start
      @specs.each_value do |spec|
        spec.start_component self
      end
    end
    
    def do_stop
    	@specs.each_value { |s| s.stop_component }
    end
    
    def check_key(key)
      raise NonexistentComponentError, "Missing component #{key.to_s}" unless @specs.has_key? key
      key
    end
    
    def check_started
      raise ContainerNotStartedError unless started?
    end
    
    def check_stopped
      raise ContainerNotStoppedError unless stopped?
    end
  end
end

