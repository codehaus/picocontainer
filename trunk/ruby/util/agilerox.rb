require 'test/unit'

# Print each TestCase and test method as it is loaded by the interpreter
module Test
  module Unit
    class TestCase
      def TestCase.inherited(sub_id)
        printf "\n%s\n", sub_id.to_s.sub(/^Test/, '').sub(/Test$/, '')
        eval <<-EOM
          def #{sub_id}.method_added(id)
            meth = id.id2name
            printf("- %s\n", meth.sub(/^test_?/, '').gsub(/_/, ' ')) if meth =~ /^test/
          end
        EOM
      end
    end
  end
end

# Load all the tests
ARGV.each { |arg| require arg } if $0 == __FILE__
exit!