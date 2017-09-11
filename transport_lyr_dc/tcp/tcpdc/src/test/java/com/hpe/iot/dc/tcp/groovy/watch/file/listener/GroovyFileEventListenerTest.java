package com.hpe.iot.dc.tcp.groovy.watch.file.listener;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hpe.iot.dc.tcp.groovy.watch.file.listener.GroovyFileEventListener;

import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class GroovyFileEventListenerTest {

	private GroovyFileEventListener groovyDirectoryListener;

	@Before
	public void setUp() throws Exception {
		// groovyDirectoryListener = new GroovyFileEventListener();
	}

	@Test
	@Ignore
	public void testGroovyDirectoryWatchService() {
		assertNotNull("GroovyDirectoryListener can not be null", groovyDirectoryListener);
	}

}
