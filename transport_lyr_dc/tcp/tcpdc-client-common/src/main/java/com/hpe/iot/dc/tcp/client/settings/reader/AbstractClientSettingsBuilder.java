/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

import com.hpe.iot.dc.tcp.client.runner.handler.ClientHandlerSettings;
import com.hpe.iot.dc.tcp.client.settings.ClientSettings;

/**
 * @author sveera
 *
 */
public abstract class AbstractClientSettingsBuilder implements ClientSettingsBuilder {

	protected int noOfClientsPerRunner = 1000;
	protected int noOfClientRunners = 3;
	private String handShakeMsgType;
	private String handShakeResponseMsgType;
	private String notificationMessageType;
	private String serverIp;
	private int serverPort;
	private int connectWaitItration = 10;
	private long notifGenIntrvl = 20000;
	private long startDeviceID = 10000;

	public AbstractClientSettingsBuilder() {
		super();
	}

	@Override
	public ClientSettingsBuilder startDeviceID(String startDeviceID) {
		this.startDeviceID = startDeviceID == null || startDeviceID.trim().isEmpty() ? this.startDeviceID
				: getLongValue("startDeviceID", startDeviceID);
		return this;

	}

	@Override
	public ClientSettingsBuilder handShakeMsgType(String handShakeMsgType) {
		this.handShakeMsgType = handShakeMsgType;
		return this;
	}

	@Override
	public ClientSettingsBuilder handShakeResponseMsgType(String handShakeResponseMsgType) {
		this.handShakeResponseMsgType = handShakeResponseMsgType;
		return this;
	}

	@Override
	public ClientSettingsBuilder notificationMessageType(String notificationMessageType) {
		this.notificationMessageType = notificationMessageType;
		return this;
	}

	@Override
	public ClientSettingsBuilder serverIp(String serverIp) {
		this.serverIp = serverIp;
		return this;
	}

	@Override
	public ClientSettingsBuilder serverPort(String serverPort) {
		this.serverPort = getIntegerValue("serverPort", serverPort);
		return this;
	}

	@Override
	public ClientSettingsBuilder connectWaitItration(int waitIteration) {
		this.connectWaitItration = waitIteration;
		return this;
	}

	@Override
	public ClientSettingsBuilder notifGenIntrvl(String notifGenIntrvl) {
		this.notifGenIntrvl = notifGenIntrvl == null || notifGenIntrvl.trim().isEmpty() ? this.notifGenIntrvl
				: getLongValue("notifGenIntrvl", notifGenIntrvl);
		return this;
	}

	@Override
	public ClientSettings build() {
		return new ClientSettings(startDeviceID, noOfClientRunners,
				new ClientHandlerSettings(handShakeMsgType, handShakeResponseMsgType, notificationMessageType, serverIp,
						serverPort, noOfClientsPerRunner, connectWaitItration, notifGenIntrvl));
	}

	protected int getIntegerValue(String fieldName, String integerString) {
		try {
			return Integer.parseInt(integerString);
		} catch (NumberFormatException n) {
			throw new RuntimeException(String.format("Input settings field - %s value should be Integer ", fieldName));
		}
	}

	protected long getLongValue(String fieldName, String longString) {
		try {
			return Long.parseLong(longString);
		} catch (NumberFormatException n) {
			throw new RuntimeException(String.format("Input settings field - %s value should be Long ", fieldName));
		}
	}

}