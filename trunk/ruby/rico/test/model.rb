# Contains all the (simple) test classes for the test model
module Rico

	class Touchable
		def touch
		end
	end

	class SimpleTouchable < Touchable
		attr_reader :was_touched
		
		def initialize
			@was_touched = false
		end
		
		def touch
			@was_touched = true
		end
	end
	
	class DependsOnTouchable
		attr_reader :touchable
		
		def initialize(touchable)
			@touchable = touchable
			@touchable.touch
		end
	end
end