package com.hpe.iot.dc.tcp.southbound.socket;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;

/**
 * @author sveera
 *
 */
public interface TCPServerSocketServiceProvider {

	ServerSocketChannel getServerSocketChannel(int portNumber, String boundLocalAddress, int backlog)
			throws IOException;

}
