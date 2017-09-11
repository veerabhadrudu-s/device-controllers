/**
 * 
 */
package com.hpe.iot.dc.udp.model;

import java.net.InetAddress;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface UDPDevice extends DeviceModel {

	InetAddress getAddress();

	int getPort();

}
