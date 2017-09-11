package com.hpe.iot.dc.groovy.loader;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.GroovyClassLoader;

/**
 * @author sveera
 *
 */
public class GroovyScriptClassLoader {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private GroovyClassLoader groovyClassLoader;

	public GroovyScriptClassLoader(File groovyFile) throws CompilationFailedException, IOException {
		logger.debug("Loading Groovy script from file " + groovyFile.getAbsolutePath());
		groovyClassLoader = new GroovyClassLoader();
		groovyClassLoader.parseClass(groovyFile);
	}

	public Class<?>[] getLoadedClasses() {
		return groovyClassLoader.getLoadedClasses();
	}

}
