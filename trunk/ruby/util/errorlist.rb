def print_with_children(root, depth = 0)
  printf "%s%s\n", "  " * depth, root
  if $children[root]
    $children[root].each { |child| print_with_children child, depth + 1 if child }
  end
end

$children = Hash.new

ObjectSpace.each_object(Class) do |c|
  next unless c < Exception
  parent = c.ancestors[1].name
  $children[parent] ||= Array.new
  $children[parent] << c.name
  #puts "Added #{c.name} under #{parent}"
end

print_with_children 'Exception'