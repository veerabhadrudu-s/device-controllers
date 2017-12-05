/**
 * 
 */
package com.hpe.iot.dc.tcp.client.socket;

import java.io.IOException;
import java.util.List;

import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;

/**
 * @author sveera
 *
 */
public interface ClientSocketManager {

	void connectClients() throws IOException, InterruptedException;

	List<DeviceSocketModel> getDeviceClients();

	DeviceSocketModel getDeviceClient(long deviceId);

	void stopAllClients();

	List<DeviceSocketModel> getHandshakedClients();

}