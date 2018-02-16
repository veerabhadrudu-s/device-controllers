package com.hpe.iot.dc.tcp.initializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.tcp.southbound.service.manager.TCPServerSocketServiceManager;

/**
 * @author sveera
 *
 */
public class TCPDCInitializerTest {

	private ClassPathXmlApplicationContext applicationContext;

	@BeforeEach
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("bean-config.xml");
	}

	@Test
	@DisplayName("test TCP DC initializer")
	public void testTCPDCInitializer() {
		TCPServerSocketServiceManager tcpServerSocketServiceManager = applicationContext
				.getBean(TCPServerSocketServiceManager.class);
		assertNotNull(tcpServerSocketServiceManager, "TCPDCInitializer Cannot be null");
		assertEquals(4, tcpServerSocketServiceManager.getRunningServerSocketServices().size(),
				"Expected Running services and actual running servcies are not same");
		applicationContext.close();
	}

}
