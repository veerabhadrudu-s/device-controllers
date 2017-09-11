package com.hpe.iot.dc.mmi.safemate.northbound.service.outflow.impl;

import java.nio.channels.SocketChannel

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.SettingsDownlinkService
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool
import com.hpe.iot.dc.tcp.southbound.socketpool.impl.DefaultTCPServerClientSocketPool

import static org.junit.Assert.*

/**
 * @author sveera
 *
 */
class SettingsDownlinkServiceTest {

	private MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private SettingsDownlinkService settingsDownlinkService;
	private ServerClientSocketPool tcpServerClientSocketPool;
	@Mock
	private SocketChannel socketChannel;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.when(socketChannel.isConnected()).thenReturn(true);
		tcpServerClientSocketPool=new DefaultTCPServerClientSocketPool();
		TCPServerSocketWriter tcpServerSocketSender = new TCPServerSocketWriter(tcpServerClientSocketPool);
		settingsDownlinkService=new SettingsDownlinkService(tcpServerSocketSender, mmicrcAlgorithm);
	}

	@Test
	@Ignore
	public void test() {
		fail("Not yet implemented");
	}
}
