/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.service.inflow.session;

import java.nio.channels.SocketChannel;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ClientSocketDeviceReader;

/**
 * @author sveera
 *
 */
public interface DeviceClientSocketExtractor {

	Device extractConnectedDevice(byte[] clientSocketData, SocketChannel socketChannel, DeviceModel deviceModel,
			ClientSocketDeviceReader clientSocketDeviceReader);

}
