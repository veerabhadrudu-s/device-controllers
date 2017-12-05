/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.handler;

import com.hpe.iot.dc.tcp.client.settings.DeviceClientSettings;

/**
 * @author sveera
 *
 */
public class ClientHandlerSettings extends DeviceClientSettings {

	private final String serverIp;
	private final int serverPort;
	private final int noOfClients;
	private final int waitIteration;
	private final long notifGenIntrvl;

	public ClientHandlerSettings(String handShakeMsgType, String handShakeResponseMsgType,
			String notificationMessageType, String serverIp, int serverPort, int noOfClients, int waitIteration,
			long notifGenIntrvl) {
		super(handShakeMsgType, handShakeResponseMsgType, notificationMessageType);
		this.serverIp = serverIp;
		this.serverPort = serverPort;
		this.noOfClients = noOfClients;
		this.waitIteration = waitIteration;
		this.notifGenIntrvl = notifGenIntrvl;
	}

	public long getNotifGenIntrvl() {
		return notifGenIntrvl;
	}

	public String getServerIp() {
		return serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getNoOfClients() {
		return noOfClients;
	}

	public int getWaitIteration() {
		return waitIteration;
	}

	@Override
	public String toString() {
		return "ClientRunnerSettings [serverIp=" + serverIp + ", serverPort=" + serverPort + ", noOfClients="
				+ noOfClients + ", waitIteration=" + waitIteration + ", notifGenIntrvl=" + notifGenIntrvl
				+ ", toString()=" + super.toString() + "]";
	}

}
