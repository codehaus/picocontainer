module Rico
  class NonexistentComponentError < RuntimeError; end
  class ContainerNotStartedError < RuntimeError; end
  class ContainerNotStoppedError < RuntimeError; end
end