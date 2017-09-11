package com.hpe.iot.dc.groovy.loader;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.groovy.loader.GroovyScriptClassLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class GroovyScriptClassLoaderTest {

	private static final String GROOVY_DIRECTORY = "testGroovy";
	private static final String GROOVY_FILE = "testClasses.groovy";
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
		assertEquals("Expected and Actual loaded class count are not same ", 3,
				groovyScriptlClassLoader.getLoadedClasses().length);
	}

}
