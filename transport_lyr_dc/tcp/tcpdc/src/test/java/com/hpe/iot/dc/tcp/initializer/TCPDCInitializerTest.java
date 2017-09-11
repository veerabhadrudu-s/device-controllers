package com.hpe.iot.dc.tcp.initializer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.tcp.southbound.service.manager.TCPServerSocketServiceManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class TCPDCInitializerTest {

	private ClassPathXmlApplicationContext applicationContext;

	@Before
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("bean-config.xml");
	}

	@Test
	public void testTCPDCInitializer() {
		TCPServerSocketServiceManager tcpServerSocketServiceManager = applicationContext
				.getBean(TCPServerSocketServiceManager.class);
		assertNotNull("TCPDCInitializer Cannot be null ", tcpServerSocketServiceManager);
		assertEquals("Expected Running services and actual running servcies are not same ", 4,
				tcpServerSocketServiceManager.getRunningServerSocketServices().size());
		applicationContext.close();
	}

}
