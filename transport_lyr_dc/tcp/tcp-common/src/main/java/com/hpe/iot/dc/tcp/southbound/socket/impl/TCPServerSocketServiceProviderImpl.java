package com.hpe.iot.dc.tcp.southbound.socket.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.southbound.socket.TCPServerSocketServiceProvider;

/**
 * @author sveera
 *
 */
public class TCPServerSocketServiceProviderImpl implements TCPServerSocketServiceProvider {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public ServerSocketChannel getServerSocketChannel(final int portNumber, String boundLocalAddress, int backlog)
			throws IOException {
		InetAddress localAddress = InetAddress.getByName(boundLocalAddress);
		logger.debug("Local IP address used to bind for TCP communication is " + localAddress.toString());
		ServerSocketChannel myServerSocketChannel = ServerSocketChannel.open();
		myServerSocketChannel.configureBlocking(false);
		myServerSocketChannel.socket().bind(new InetSocketAddress(InetAddress.getByName(boundLocalAddress), portNumber),
				backlog);
		logger.debug("Server socket informaion " + myServerSocketChannel.toString());
		logger.trace("Is server socket open " + myServerSocketChannel.isOpen());
		return myServerSocketChannel;
	}

}
