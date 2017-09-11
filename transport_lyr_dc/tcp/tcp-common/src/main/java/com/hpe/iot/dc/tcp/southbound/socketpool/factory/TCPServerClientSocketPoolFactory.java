/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool.factory;

import java.io.IOException;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public interface TCPServerClientSocketPoolFactory extends SocketPoolFactoryReader {

	ServerClientSocketPool getServerClientSocketPool(ServerSocketToDeviceModel serverSocketToDeviceModel)
			throws IOException;
	
	boolean removeServerClientSocketPool(ServerSocketToDeviceModel serverSocketToDeviceModel);
	
}
