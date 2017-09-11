package com.hpe.iot.dc.udp.southbound.service.impl;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hpe.iot.dc.udp.southbound.service.UDPDatagramServiceProvider;

@Component
public class UDPDatagramSender {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final UDPDatagramServiceProvider udpDatagramServiceProvider;

	@Autowired
	public UDPDatagramSender(UDPDatagramServiceProvider udpDatagramServiceProvider) {
		this.udpDatagramServiceProvider = udpDatagramServiceProvider;
	}

	public void sendUDPDatagram(String host, int port, byte[] message) {
		try {
			// InetAddress address = InetAddress.getByName(host);
			final SocketAddress address = new InetSocketAddress(host, port);
			logger.info("Destination address is : " + address.toString());
			final DatagramPacket packet = new DatagramPacket(message, message.length, address);
			udpDatagramServiceProvider.getDatagramSocket().send(packet);
		} catch (Exception e) {
			logger.error("Failed to connec to Destination with IP " + host + " on port " + port, e);
		}
	}

}
