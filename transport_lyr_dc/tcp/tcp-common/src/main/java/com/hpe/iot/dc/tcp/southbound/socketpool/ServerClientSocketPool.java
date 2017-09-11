/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.hpe.iot.dc.model.Device;

/**
 * @author sveera
 *
 */
public interface ServerClientSocketPool extends ClientSocketDeviceReader {

	Selector getClientSocketSelector();

	List<Device> getDevices();

	SocketChannel getClientSocket(Device device);

	void addSocketChannel(Device device, SocketChannel clientSocketChannel) throws IOException;

	void removeClientSocketChannel(SocketChannel socketChannel);

	void closeAllClientSockets();

}