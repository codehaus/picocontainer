require 'test/unit'

require 'rico/xmlconfigurator'

class XmlConfiguratorTest < Test::Unit::TestCase
  include Rico
  
  def test_empty_config_creates_empty_container
#    rico = XmlConfigurator.new().rico("<rico/>")
#    assert_instance_of Container, rico
  end
end
