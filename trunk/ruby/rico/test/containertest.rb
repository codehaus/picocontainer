require 'test/unit'

require 'rico'
require 'rico/exceptions'

class ContainerTest < Test::Unit::TestCase

  include Rico # so we don't have to keep typing Rico::

  def test_new_container_is_empty
    rico = Container.new
    assert_equal true, rico.is_empty?
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
  
  def test_registers_component_with_no_dependencies
    rico = Container.new
    rico.register_component :object, Object
    assert_equal false, rico.is_empty?, "is empty"
    assert_equal 1, rico.component_count, "component count"
    assert_equal Object, rico.component_class(:object), "component class"
    assert_equal [], rico.dependencies(:object), "dependencies"
  end
  
  class HasDependents
    attr_reader :needed, :also_needed
    
    def initialize needed, also_needed
      @needed, @also_needed = needed, also_needed
    end
  end
  class Needed; end
  class AlsoNeeded; end
  
  def build_rico_with_dependent_components
    rico = Container.new
    rico.register_component :needed, Needed
    rico.register_component :also_needed, AlsoNeeded
    rico.register_component :has_dependents, HasDependents, [ :needed, :also_needed ]
    return rico
  end

  def test_registers_component_with_dependencies
    rico = build_rico_with_dependent_components
    assert_equal 3, rico.component_count, "component count"
    assert_equal HasDependents, rico.component_class(:has_dependents), "component class"
    assert_equal [ :needed, :also_needed ], rico.dependencies(:has_dependents), "dependencies"
  end
  
  def test_registering_component_with_missing_dependencies_fails
    rico = Container.new
    assert_raises NonexistentComponentError do
       rico.register_component :key, Object, [ :nonexistent ]
    end
  end
  
  def test_gets_component_with_no_dependencies
    rico = Container.new
    rico.register_component :object, Object
    result = rico.component :object
    assert_instance_of Object, result
  end
  
  def test_getting_component_twice_gives_same_component
    rico = Container.new
    rico.register_component :object, Object
    one = rico.component :object
    two = rico.component :object
    assert_same one, two
  end
    
  def test_gets_component_with_dependencies
    rico = build_rico_with_dependent_components
    component = rico.component(:has_dependents)
    assert_instance_of Needed, component.needed
    assert_instance_of AlsoNeeded, component.also_needed
  end
  
  class Higher
    attr_reader :dependent
    def initialize dependent
      @dependent = dependent
    end
  end
    
  def test_gets_component_with_multiple_levels_of_dependencies
    rico = build_rico_with_dependent_components
    rico.register_component :higher, Higher, [ :has_dependents ]
    
    # check top level has dependent
    higher = rico.component :higher
    assert_instance_of HasDependents, higher.dependent
    
    # check dependent has its own dependents
    dependent = higher.dependent
    assert_instance_of Needed, dependent.needed
    assert_instance_of AlsoNeeded, dependent.also_needed
  end
  
  class Common; end # a shared component
  
  class Thing1
  	attr_reader :common
    def initialize common; @common = common; end
  end
  
  class Thing2
  	attr_reader :common
    def initialize common; @common = common; end
  end

  def test_two_components_get_the_same_instance_for_the_same_tag
    rico = Container.new
    rico.register_component :common, Common
    rico.register_component :thing1, Thing1, [ :common ]
    rico.register_component :thing2, Thing2, [ :common ]
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
    thing1 = rico.component :thing1
    thing2 = rico.component :thing2
    assert_not_same thing1.common, thing2.common
  end
  
  class Washable
    attr_accessor :washed # creates washed and washed= methods
    def initialize; @washed = false; end
  end
  
  class AlsoWashable
    attr_accessor :washed
    def initialize; @washed = false; end
  end
  
  class DontWashMe; end
  
  def test_multicast_delivers_message_to_all_components
    rico = Container.new
    rico.register_component :washable, Washable
    rico.register_component :also_washable, AlsoWashable
    rico.register_component :dont_wash_me, DontWashMe
    assert_equal false, rico.component(:washable).washed
    assert_equal false, rico.component(:also_washable).washed
    rico.multicaster.washed = true
    assert_equal true, rico.component(:washable).washed
    assert_equal true, rico.component(:also_washable).washed
  end
  
  def test_registers_constant_values
  	rico = Container.new
  	rico.register_component :constant, "some value"
  	assert_equal "some value", rico.component(:constant)
  end
  
  class ComponentWithDependentAndConstant
    attr_reader :needed, :constant
    
    def initialize needed, constant
      @needed, @constant = needed, constant
    end
  end 
  
  def test_dependencies_can_be_constant_values
    rico = Container.new
    rico.register_component :needed, Needed
    rico.register_component :component, ComponentWithDependentAndConstant, [ :needed, "constant" ]
    component = rico.component(:component)
    assert_instance_of Needed, component.needed
    assert_equal "constant", component.constant
  end
end
