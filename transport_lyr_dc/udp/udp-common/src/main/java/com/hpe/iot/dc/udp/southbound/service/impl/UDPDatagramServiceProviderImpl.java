package com.hpe.iot.dc.udp.southbound.service.impl;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hpe.iot.dc.udp.southbound.service.UDPDatagramServiceProvider;

@Service("udpDatagramServiceProvider")
public class UDPDatagramServiceProviderImpl implements UDPDatagramServiceProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	final DatagramSocket myDatagramsocket;

	@Autowired
	public UDPDatagramServiceProviderImpl(@Value("${udp.inbound.port}") String outboundPort,
			@Value("${udp.ip.localaddress}") String boundLocalAddress) throws SocketException, UnknownHostException {
		super();
		// final SocketAddress localAddress = new
		// InetSocketAddress(boundLocalAddress, Integer.parseInt(outboundPort));
		InetAddress localAddress = InetAddress.getByName(boundLocalAddress);
		logger.debug("IPAaddress used for UDP communication is " + localAddress.toString());
		logger.debug("Port used for UDP communication is " + outboundPort);
		myDatagramsocket = new DatagramSocket(Integer.parseInt(outboundPort), localAddress);
	}

	public DatagramSocket getDatagramSocket() {
		return myDatagramsocket;
	}

	@PreDestroy
	public void close() {
		myDatagramsocket.close();
	}

}
