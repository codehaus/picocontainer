require 'test/unit'

# Print each TestCase and test method as it is loaded by the interpreter
module Test
  module Unit
    class TestCase
      def TestCase.inherited(subclass) # intercept test cases
        printf "\n%s\n", subclass.to_s.sub(/^Test/, '').sub(/Test$/, '')
        eval <<-EOM
          def #{subclass}.method_added(id) # intercept test methods
            meth = id.to_s
            printf("- %s\n", meth.sub(/^test_?/, '').gsub(/_/, ' ')) if meth =~ /^test/
          end
        EOM
      end
    end
  end
end

# Load all the tests
ARGV.each { |src_file| require src_file } if $0 == __FILE__
exit! # avoid running tests at exit
