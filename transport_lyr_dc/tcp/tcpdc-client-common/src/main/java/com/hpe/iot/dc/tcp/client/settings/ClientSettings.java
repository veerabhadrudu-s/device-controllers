/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings;

import com.hpe.iot.dc.tcp.client.runner.handler.ClientHandlerSettings;

/**
 * @author sveera
 *
 */
public class ClientSettings {

	private final long staringDeviceId;
	private final int noOfClientRunners;
	private final ClientHandlerSettings clientRunnerSettings;

	public ClientSettings(long staringDeviceId, int noOfClientRunners, ClientHandlerSettings clientRunnerSettings) {
		super();
		this.staringDeviceId = staringDeviceId;
		this.noOfClientRunners = noOfClientRunners;
		this.clientRunnerSettings = clientRunnerSettings;
	}

	public long getStaringDeviceId() {
		return staringDeviceId;
	}

	public int getNoOfClientRunners() {
		return noOfClientRunners;
	}

	public ClientHandlerSettings getClientRunnerSettings() {
		return clientRunnerSettings;
	}

	@Override
	public String toString() {
		return "ClientSettings [staringDeviceId=" + staringDeviceId + ", noOfClientRunners=" + noOfClientRunners
				+ ", clientRunnerSettings=" + clientRunnerSettings + "]";
	}

}
