/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * @author sveera
 *
 */
public interface TCPServerSocketReader {
	void startClientCommunication();

	void stopClientCommunication();

	void startCommunicatingWithClient(SocketChannel socketToClient) throws IOException;
}