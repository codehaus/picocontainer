package picocontainer.extras;

import junit.framework.TestCase;
import picocontainer.PicoInitializationException;
import picocontainer.defaults.AssignabilityRegistrationException;
import picocontainer.defaults.DefaultPicoContainer;
import picocontainer.defaults.DuplicateComponentTypeRegistrationException;
import picocontainer.defaults.NotConcreteRegistrationException;

public class BeanStyleComponentFactoryTestCase extends TestCase {

    public static interface Dog {
        Man getOwner();
    }

    public static interface Man {
    }

    public static class DogImpl implements Dog {
        private Man man;

        public void setOwner(Man man) {
            this.man = man;
        }

        public Man getOwner() {
            return man;
        }
    }

    public static class ManImpl implements Man {
    }

    public void testCreateComponent() throws NoSuchMethodException, PicoInitializationException {
        BeanStyleComponentFactory cf = new BeanStyleComponentFactory();

        Man man = new ManImpl();
        Dog dog = (Dog) cf.createComponent(Dog.class, DogImpl.class, new Class[]{Man.class}, new Object[]{man});
        assertSame(man, dog.getOwner());
    }

    public void testWithContainer() throws AssignabilityRegistrationException,
            DuplicateComponentTypeRegistrationException, NotConcreteRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer(new BeanStyleComponentFactory());

        pico.registerComponent(Dog.class, DogImpl.class);
        pico.registerComponent(ManImpl.class);

        pico.instantiateComponents();

        Man man = (Man) pico.getComponent(ManImpl.class);
        Dog dog = (Dog) pico.getComponent(Dog.class);

        //assertSame(wife,husband.getWife());
        assertSame(man, dog.getOwner());
    }
}
