require 'test/unit'

require 'rico/container'
require 'rico/exceptions'

=begin
  Author: Dan North
=end
class ContainerTest < Test::Unit::TestCase

  include Rico # so we don't have to keep typing Rico::
  
  # Registering components in the container

  def test_new_container_is_empty
    rico = Container.new
    assert_equal true, rico.is_empty?
  end
  
  def test_registers_component_with_no_dependencies
    rico = Container.new
    rico.register_component_implementation :object, Object
    assert_equal 1, rico.component_count, "component count"
    assert_equal Object, rico.component_class(:object), "component class"
    assert_equal [], rico.dependencies(:object), "dependencies"
  end
  
  def test_container_with_component_is_not_empty
    rico = Container.new
    assert_equal true, rico.is_empty?, "before"
    rico.register_component_implementation :object, Object
    assert_equal false, rico.is_empty?, "after"
  end
  
  def test_get_class_of_nonexistent_component_raises_error
    rico = Container.new
    assert_raises UnresolvableComponentError, "component class for nonexistent key" do
      rico.component_class :nonexistent_key
    end
  end
  
  def test_get_dependencies_for_nonexistent_component_raises_error
    rico = Container.new
    assert_raises UnresolvableComponentError, "dependencies for nonexistent key" do
      rico.dependencies :nonexistent_key
    end
  end
  
  class HasDependents
    attr_reader :needed, :also_needed
    
    def initialize(needed, also_needed)
      @needed, @also_needed = needed, also_needed
    end
  end
  
  class Needed; end
  class AlsoNeeded; end
  
  def build_rico_with_dependent_components
    rico = Container.new
    rico.register_component_implementation :needed, Needed
    rico.register_component_implementation :also_needed, AlsoNeeded
    rico.register_component_implementation :has_dependents, HasDependents, [ :needed, :also_needed ]
    return rico
  end
  private :build_rico_with_dependent_components

  def test_registers_component_with_dependencies
    rico = build_rico_with_dependent_components
    assert_equal 3, rico.component_count, "component count"
    assert_equal HasDependents, rico.component_class(:has_dependents), "component class"
    assert_equal [ :needed, :also_needed ], rico.dependencies(:has_dependents), "dependencies"
  end
  
#  def test_registering_component_with_missing_dependencies_raises_error
#    rico = Container.new
#    assert_raises UnresolvableComponentError do
#       rico.register_component_implementation :object, Object, [ :nonexistent_key ]
#    end
#  end
  
  # Getting things out of the container
  
  def test_gets_component_with_no_dependencies
    rico = Container.new
    rico.register_component_implementation :object, Object
    result = rico.component_instance :object
    assert_instance_of Object, result
  end
  
  def test_getting_same_component_twice_gives_same_component
    rico = Container.new
    rico.register_component_implementation :object, Object
    one = rico.component_instance :object
    two = rico.component_instance :object
    assert_same one, two
  end
    
  def test_gets_component_with_dependencies
    rico = build_rico_with_dependent_components
    component = rico.component_instance(:has_dependents)
    assert_instance_of HasDependents, component
    assert_instance_of Needed, component.needed
    assert_instance_of AlsoNeeded, component.also_needed
  end
  
  class HasNestedDependents
    attr_reader :dependent
    def initialize(dependent)
      @dependent = dependent
    end
  end
    
  def test_gets_component_with_multiple_levels_of_dependencies
    rico = build_rico_with_dependent_components
    rico.register_component_implementation :has_nested_dependents, HasNestedDependents, [ :has_dependents ]
    
    # check top level has dependent
    component = rico.component_instance(:has_nested_dependents)
    assert_instance_of HasNestedDependents, component
    assert_instance_of HasDependents, component.dependent
    
    # check dependent has its own dependents
    dependent = component.dependent
    assert_instance_of Needed, dependent.needed
    assert_instance_of AlsoNeeded, dependent.also_needed
  end
  
  class Common; end # a shared component
  
  class Thing1
    attr_reader :common
    def initialize(common)
      @common = common
    end
  end
  
  class Thing2
    attr_reader :common
    def initialize(common)
      @common = common
    end
  end

  def test_two_components_get_the_same_instance_for_the_same_tag
    rico = Container.new
    rico.register_component_implementation :common, Common
    rico.register_component_implementation :thing1, Thing1, [ :common ]
    rico.register_component_implementation :thing2, Thing2, [ :common ]
    thing1 = rico.component_instance :thing1
    thing2 = rico.component_instance :thing2
    assert_same thing1.common, thing2.common
  end
  
  def test_two_components_get_different_instances_for_different_tags
    rico = Container.new
    rico.register_component_implementation :common1, Common
    rico.register_component_implementation :common2, Common
    rico.register_component_implementation :thing1, Thing1, [ :common1 ]
    rico.register_component_implementation :thing2, Thing2, [ :common2 ]
    thing1 = rico.component_instance :thing1
    thing2 = rico.component_instance :thing2
    assert_not_same thing1.common, thing2.common
  end
  
  class SelfDependent
    def initialize(self_dependent); end
  end
  
  def test_component_cannot_be_dependent_on_itself
    rico = Container.new
    rico.register_component_implementation :self_dependent, SelfDependent, [ :self_dependent ]
    assert_raises CyclicDependencyError do
      rico.component_instance :self_dependent
    end
  end
  
  class A
    def initialize(b); end
  end
  
  class B
    def initialize(a); end
  end
  
  def TODO_test_component_cannot_be_indirectly_dependent_on_itself
    rico = Container.new
    rico.register_component_implementation :a, A, [ :b ]
    rico.register_component_implementation :b, B, [ :a ]
    assert_raises CyclicDependencyError do
      rico.component_instance :a
    end
  end
  
  class Washable
    attr_accessor :washed # creates washed and washed= methods
    def initialize
      @washed = false
    end
  end
  
  class AlsoWashable
    attr_accessor :washed
    def initialize
      @washed = false
    end
  end
  
  def test_multicast_delivers_message_to_multiple_components
    rico = Container.new
    rico.register_component_implementation :washable, Washable
    rico.register_component_implementation :also_washable, AlsoWashable
    
    assert_equal false, rico.component_instance(:washable).washed
    assert_equal false, rico.component_instance(:also_washable).washed
    
    multicaster = rico.multicaster
    multicaster.washed = true # call the washed= method on anything that understands it
    
    assert_equal true, rico.component_instance(:washable).washed
    assert_equal true, rico.component_instance(:also_washable).washed
  end
      
  class DontWashMe
  end
  
  def test_multicast_ignores_irrelevant_component
    rico = Container.new
    rico.register_component_implementation :dont_wash_me, DontWashMe
    multicaster = rico.multicaster
    multicaster.washed = true # call the washed= method on anything that understands it
  end
  
  def test_registers_constant_values
    rico = Container.new
    rico.register_component_implementation :constant, "some value"
    assert_equal "some value", rico.component_instance(:constant)
  end
  
  class ComponentWithDependentAndConstant
    attr_reader :needed, :constant
    
    def initialize needed, constant
      @needed, @constant = needed, constant
    end
  end 
  
  def test_dependencies_can_be_constant_values
    rico = Container.new
    rico.register_component_implementation :needed, Needed
    rico.register_component_implementation :constant, "constant"
    rico.register_component_implementation :component, ComponentWithDependentAndConstant, [ :needed, :constant ]
    component = rico.component_instance(:component)
    assert_instance_of Needed, component.needed
    assert_equal "constant", component.constant
  end

  def test_should_be_yamlable
    rico = Container.new
    rico.register_component_implementation :touchable, SimpleTouchable
    touchable = rico.component_instance(:touchable)
    touchable.touch
    
    rico = YAML::load(YAML::dump(rico))
    touchable = rico.component_instance(:touchable)
    assert(touchable.was_touched)    
  end
end
