=begin
  Errors raised by Rico
  Author: Dan North
=end
module Rico
  class UnresolvableComponentError < StandardError; end
  
  class AmbiguousComponentResolutionError < StandardError; end
  
  class NoSatisfiableConstructorsError < StandardError; end
  
  class DuplicateComponentKeyRegistrationError < StandardError
    attr_reader :duplicate_key
    def initialize(duplicate_key)
      @duplicate_key = duplicate_key
    end
  end
  
  class CyclicDependencyError < StandardError; end
end