package com.handson.iot.dc.groovy.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class GroovyScriptClassLoaderTest {

	private static final String GROOVY_DIRECTORY = "testGroovy";
	private static final String GROOVY_FILE = "testClasses.groovy";
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
	@DisplayName("Test class count loaded by Groovy Class Loader")
	public void testGetLoadedClassesCount() {
		assertEquals(3, groovyScriptlClassLoader.getLoadedClasses().length,
				"Expected and Actual loaded class count are not same ");
	}

}
