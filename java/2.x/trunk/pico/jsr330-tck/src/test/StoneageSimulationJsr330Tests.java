import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.FuelTank;
import org.atinject.tck.auto.Seat;
import org.atinject.tck.auto.Tire;
import org.atinject.tck.auto.accessories.Cupholder;
import org.atinject.tck.auto.accessories.SpareTire;
import org.picocontainer.DefaultPicoContainer;

import javax.inject.Named;
import javax.inject.Provider;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class StoneageSimulationJsr330Tests {
    public static Test suite() throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException {
        Constructor<?> seatCtor = Seat.class.getDeclaredConstructors()[0];
        seatCtor.setAccessible(true);

        final Seat[] plainSeat = new Seat[1];

        Provider<Seat> plainSeatProvider = new Provider<Seat>() {
            public Seat get() {
                return plainSeat[0];
            }
        };

        Cupholder cupholder = new Cupholder(plainSeatProvider);
        plainSeat[0] = (Seat) seatCtor.newInstance(cupholder);
        final Seat driversSeat = (Seat) seatCtor.newInstance(cupholder);

        FuelTank fuelTank = new FuelTank();

        final Tire plainTire = new Tire(fuelTank);

        final SpareTire spareTire = new SpareTire(fuelTank, new FuelTank());

        Provider<Seat> driversSeatProvider = new Provider<Seat>() {
            public Seat get() {
                return driversSeat;
            }
        };

        Provider<Tire> plainTireProvider = new Provider<Tire>() {
            public Tire get() {
                return plainTire;
            }
        };

        Provider<Tire> spareTireProvider = new Provider<Tire>() {
            public Tire get() {
                return spareTire;
            }
        };

        Constructor<?> convertibelCtor = null;
        Constructor<?>[] convertibelCtors = Convertible.class.getDeclaredConstructors();
        for (int i = 0; i < convertibelCtors.length; i++) {
            convertibelCtor = convertibelCtors[i];
            if (convertibelCtor.getParameterTypes().length >0) {
                break;
            }
        }
        convertibelCtor.setAccessible(true);

        System.out.println("l" + convertibelCtor.getParameterTypes().length);
        Car car = (Car) convertibelCtor.newInstance(
                plainSeat[0],
                driversSeat,
                plainTire,
                spareTire,
                plainSeatProvider,
                driversSeatProvider,
                plainTireProvider,
                spareTireProvider);

        return Tck.testsFor(car, true, true);
    }
}