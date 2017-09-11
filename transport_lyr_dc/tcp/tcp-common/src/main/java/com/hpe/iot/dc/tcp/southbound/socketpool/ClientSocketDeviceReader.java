/**
 * 
 */
package com.hpe.iot.dc.tcp.southbound.socketpool;

import java.nio.channels.SocketChannel;

import com.hpe.iot.dc.model.Device;

/**
 * @author sveera
 *
 */
public interface ClientSocketDeviceReader {

	Device getDevice(SocketChannel socketChannel);

}
