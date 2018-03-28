package com.hpe.iot.dc.mmi.vt15.client.integration.test;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.mmi.vt15.tcp.client.payload.converters.MMIVT15ClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.CliTcpClient;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class TCPDCPluginTestClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ClientMessageGenerator messageGenerator;

	@BeforeEach
	public void setUp() {
		messageGenerator = new MMIVT15ClientMessageGenerator();
	}

	@Test
	@Disabled
	public void testMMIVT15Plugin() throws IOException, InterruptedException {
		logger.info("Running TCPClient ");
		new CliTcpClient().runClient("src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "tcpClient.properties", messageGenerator);
	}

}
