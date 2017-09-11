package com.hpe.iot.dc.client.integration.test;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.CliTcpClient;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientToServerMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.impl.SampleClientToServerMessageGenerator;
import com.hpe.iot.dc.tcp.client.settings.reader.SettingsReader;

/**
 * @author sveera
 *
 */
public class TCPDCPluginTestClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ClientToServerMessageGenerator messageGenerator;

	@Before
	public void setUp() {
		messageGenerator = new SampleClientToServerMessageGenerator();
		SettingsReader.TCP_CLIENT_PROPERTIES = "src" + File.separator + "test" + File.separator + "resources"
				+ File.separator + "tcpClient.properties";
	}

	@Test
	@Ignore
	public void testAllTCPDCPluginsWithClientsConnected() throws IOException {
		logger.info("Running TCPClient ");
		new CliTcpClient().runClient(messageGenerator,null);
	}

}
