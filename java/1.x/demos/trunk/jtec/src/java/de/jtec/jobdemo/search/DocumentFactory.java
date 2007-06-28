/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;

import de.jtec.jobdemo.beans.BaseEntity;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.AbstractContext;
import java.io.StringWriter;
/**
 * factory for lucene search documents
 *
 * @author    kostik
 * @created   November 30, 2004
 * @version   $Revision$
 */
public class DocumentFactory {
    /**
     * Description of the Field
     */
    public final static String ID = "id";
    /**
     * Description of the Field
     */
    public final static String UID = "uid";
    /**
     * Description of the Field
     */
    public final static String CLASS = "class";
    /**
     * Description of the Field
     */
    public final static String FULL_TEXT = "full_text";
    final static String KEY = "instance";
    final static Object[] KEYS = new Object[]{KEY};
    private VelocityEngine _velocityEngine;


    /**
     * Constructor for the DocumentFactory object
     *
     * @param engine  Description of Parameter
     */
    public DocumentFactory(VelocityEngine engine) {
        setVelocityEngine(engine);
    }


    /**
     * Gets the VelocityEngine attribute of the DocumentFactory object
     *
     * @return   The VelocityEngine value
     */
    public VelocityEngine getVelocityEngine() {
        return _velocityEngine;
    }


    /**
     * Sets the VelocityEngine attribute of the DocumentFactory object
     *
     * @param velocityEngine  The new VelocityEngine value
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        _velocityEngine = velocityEngine;
    }


    /**
     * create document from entity
     *
     * @param entity         Description of Parameter
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public Document createDocument(BaseEntity entity) throws Exception {
        Document doc = new Document();
        doc.add(Field.Keyword(ID, entity.getId().toString()));
        doc.add(Field.Keyword(CLASS, entity.getClass().getName()));
        doc.add(Field.Keyword(UID, entity.getClass().getName() + "|" + entity.getId().toString()));
        StringWriter content = new StringWriter();
        getVelocityEngine().mergeTemplate(entity.getClass().getName().replace('.', '/') + ".vm", new RenderContext(entity), content);
        doc.add(Field.UnStored(FULL_TEXT, content.toString().trim()));
        return doc;
    }


    /**
     * read only context providing just the object to be rendered
     *
     * @author    kostik
     * @created   November 30, 2004
     * @version   $Revision$
     */
    class RenderContext extends AbstractContext {
        /**
         * Description of the Field
         */
        Object      _value;


        /**
         * Constructor for the RenderContext object
         *
         * @param value  Description of Parameter
         */
        RenderContext(Object value) {
            super();
            _value = value;
        }


        /**
         * we have only one object.
         *
         * @param key  Description of Parameter
         * @return     Description of the Returned Value
         */
        public Object internalGet(String key) {
            if (KEY.equals(key)) {
                return _value;
            }
            return null;
        }


        /**
         * do nothing.
         *
         * @param key  Description of Parameter
         * @return     Description of the Returned Value
         */
        public Object internalRemove(Object key) {
            return null;
        }


        /**
         * DOCUMENT METHOD
         *
         * @return   Description of the Returned Value
         */
        public Object[] internalGetKeys() {
            return KEYS;
        }


        /**
         * we have only one key...
         *
         * @param key  Description of Parameter
         * @return     Description of the Returned Value
         */
        public boolean internalContainsKey(Object key) {
            return KEY.equals(key);
        }


        /**
         * DOCUMENT METHOD
         *
         * @param key    Description of Parameter
         * @param value  Description of Parameter
         * @return       Description of the Returned Value
         */
        public Object internalPut(String key, Object value) {
            return null;
        }
    }
}
