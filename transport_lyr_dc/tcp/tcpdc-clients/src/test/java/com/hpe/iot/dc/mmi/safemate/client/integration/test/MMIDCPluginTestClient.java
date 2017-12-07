package com.hpe.iot.dc.mmi.safemate.client.integration.test;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientToServerMessageGenerator;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMISeverToClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.CliTcpClient;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.settings.reader.SettingsReader;

/**
 * @author sveera
 *
 */
public class MMIDCPluginTestClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ClientMessageGenerator clientToServerMessageGenerator;
	private ClientMessageConsumer serverToClientMessageGenerator;

	@Before
	public void setUp() {
		clientToServerMessageGenerator = new MMIClientToServerMessageGenerator(new MMICRCAlgorithm());
		serverToClientMessageGenerator = new MMISeverToClientMessageGenerator();
		SettingsReader.TCP_CLIENT_PROPERTIES = "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "tcpClient.properties";
	}

	@Ignore
	@Test
	public void testAllTCPDCPluginsWithClientsConnected() throws IOException {
		logger.info("Running TCPClient ");
		new CliTcpClient().runClient(clientToServerMessageGenerator, serverToClientMessageGenerator);
	}

}
