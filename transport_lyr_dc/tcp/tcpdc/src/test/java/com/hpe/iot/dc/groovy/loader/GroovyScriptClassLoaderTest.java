package com.hpe.iot.dc.groovy.loader;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.File;
import java.util.Arrays;

import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class GroovyScriptClassLoaderTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String GROOVY_DIRECTORY = "testGroovy";
	private static final String GROOVY_FILE = "mmiSafeMate.groovy";
	private GroovyScriptClassLoader groovyScriptlClassLoader;

	@Before
	public void setUp() throws Exception {
		groovyScriptlClassLoader = new GroovyScriptClassLoader(
				new File(ClassLoader.getSystemResource(GROOVY_DIRECTORY + File.separator + GROOVY_FILE).toURI()));
	}

	@Test
	public void testGroovyScriptlCassLoader() {
		assertNotNull("GroovyScriptClassLoader cannot be null", groovyScriptlClassLoader);
	}

	@Test
	public void testGetLoadedClasses() {
		try {
			for (Class<?> loadedClass : groovyScriptlClassLoader.getLoadedClasses()) {
				logger.info("Loaded class name is " + loadedClass.getName());
				logger.info("Interfaces Implemented by this class" + loadedClass.getName() + " is "
						+ Arrays.asList(loadedClass.getInterfaces()));
			}
		} catch (CompilationFailedException e) {
			logger.error("Failed to complile Groovy Script");
			logExceptionStackTrace(e, getClass());
			Assert.fail("Failed to compile script");
		} catch (Exception e) {
			logExceptionStackTrace(e, getClass());
			Assert.fail("Failed to compile script");
		}
	}

}
