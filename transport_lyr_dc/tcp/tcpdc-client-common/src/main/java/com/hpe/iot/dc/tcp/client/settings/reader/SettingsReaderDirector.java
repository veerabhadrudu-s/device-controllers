/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hpe.iot.dc.tcp.client.settings.ClientSettings;

/**
 * @author sveera
 *
 */
public class SettingsReaderDirector {

	private final ClientSettingsBuilder clientSettingsBuilder;

	public SettingsReaderDirector(ClientSettingsBuilder clientSettingsBuilder) {
		super();
		this.clientSettingsBuilder = clientSettingsBuilder;
	}

	public ClientSettings readSettings(String tcpClientPropertiesPath) throws IOException {
		return initalizeSettings(tcpClientPropertiesPath);
	}

	private ClientSettings initalizeSettings(String tcpClientPropertiesPath) throws IOException {
		InputStream inputStream = new FileInputStream(
				System.getProperty("user.dir") + File.separator + tcpClientPropertiesPath);
		Properties properties = new Properties();
		properties.load(inputStream);
		clientSettingsBuilder.startDeviceID(properties.getProperty("startDeviceID"))
				.noOfClientRunners(properties.getProperty("noOfClientRunners"))
				.serverIp(properties.getProperty("serverIp")).serverPort(properties.getProperty("serverPort"))
				.noOfClientsPerRunner(properties.getProperty("noOfClientsPerRunner")).connectWaitItration(20)
				.notifGenIntrvl(properties.getProperty("notifGenIntrvl"))
				.handShakeMsgType(properties.getProperty("handShakeMsgType"))
				.handShakeResponseMsgType(properties.getProperty("handShakeResponseMsgType"))
				.notificationMessageType(properties.getProperty("notificationMessageType"));
		inputStream.close();
		return clientSettingsBuilder.build();
	}

}
