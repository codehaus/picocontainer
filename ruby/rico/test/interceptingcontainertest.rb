require 'test/unit'
require 'rico/interceptingcontainer'

class InterceptingContainerTest < Test::Unit::TestCase
  include Rico
  
  
  def setup
    $results = []
  end
  
  class Pusher
    def push_own_result
      $results.push "in method"
    end
  end

  def test_container_intercepts_entry_to_component_method_call
    entry_proc = proc { $results.push "at entry" }
    # create an intercepting container that does something
    rico = InterceptingContainer.new entry_proc
    
    # register a component
    rico.register_component :pusher, Pusher
    assert_equal Pusher, rico.component(:pusher).type
    
    # call a method on the component
    rico.component(:pusher).push_own_result
    
    # assert that it was intercepted before the method ran
    assert_equal 2, $results.length, "array length"
    assert_equal "in method", $results.pop
    assert_equal "at entry", $results.pop
  end 
end
