/*****************************************************************************
 * Copyright (Cc) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Aslak Hellesoy and Paul Hammant                          *
 *****************************************************************************/

package nanocontainer.testmodel;

import org.apache.commons.digester.Digester;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.StringReader;
import java.io.IOException;

public class DigesterWebServerConfig implements WebServerConfig {

    public DigesterWebServerConfig() throws SAXException, IOException {
        Digester dig = new Digester();
        dig.push(this);
        dig.addBeanPropertySetter("webserver/host", "host");
        dig.addBeanPropertySetter("webserver/port", "port");

        // In a real example, this would be an FileReader (thus external).
        dig.parse(new InputSource(new StringReader(
                "<webserver>" +
                "      <host>Foo</host>" +
                "      <port>123</port>" +
                "</webserver>")));
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private String host;
    private int port;


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
