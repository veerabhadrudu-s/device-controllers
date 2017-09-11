/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings;

/**
 * @author sveera
 *
 */
public class DeviceClientSettings {

	private final String handShakeMsgType;
	private final String handShakeResponseMsgType;
	private final String notificationMessageType;

	public DeviceClientSettings(String handShakeMsgType, String handShakeResponseMsgType,
			String notificationMessageType) {
		super();
		this.handShakeMsgType = handShakeMsgType;
		this.handShakeResponseMsgType = handShakeResponseMsgType;
		this.notificationMessageType = notificationMessageType;
	}

	public String getHandShakeMsgType() {
		return handShakeMsgType;
	}

	public String getNotificationMessageType() {
		return notificationMessageType;
	}

	public String getHandShakeResponseMsgType() {
		return handShakeResponseMsgType;
	}

	@Override
	public String toString() {
		return "DeviceClientSettings [handShakeMsgType=" + handShakeMsgType + ", handShakeResponseMsgType="
				+ handShakeResponseMsgType + ", notificationMessageType=" + notificationMessageType + "]";
	}

}
