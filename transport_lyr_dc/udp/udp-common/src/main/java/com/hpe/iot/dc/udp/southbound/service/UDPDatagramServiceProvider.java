package com.hpe.iot.dc.udp.southbound.service;

import java.net.DatagramSocket;

public interface UDPDatagramServiceProvider {

	DatagramSocket getDatagramSocket();

}
