require 'rico/container'

module Rico
  class Multicaster
    def initialize(container); @container = container; end
    
    def method_missing(method, *args)
      @container.multicast method, *args
    end
  end
end
