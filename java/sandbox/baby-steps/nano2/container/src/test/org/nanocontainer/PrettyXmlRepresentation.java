package org.nanocontainer;

import org.picocontainer.MutablePicoContainer;

import java.util.HashSet;
import java.util.ArrayList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;

public class PrettyXmlRepresentation {

    private XStream xs;

    public PrettyXmlRepresentation() {
        xs = new XStream();
        xs.registerConverter(new Converter() {
            public boolean canConvert(Class aClass) {
                return aClass.getName().equals("org.picocontainer.DefaultPicoContainer$OrderedComponentAdapterLifecycleManager") ||
                       aClass.getName().equals("org.picocontainer.DefaultPicoContainer$1") ||
                       aClass.getName().equals("org.picocontainer.ComponentCharacteristics") ||
                       aClass == Boolean.class ||
                       aClass == HashSet.class ||
                       aClass == ArrayList.class;
            }

            public void marshal(Object o, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
            }

            public Object unmarshal(HierarchicalStreamReader hierarchicalStreamReader, UnmarshallingContext unmarshallingContext) {
                return null;
            }
        });
        xs.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);


    }

    public String simplifyRepresentation(MutablePicoContainer mpc) {
        String foo = xs.toXML(mpc);
        foo = foo.replace('$','_');
        foo = foo.replaceAll("/>","");
        foo = foo.replaceAll("</org.nanocontainer.DefaultNanoContainer","");
        foo = foo.replaceAll("</","");
        foo = foo.replaceAll("<","");
        foo = foo.replaceAll(">","");
        foo = foo.replaceAll("\n    childrenStarted","");
        foo = foo.replaceAll("\n    componentAdapters","");
        foo = foo.replaceAll("\n    orderedComponentAdapters","");
        foo = foo.replaceAll("\n    started","");
        foo = foo.replaceAll("\n    disposed","");
        foo = foo.replaceAll("\n    handler","");
        foo = foo.replaceAll("\n    children","");
        foo = foo.replaceAll("\n  namedChildContainers","");
        foo = foo.replaceAll("\n  delegate\n","\n");
        foo = foo.replaceAll("\n    delegate\n","\n");
        foo = foo.replaceAll("\n      delegate\n","\n");
        foo = foo.replaceAll("\n    componentCharacteristic class=\"org.picocontainer.DefaultPicoContainer$1\"","");
        foo = foo.replaceAll("\n    componentCharacteristics","");
        foo = foo.replaceAll("\n    componentKeyToAdapterCache","");
        foo = foo.replaceAll("\n    startedComponentAdapters","");
        foo = foo.replaceAll("\"class=","\"\nclass=");
        foo = foo.replaceAll("\n    componentFactory\n","\n");
        foo = foo.replaceAll("\n    componentMonitor\n","\n");
        foo = foo.replaceAll("\n    lifecycleManager","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_1\"","");
        foo = foo.replaceAll("class=\"org.picocontainer.DefaultPicoContainer_OrderedComponentAdapterLifecycleManager\"","");
        foo = foo.replaceAll("class=","=");
        foo = foo.replaceAll("\"","");
        foo = foo.replaceAll(" \n","\n");
        foo = foo.replaceAll(" =","=");
        foo = foo.replaceAll("\n\n","\n");

        return foo;
    }



}
