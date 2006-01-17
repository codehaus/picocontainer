package org.picocontainer.testmodel;

/**
 *
 * @author greg
 * @author $Author: $ (last edit)
 * @version $Revision: $
 */
public class CoupleBean {
    private PersonBean personA;
    private PersonBean personB;

    public CoupleBean(PersonBean a, PersonBean b) {
        this.personA = a;
        this.personB = b;
    }

    public PersonBean getPersonA() {
        return personA;
    }

    public PersonBean getPersonB() {
        return personB;
    }
}
