require 'test/unit'
require 'rico/container'
require 'rico/chainedcontainer'

class ChainedContainerTest < Test::Unit::TestCase
  include Rico
  
  def test_chained_container_finds_own_component
    parent = Container.new
    child = ChainedContainer.new parent
    child.register_component :object, Object
    assert_instance_of Object, child.component(:object)
  end
  
  def test_chained_container_finds_parents_component
    parent = Container.new
    parent.register_component :object, Object
    child = ChainedContainer.new parent
    assert_instance_of Object, child.component(:object)
  end
  
  def test_chained_container_counts_parents_components
    parent = Container.new
    parent.register_component :object, Object
    child = ChainedContainer.new parent
    child.register_component :object, Object
    assert_equal 2, child.component_count
  end
  
  class ParentComponent; end
  class ChildComponent; end
  
  def test_child_component_masks_parent_component
    parent = Container.new
    parent.register_component :mask_me, ParentComponent
    child = ChainedContainer.new parent
    child.register_component :mask_me, ChildComponent
    assert_instance_of ChildComponent, child.component(:mask_me)
  end
  
  class Washable
    attr_accessor :washed
  end
  
  def test_chained_container_multicasts_to_parent
    parent = Container.new
    parent.register_component :parent_washable, Washable
    child = ChainedContainer.new parent
    child.register_component :child_washable, Washable
    assert_equal nil, child.component(:parent_washable).washed
    assert_equal nil, child.component(:child_washable).washed
    child.multicast :washed=, true
    assert_equal true, child.component(:parent_washable).washed, "parent component"
    assert_equal true, child.component(:child_washable).washed, "child component"
  end
  
  def test_multiple_levels_of_container
    inner = Container.new
    inner.register_component :inner_washable, Washable
    middle = ChainedContainer.new inner
    middle.register_component :middle_washable, Washable
    outer = ChainedContainer.new middle
    outer.register_component :outer_washable, Washable
    assert_not_nil outer.component(:inner_washable), "chained component should be found"
    outer.multicast :washed=, true
    
  end
end