/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.hpe.iot.dc.tcp.client.runner.handler.ClientHandlerSettings;
import com.hpe.iot.dc.tcp.client.settings.ClientSettings;

/**
 * @author sveera
 *
 */
public class SettingsReader {

	public static String TCP_CLIENT_PROPERTIES = "tcpClient.properties";
	private String serverIp = "10.3.239.75";
	private int serverPort = 2002;
	private int noOfClientRunners = 3;
	private int noOfClientsPerRunner = 1000;
	private int connecWaitItration = 20;
	private long startDeviceID = 10000;
	private long notifGenIntrvl = 20000;

	public ClientSettings readSettings(ClientType clientType) throws IOException {
		return initalizeSettings(clientType);
	}

	private ClientSettings initalizeSettings(ClientType clientType) throws IOException {
		InputStream inputStream = new FileInputStream(
				System.getProperty("user.dir") + File.separator + TCP_CLIENT_PROPERTIES);
		Properties properties = new Properties();
		properties.load(inputStream);
		serverIp = properties.getProperty("serverIp") == null ? serverIp : properties.getProperty("serverIp");
		serverPort = properties.getProperty("serverIp") == null ? serverPort
				: Integer.parseInt(properties.getProperty("serverPort"));
		noOfClientRunners = clientType != null && clientType == ClientType.CLI
				? properties.getProperty("noOfClientRunners") == null ? noOfClientRunners
						: Integer.parseInt(properties.getProperty("noOfClientRunners"))
				: 1;
		noOfClientsPerRunner = clientType != null && clientType == ClientType.CLI
				? properties.getProperty("noOfClientsPerRunner") == null ? noOfClientsPerRunner
						: Integer.parseInt(properties.getProperty("noOfClientsPerRunner"))
				: 1;
		startDeviceID = properties.getProperty("startDeviceID") == null ? startDeviceID
				: Long.parseLong(properties.getProperty("startDeviceID"));
		notifGenIntrvl = properties.getProperty("notifGenIntrvl") == null ? notifGenIntrvl
				: Long.parseLong(properties.getProperty("notifGenIntrvl"));
		String handShakeMsgType = properties.getProperty("handShakeMsgType");
		String notificationMessageType = properties.getProperty("notificationMessageType");
		String handShakeResponseMsgType = properties.getProperty("handShakeResponseMsgType");
		inputStream.close();
		return new ClientSettings(startDeviceID, noOfClientRunners,
				new ClientHandlerSettings(handShakeMsgType, handShakeResponseMsgType, notificationMessageType, serverIp,
						serverPort, noOfClientsPerRunner, connecWaitItration, notifGenIntrvl));
	}

}
