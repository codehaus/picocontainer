require 'rico/container'
require 'rico/test/model'

module Rico
=begin

  (Should) test all the methods in Rico container. Based on the TCK test suite
  from the Java PicoContainer.

  Mix into a TestCase containing a create_container() method

  Author: Dan North based on Aslak Hellesoy's tests
=end
  module ContainerTestCase

    def create_container()
        fail "need to implement create_container"
    end
  
    def create_container_with_touchable_and_dependency()
      rico = create_container
      rico.register_component_implementation Touchable, SimpleTouchable
      rico.register_component_implementation DependsOnTouchable, DependsOnTouchable, [ Touchable ]
      return rico
    end
    private :create_container_with_touchable_and_dependency
    
    def create_container_with_depends_on_touchable_only()
      rico = create_container
      rico.register_component_implementation DependsOnTouchable, DependsOnTouchable, [ Touchable ]
      return rico
    end
    private :create_container_with_depends_on_touchable_only
    
    def test_new_container_is_not_nil
      assert_not_nil create_container_with_touchable_and_dependency()
    end
    
    def test_registered_components_exist_and_are_the_correct_types
      rico = create_container_with_touchable_and_dependency
      assert rico.has_component?(Touchable), "should have :touchable component"
      assert rico.has_component?(DependsOnTouchable)
      assert_kind_of Touchable, rico.component_instance(Touchable)
    end

    def test_registers_single_instance
      rico = create_container
      a = Array.new
      rico.register_component_instance a
      assert_same a, rico.component_instance(Array)
    end
    
    def test_container_is_serializable
      rico = create_container_with_touchable_and_dependency()
      loadedRico = Marshal.load Marshal.dump(rico)
      
      depends_on_touchable = loadedRico.component_instance DependsOnTouchable
      assert_kind_of DependsOnTouchable, depends_on_touchable
      
      touchable = loadedRico.component_instance Touchable
      assert_equal true, touchable.was_touched
    end
    
    def test_getting_component_with_missing_dependency_fails
      assert_raises NoSatisfiableConstructorsError do
        rico = create_container_with_depends_on_touchable_only
        rico.component_instance DependsOnTouchable
      end
    end
  
    def test_getting_same_component_twice_gives_same_component
      rico = create_container
      rico.register_component_implementation Object, Object
      assert_same(
        rico.component_instance(Object),
        rico.component_instance(Object)
      )
    end

    def test_registering_component_with_duplicate_key_fails
      begin
        rico = create_container
        2.times { rico.register_component_implementation Object, Object }
      rescue DuplicateComponentKeyRegistrationError => e
        assert_equal Object, e.duplicate_key
      end
    end

    def test_registers_instance
      rico = create_container
      rico.register_component_instance Hash, Hash.new
      assert_equal 1, rico.component_instances.size
      assert_instance_of Hash, rico.component_instance(Hash), "Should contain Hash key -> Hash instance"
    end
    
    def test_getting_component_for_missing_key_falls_back_to_component_type_if_key_is_a_class
      rico = create_container
      rico.register_component_implementation "array", Array
      array = rico.component_instance Array
      assert_instance_of Array, array
    end

    def test_ambiguous_resolution_with_registered_instance_and_implementation_fails
      rico = create_container
      rico.register_component_implementation "string implementation key", String
      rico.register_component_instance "string instance key", "string instance"
      assert_raises AmbiguousComponentResolutionError do
        rico.component_instance(String)
      end
    end

    def test_fails_if_unable_to_resolve_component_instance
      rico = create_container
      assert_raises UnresolvableComponentError do
        rico.component_instance(Object)
      end
    end

    class A
      def initialize(b,c);end
    end
    class B; end
    class C; end

    def test_unsatisfied_components_error_gives_verbose_enough_error_message
      rico = create_container
      rico.register_component_implementation A, A, [B, C]
      rico.register_component_implementation B, B
      begin
        puts rico.component_instance(A)
        flunk "Should have raised an exception"
      rescue NoSatisfiableConstructorsError => e
        assert_equal A.name + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + C.name + "]", e.message
      end
    end

#    public static class A {
#        public A(B b, C c){}
#    }
#    public static class B {}
#    public static class C {}
#
#    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
#        MutablePicoContainer pico = createPicoContainer();
#        pico.registerComponentImplementation(A.class);
#        pico.registerComponentImplementation(B.class);
#
#        try {
#            pico.getComponentInstance(A.class);
#        } catch (NoSatisfiableConstructorsException e) {
#            assertEquals( A.class.getName() + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + C.class.getName() + "]", e.getMessage() );
#            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
#            assertEquals(1, unsatisfiableDependencies.size());
#            assertTrue(unsatisfiableDependencies.contains(C.class));
#        }
#    }

  end
end

#    public static class ListAdder {
#        public ListAdder(Collection list) {
#            list.add("something");
#        }
#    }
#
#    public void TODOtestMulticasterResolution() throws PicoRegistrationException, PicoInitializationException {
#        MutablePicoContainer pico = createPicoContainer();
#
#        pico.registerComponentImplementation(ListAdder.class);
#        pico.registerComponentImplementation("a", ArrayList.class);
#        pico.registerComponentImplementation("l", LinkedList.class);
#
#        pico.getComponentInstance(ListAdder.class);
#
#        List a = (List) pico.getComponentInstance("a");
#        assertTrue(a.contains("something"));
#
#        List l = (List) pico.getComponentInstance("l");
#        assertTrue(l.contains("something"));
#    }
#
#    public static class A {
#        public A(B b, C c){}
#    }
#    public static class B {}
#    public static class C {}
#
#    public void testUnsatisfiedComponentsExceptionGivesVerboseEnoughErrorMessage() {
#        MutablePicoContainer pico = createPicoContainer();
#        pico.registerComponentImplementation(A.class);
#        pico.registerComponentImplementation(B.class);
#
#        try {
#            pico.getComponentInstance(A.class);
#        } catch (NoSatisfiableConstructorsException e) {
#            assertEquals( A.class.getName() + " doesn't have any satisfiable constructors. Unsatisfiable dependencies: [class " + C.class.getName() + "]", e.getMessage() );
#            Set unsatisfiableDependencies = e.getUnsatisfiableDependencies();
#            assertEquals(1, unsatisfiableDependencies.size());
#            assertTrue(unsatisfiableDependencies.contains(C.class));
#        }
#    }
#
#    public static class D {
#        public D(E e, B b){}
#    }
#    public static class E {
#        public E(D d){}
#    }
#
#    public void testCyclicDependencyThrowsCyclicDependencyException() {
#        MutablePicoContainer pico = createPicoContainer();
#        pico.registerComponentImplementation(B.class);
#        pico.registerComponentImplementation(D.class);
#        pico.registerComponentImplementation(E.class);
#
#        try {
#            pico.getComponentInstance(D.class);
#            fail();
#        } catch (CyclicDependencyException e) {
#            assertEquals("Cyclic dependency: " + D.class.getConstructors()[0].getName() + "(" + E.class.getName() + "," + B.class.getName() + ")", e.getMessage());
#            assertEquals(D.class.getConstructors()[0], e.getConstructor());
#        } catch (StackOverflowError e) {
#            fail();
#        }
#    }
#
#    public static class NeedsTouchable {
#        public Touchable touchable;
#
#        public NeedsTouchable(Touchable touchable) {
#            this.touchable = touchable;
#        }
#    }
#
#    public static class NeedsWashable {
#        public Washable washable;
#
#        public NeedsWashable(Washable washable) {
#            this.washable = washable;
#        }
#    }
#
#    public void testSameInstanceCanBeUsedAsDifferentType() {
#        MutablePicoContainer pico = createPicoContainer();
#        pico.registerComponentImplementation("wt", WashableTouchable.class);
#        pico.registerComponentImplementation("nw", NeedsWashable.class);
#        pico.registerComponentImplementation("nt", NeedsTouchable.class);
#
#        NeedsWashable nw = (NeedsWashable) pico.getComponentInstance("nw");
#        NeedsTouchable nt = (NeedsTouchable) pico.getComponentInstance("nt");
#        assertSame(nw.washable, nt.touchable);
#    }
#
#    public void testRegisterComponentWithObjectBadType() throws PicoIntrospectionException {
#        MutablePicoContainer pico = createPicoContainer();
#
#        try {
#            pico.registerComponentInstance(Serializable.class, new Object());
#            fail("Shouldn't be able to register an Object.class as Serializable because it is not, " +
#                    "it does not implement it, Object.class does not implement much.");
#        } catch (AssignabilityRegistrationException e) {
#        }
#
#    }
#
#    public static class JMSService {
#        public final String serverid;
#        public final String path;
#
#        public JMSService(String serverid, String path) {
#            this.serverid = serverid;
#            this.path = path;
#        }
#    }
#    // http://jira.codehaus.org/secure/ViewIssue.jspa?key=PICO-52
#    public void testPico52() {
#        MutablePicoContainer pico = createPicoContainer();
#
#        pico.registerComponentImplementation("foo", JMSService.class, new Parameter[] {
#            new ConstantParameter("0"),
#            new ConstantParameter("something"),
#        });
#        JMSService jms = (JMSService) pico.getComponentInstance("foo");
#        assertEquals("0", jms.serverid);
#        assertEquals("something", jms.path);
#    }
#
#    public void testChildContainerCanBeAddedAndRemoved() {
#        MutablePicoContainer parent = createPicoContainer();
#        MutablePicoContainer child = createPicoContainer();
#
#        assertTrue(parent.addChild(child));
#        assertFalse(parent.addChild(child));
#        assertTrue(child.getParentContainers().contains(parent));
#        assertTrue(parent.getChildContainers().contains(child));
#
#        assertTrue(parent.removeChild(child));
#        assertFalse(parent.removeChild(child));
#        assertFalse(child.getParentContainers().contains(parent));
#        assertFalse(parent.getChildContainers().contains(child));
#
#    }
#
#    public void testParentContainerCanBeAddedAndRemoved() {
#        MutablePicoContainer parent = createPicoContainer();
#        MutablePicoContainer child = createPicoContainer();
#
#        assertTrue(child.addParent(parent));
#        assertFalse(child.addParent(parent));
#        assertTrue(child.getParentContainers().contains(parent));
#        assertTrue(parent.getChildContainers().contains(child));
#
#        assertTrue(child.removeParent(parent));
#        assertFalse(child.removeParent(parent));
#        assertFalse(child.getParentContainers().contains(parent));
#        assertFalse(parent.getChildContainers().contains(child));
#
#    }
#}
