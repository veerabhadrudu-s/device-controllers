/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socket.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class TCPServerSocketServiceProviderImplTest {

	private TCPServerSocketServiceProviderImpl tcpServerSocketServiceProviderImpl;

	@BeforeEach
	public void setUp() throws Exception {
		tcpServerSocketServiceProviderImpl = new TCPServerSocketServiceProviderImpl();
	}

	@Test
	@DisplayName("test Is TCPServerSocketServiceProviderImpl not null")
	public void testTCPServerSocketServiceProviderImpl() {
		assertNotNull(tcpServerSocketServiceProviderImpl,
				tcpServerSocketServiceProviderImpl.getClass().getSimpleName() + " cannot be null");
	}

	@Test
	@DisplayName("test ServerSocketChannel options")
	public void testGetServerSocketChannel() throws IOException {
		try (ServerSocketChannel serverSocketChannel = tcpServerSocketServiceProviderImpl.getServerSocketChannel(2001,
				"localhost", 10000)) {
			assertNotNull(serverSocketChannel, serverSocketChannel.getClass().getSimpleName() + " cannot be null");
			assertEquals(2001, serverSocketChannel.socket().getLocalPort(), "Expected and Actual ports are not equal");
			assertEquals("127.0.0.1", serverSocketChannel.socket().getInetAddress().getHostAddress());
			assertTrue(serverSocketChannel.isOpen(), "serverSocketChannel should be open");
			assertFalse(serverSocketChannel.isBlocking(), "serverSocketChannel should not be blocking");
		}

	}

}
