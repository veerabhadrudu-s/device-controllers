/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socket.listener;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;

/**
 * @author sveera
 *
 */
public interface SocketChangeListener {

	void handleChangedCount(ServerSocketToDeviceModel serverSocketToDeviceModel, int deviceCount);

}
