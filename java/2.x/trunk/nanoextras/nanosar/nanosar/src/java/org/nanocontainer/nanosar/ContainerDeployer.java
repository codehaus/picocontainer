package org.nanocontainer.nanosar;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.script.ScriptBuilderResolver;
import org.nanocontainer.script.ScriptedContainerBuilderFactory;

/**
 * mbean for deployment in JBoss SAR
 * 
 * @author ko5tik
 * 
 */
public class ContainerDeployer implements ContainerDeployerMBean {

	Log log;

	String containerComposer;

	String jndiName;

	String parentName;

	String script;

	boolean started = false;

	public ContainerDeployer() {
		log = LogFactory.getLog(ContainerDeployer.class);
		log.info("instantiating container deployer");
	}

	public String getContainerComposer() {
		return containerComposer;
	}

	public void setContainerComposer(String containerComposer) {
		this.containerComposer = containerComposer;
	}

	public String getJndiName() {
		return jndiName;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * compose container and start it
	 */
	public synchronized void start() throws Exception {
		log.info("starting container");

		if (started) {
			throw new IllegalStateException("container already started");
		}
		// create container from script
		ScriptBuilderResolver resolver = new ScriptBuilderResolver();
		if (getScript() == null) {
			throw new Exception("script shall be specified");
		}
		// explicit setting wins
		String builderName = getContainerComposer();

		// if no builder is set, try to resolve from script
		if (builderName == null) {
			builderName = resolver.getBuilderClassName(getScript());
		}
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Reader scriptReader = new InputStreamReader(loader
				.getResourceAsStream(getScript()));
		ScriptedContainerBuilderFactory factory = new ScriptedContainerBuilderFactory(
				scriptReader, builderName, loader);
		
		InitialContext context = new InitialContext();
		
		JNDIObjectReference parentContainer = new JNDIObjectReference(getParentName(),context);
		JNDIObjectReference myContainer = new JNDIObjectReference(getJndiName(),context);
		
		factory.getContainerBuilder().buildContainer(myContainer,parentContainer,null,false);
		
		log.info("container started an bound to JNDI");
		started = true;
	}

	/**
	 * stop and dispose container
	 */
	public void stop() throws Exception {
		log.info("stopping container");
	}

	public boolean isStarted() {
		return started;
	}

}
