require 'test/unit'
require 'rico/test/tck/containertestcase'
require 'rico/container'

class TckTest < Test::Unit::TestCase
	include Rico::ContainerTestCase
	
	def create_container()
		return Rico::Container.new
	end
end
