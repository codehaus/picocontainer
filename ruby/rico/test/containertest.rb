require 'test/unit'
require 'singleton'

require 'rico/container'
require 'rico/exceptions'

class ContainerTest < Test::Unit::TestCase

  include Rico # so we don't have to keep typing Rico::

  def test_new_container_is_empty
    rico = Container.new
    assert_equal 0, rico.component_count, "component count"
  end
  
  def test_new_container_is_stopped
    rico = Container.new
    assert rico.stopped?
  end
  
  def test_get_component_in_stopped_container_fails
    rico = Container.new
    rico.register_component :object, Object
    assert_raises ContainerNotStartedError do
      rico.component :object
    end
  end
  
  def test_get_nonexistent_component_details_fails
    rico = Container.new
    assert_raises NonexistentComponentError, "component class for nonexistent key" do
      rico.component_class :key
    end
    assert_raises NonexistentComponentError, "dependencies for nonexistent key" do
      rico.dependencies :key
    end
  end
  
  def test_register_component_in_started_container_fails
    rico = Container.new
    rico.start
    assert_raises ContainerNotStoppedError do
      rico.register_component :object, Object
    end
  end
    
  def test_register_component_with_no_dependencies
    rico = Container.new
    rico.register_component :object, Object
    assert_equal 1, rico.component_count, "component count"
    assert_equal Object, rico.component_class(:object), "component class"
    assert_equal [], rico.dependencies(:object), "dependencies"
  end
    
  class Dependent
    attr_reader :needed, :also_needed
      
    def initialize needed, also_needed
      @needed, @also_needed = needed, also_needed
    end
  end
  class Needed; end
  class AlsoNeeded; end
  
  def rico_with_dependent_components
    rico = Container.new
    rico.register_component :needed, Needed
    rico.register_component :also_needed, AlsoNeeded
    rico.register_component :dependent, Dependent, [ :needed, :also_needed ]
    rico
  end 

  def test_register_component_with_dependencies
    rico = rico_with_dependent_components
    assert_equal 3, rico.component_count, "component count"
    assert_equal Dependent, rico.component_class(:dependent), "component class"
    assert_equal [ :needed, :also_needed ], rico.dependencies(:dependent), "dependencies"
  end

  def test_register_component_with_missing_dependencies_fails
    rico = Container.new
    assert_raises NonexistentComponentError do
      rico.register_component :key, Object, [ :nonexistent ]
    end
  end
  
  class SimpleComponent; end
  
  def test_get_component_with_no_dependencies
    rico = Container.new
    rico.register_component :component, SimpleComponent
    rico.start
    result = rico.component :component
    assert_equal SimpleComponent, result.class
  end
  
  def test_get_component_twice_gives_same_component
    rico = Container.new
    rico.register_component :component, SimpleComponent
    rico.start
    one = rico.component :component
    two = rico.component :component
    assert_same one, two
  end
    
  def test_get_component_with_dependencies
    rico = rico_with_dependent_components
    rico.start
    dependent = rico.component :dependent
    assert_instance_of Needed, dependent.needed
    assert_instance_of AlsoNeeded, dependent.also_needed
  end
  
  class Higher
    attr_reader :dependent
    def initialize dependent
      @dependent = dependent
    end
  end
    
  def test_get_component_with_multiple_levels_of_dependencies
    rico = rico_with_dependent_components
    rico.register_component :higher, Higher, [ :dependent ]
    rico.start
    
    # check top level has dependent
    higher = rico.component :higher
    assert_instance_of Dependent, higher.dependent
    
    # check dependent has its own dependents
    dependent = higher.dependent
    assert_instance_of Needed, dependent.needed
    assert_instance_of AlsoNeeded, dependent.also_needed
  end
  
  class Thing1
  	attr_reader :common
    def initialize common; @common = common; end
  end
  
  class Thing2
  	attr_reader :common
    def initialize common; @common = common; end
  end
  
  class Common; end

  def test_two_components_get_the_same_instance_for_the_same_tag
    rico = Container.new
    rico.register_component :common, Common
    rico.register_component :thing1, Thing1, [ :common ]
    rico.register_component :thing2, Thing2, [ :common ]
    rico.start
    thing1 = rico.component :thing1
    thing2 = rico.component :thing2
    assert_same thing1.common, thing2.common
  end
  
  def test_two_components_get_different_instances_for_different_tags
    rico = Container.new
    rico.register_component :common1, Common
    rico.register_component :common2, Common
    rico.register_component :thing1, Thing1, [ :common1 ]
    rico.register_component :thing2, Thing2, [ :common2 ]
    rico.start
    thing1 = rico.component :thing1
    thing2 = rico.component :thing2
    assert_not_same thing1.common, thing2.common
  end
  
  class StartableComponent
    include Singleton # so we can check the instance's run state
    include Rico::RunState
    def do_start; end
    def do_stop; end
  end
  
  def test_container_starts_components
    # register startable component
    rico = Container.new
    rico.register_component :startable, StartableComponent, [], :instance
    rico.register_startable :startable, :start, :stop
    
    # assert not started
    assert !StartableComponent.instance.started?, "Component should not be started yet"
    
    # start container
    rico.start
    
    # assert component was started
    assert StartableComponent.instance.started?, "Component should have started"
  end
  
  def test_stop_instances
    rico = Container.new
    rico.register_component :startable, StartableComponent, [], :instance
    rico.register_startable :startable, :start, :stop
    rico.start
    rico.stop
    assert StartableComponent.instance.stopped?, "Component should have stopped"
  end
  
  class StartableWithoutMixin
  	def initialize
  	  @started = false
  	end
  	def actual_start
  		@started = true
  	end
  	def actual_stop
  	    @started = false
  	end
  end
  
  def test_start_and_stop_startable_component_without_mixin
    rico = Container.new
  end
end
