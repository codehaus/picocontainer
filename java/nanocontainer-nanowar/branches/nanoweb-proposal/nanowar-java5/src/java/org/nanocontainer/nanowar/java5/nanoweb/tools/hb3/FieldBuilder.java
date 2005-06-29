package org.nanocontainer.nanowar.java5.nanoweb.tools.hb3;

import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.hibernate.cfg.Configuration;
import org.nanocontainer.nanowar.java5.nanoweb.tools.hb3.impl.FieldInfo;

public class FieldBuilder {

    private Configuration cfg;

    public FieldBuilder(Configuration cfg) {
        this.cfg = cfg;
    }

    public String field(Class type, Object bean, String beanFieldName, String formFieldName) throws Exception {
        StringWriter result = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(result);

        FieldInfo fieldInfo = new FieldInfo(cfg, type, bean, beanFieldName);

        if (fieldInfo.column == null) {
            writer.writeStartElement("h1");
            writer.writeCharacters("Field '" + beanFieldName + "' not found.");
            writer.writeEndElement();
            result.close();
            return result.toString();
        }

        field(writer, fieldInfo, formFieldName);

        result.close();
        return result.toString();
    }

    public void field(XMLStreamWriter writer, FieldInfo fieldInfo, String formFieldName) throws XMLStreamException {
        // verify if its an textarea
        if (String.class.isAssignableFrom(fieldInfo.property.getType().getReturnedClass())) {
            if (fieldInfo.column.getLength() > 500) {
                textarea(writer, fieldInfo, formFieldName);
                return;
            }
        }
        
        inputText(writer, fieldInfo, formFieldName);
    }

    private void inputText(XMLStreamWriter writer, FieldInfo fieldInfo, String formFieldName) throws XMLStreamException {
        writer.writeStartElement("input");
        writer.writeAttribute("type", "text");
        writer.writeAttribute("name", formFieldName);
        writer.writeAttribute("class", "__gen");
        writer.writeAttribute("maxlength", Integer.toString(fieldInfo.column.getLength()));
        writer.writeEndElement();
    }

    private void textarea(XMLStreamWriter writer, FieldInfo fieldInfo, String formFieldName) throws XMLStreamException {
        writer.writeStartElement("textarea");
        writer.writeAttribute("name", formFieldName);
        writer.writeAttribute("class", "__gen");

        if (fieldInfo.column.getLength() < 100000) {
            writer.writeAttribute("maxlength", Integer.toString(fieldInfo.column.getLength()));
        }

        writer.writeEndElement();
    }

}
