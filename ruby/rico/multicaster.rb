require 'rico/container'

module Rico
  class Multicaster
    def initialize(container); @container = container; end
    
    def method_missing(name, *args)
      @container.multicast name, *args
    end
  end
end
