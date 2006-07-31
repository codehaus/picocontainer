package org.nanocontainer.script.jruby;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Collections;

import org.jruby.IRuby;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.nanocontainer.integrationkit.PicoCompositionException;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.alternatives.EmptyPicoContainer;

/**
 * The script uses the {@code nanocontainer.rb} script to create an instance of
 * {@link PicoContainer}.
 * There are implicit variables named "$parent" and "$assembly_scope".
 * 
 * @author Nick Sieger
 */
public class JRubyContainerBuilder extends ScriptedContainerBuilder {
    public static final String MARKUP_EXCEPTION_PREFIX = "nanobuilder: ";

    private String script;

    public JRubyContainerBuilder(Reader script, ClassLoader classLoader) {
        super(script, classLoader);        
        this.script = toString( script );
    }

    private String toString(Reader script) {
	int charsRead;
	char[] chars = new char[1024];
	StringWriter writer = new StringWriter();
	try {
	    while ((charsRead = script.read(chars)) != -1) {
		writer.write(chars, 0, charsRead);
	    }
	} catch (IOException e) {
	    throw new RuntimeException("unable to read script from reader", e);
	}
	return writer.toString();
    }

    protected PicoContainer createContainerFromScript(PicoContainer parentContainer, Object assemblyScope) {
        if (parentContainer == null) {
            parentContainer = new EmptyPicoContainer();
        }
        parentContainer = new DefaultNanoPicoContainer(getClassLoader(), parentContainer);

        IRuby ruby = JavaEmbedUtils.initialize(Collections.EMPTY_LIST);
        ruby.getLoadService().require("org/nanocontainer/script/jruby/nanobuilder");
        ruby.defineReadonlyVariable("$parent", JavaEmbedUtils.javaToRuby(ruby, parentContainer));
        ruby.defineReadonlyVariable("$assembly_scope", JavaEmbedUtils.javaToRuby(ruby, assemblyScope));
        try {
            IRubyObject result = ruby.evalScript(script);
            return (PicoContainer) JavaEmbedUtils.rubyToJava(ruby, result, PicoContainer.class);
        } catch (RaiseException re) {
            String message = (String) JavaEmbedUtils.rubyToJava(ruby, re.getException().message, String.class);
            if (message.startsWith(MARKUP_EXCEPTION_PREFIX)) {
                throw new NanoContainerMarkupException(message.substring(MARKUP_EXCEPTION_PREFIX.length()));
            } else {
                throw new PicoCompositionException(message, re);
            }
        }
    }
}
