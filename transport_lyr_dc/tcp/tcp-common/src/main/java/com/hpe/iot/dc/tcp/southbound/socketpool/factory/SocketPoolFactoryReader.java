/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool.factory;

import java.util.Map;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public interface SocketPoolFactoryReader {

	Map<ServerSocketToDeviceModel, ServerClientSocketPool> getDeviceModelToSocketPool();
}
