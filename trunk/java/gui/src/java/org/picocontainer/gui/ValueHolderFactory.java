package org.picocontainer.gui;

import java.beans.FeatureDescriptor;


/**
 * TODO remove this class?
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
public interface ValueHolderFactory {
    ValueHolder createValueHolder(Class parameterClass, FeatureDescriptor featureDescriptor);
}
