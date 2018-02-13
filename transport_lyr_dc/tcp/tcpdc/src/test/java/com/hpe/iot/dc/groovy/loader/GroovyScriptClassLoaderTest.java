package com.hpe.iot.dc.groovy.loader;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.Arrays;

import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.groovy.loader.GroovyScriptClassLoader;

/**
 * @author sveera
 *
 */
public class GroovyScriptClassLoaderTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String GROOVY_DIRECTORY = "testGroovy";
	private static final String GROOVY_FILE = "mmiSafeMate.groovy";
	private GroovyScriptClassLoader groovyScriptlClassLoader;

	@BeforeEach
	public void setUp() throws Exception {
		groovyScriptlClassLoader = new GroovyScriptClassLoader(
				new File(ClassLoader.getSystemResource(GROOVY_DIRECTORY + File.separator + GROOVY_FILE).toURI()));
	}

	@Test
	public void testGroovyScriptlCassLoader() {
		assertNotNull(groovyScriptlClassLoader, "GroovyScriptClassLoader cannot be null");
	}

	@Test
	public void testGetLoadedClassesCount() {
		try {
			for (Class<?> loadedClass : groovyScriptlClassLoader.getLoadedClasses()) {
				logger.info("Loaded class name is " + loadedClass.getName());
				logger.info("Interfaces Implemented by this class" + loadedClass.getName() + " is "
						+ Arrays.asList(loadedClass.getInterfaces()));
			}
		} catch (CompilationFailedException e) {
			logger.error("Failed to complile Groovy Script");
			logExceptionStackTrace(e, getClass());
			fail("Failed to compile script");
		} catch (Exception e) {
			logExceptionStackTrace(e, getClass());
			fail("Failed to compile script");
		}
	}

}
