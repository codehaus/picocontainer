require 'test/unit'
require 'rico/interceptingcontainer'

class InterceptingContainerTest < Test::Unit::TestCase
  include Rico
  
  def setup
    $results = []
  end
  
  class Pusher
    def push_own_result
      $results << "in method"
    end
  end

  def test_intercepts_entry_to_component_method_call
    
    # create an intercepting container that does something
    rico = InterceptingContainer.new Interceptor, proc { $results << "at entry" }
    
    # register a component
    rico.register_component_implementation :pusher, Pusher
    
    # call a method on the component
    rico.component_instance(:pusher).push_own_result
    
    # assert that it was intercepted before the method ran
    assert_equal 2, $results.length, "array length"
    assert_equal "at entry", $results[0]
    assert_equal "in method", $results[1]
  end
  
  def test_intercepted_component_pretends_to_have_original_components_class
    rico = InterceptingContainer.new Interceptor, proc {}
    rico.register_component_implementation :object, Object
    assert_equal Object, rico.component_instance(:object).class
  end
end
