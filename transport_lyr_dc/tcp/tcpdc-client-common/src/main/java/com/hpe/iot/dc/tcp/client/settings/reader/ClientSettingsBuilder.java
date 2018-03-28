/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

import com.hpe.iot.dc.tcp.client.settings.ClientSettings;

/**
 * @author sveera
 *
 */
public interface ClientSettingsBuilder {

	ClientSettingsBuilder noOfClientRunners(String noOfClientRunners);

	ClientSettingsBuilder startDeviceID(String startDeviceID);

	ClientSettingsBuilder handShakeMsgType(String handShakeMsgType);

	ClientSettingsBuilder handShakeResponseMsgType(String handShakeResponseMsgType);

	ClientSettingsBuilder notificationMessageType(String notificationMessageType);

	ClientSettingsBuilder serverIp(String serverIp);

	ClientSettingsBuilder serverPort(String serverPort);

	ClientSettingsBuilder noOfClientsPerRunner(String noOfClientsPerRunner);

	ClientSettingsBuilder connectWaitItration(int waitIteration);

	ClientSettingsBuilder notifGenIntrvl(String notifGenIntrvl);

	ClientSettings build();

}