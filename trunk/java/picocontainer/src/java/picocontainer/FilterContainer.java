package picocontainer;

/**
 * TODO:
 * Remove this class. It's a special case of {@link AggregatedContainersContainer}. DOH;
 * However, that class' test can be improved with this class' test.
 * 
 * @author Aslak Hellesoy
 * @version $Revision$
 */
public class FilterContainer implements Container {
    Container toFilterFor;

    public FilterContainer( Container toFilterFor ) {
        if( toFilterFor == null ) {
            throw new NullPointerException("toFilterFor can't be null.");
        }
        this.toFilterFor = toFilterFor;
    }

    public boolean hasComponent(Class componentType) {
        return toFilterFor.hasComponent(componentType);
    }

    public Object getComponent(Class componentType) {
        return toFilterFor.getComponent(componentType);
    }

    public Object[] getComponents() {
        return toFilterFor.getComponents();
    }

    public Class[] getComponentTypes() {
        return toFilterFor.getComponentTypes();
    }

    protected Container getToFilterFor() {
        return toFilterFor;
    }
}
