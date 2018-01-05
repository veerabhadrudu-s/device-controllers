package com.hpe.iot.dc.tcp.groovy.watch.file.listener;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class GroovyFileEventListenerTest {

	private GroovyFileEventListener groovyDirectoryListener;

	@BeforeEach
	public void setUp() throws Exception {
		// groovyDirectoryListener = new GroovyFileEventListener();
	}

	@Test
	@Disabled
	public void testGroovyDirectoryWatchService() {
		assertNotNull(groovyDirectoryListener, "GroovyDirectoryListener can not be null");
	}

}
