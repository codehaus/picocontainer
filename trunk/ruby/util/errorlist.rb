=begin
  Generate a list of all the standard exceptions, because the prag book doesn't have the table.
  Author: Dan North
=end

$children = Hash.new

# put all the exception classes in a hash
ObjectSpace.each_object(Class) do |c|
  next unless c < Exception
  parent = c.ancestors[1].name
  $children[parent] ||= Array.new
  $children[parent] << c.name
  #puts "Added #{c.name} under #{parent}"
end

def print_with_children(root, depth = 0)
  printf "%s - %s:\n", "  " * depth, root
  if $children[root]
    $children[root].each { |child| print_with_children child, depth + 1 if child }
  end
end

print_with_children 'Exception'