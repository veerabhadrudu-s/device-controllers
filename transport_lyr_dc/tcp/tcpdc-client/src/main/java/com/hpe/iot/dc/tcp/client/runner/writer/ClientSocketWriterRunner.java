/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.writer;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientHandlerSettings;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManager;
import com.hpe.iot.dc.tcp.client.writer.ClientSocketWriter;

/**
 * @author sveera
 *
 */
public class ClientSocketWriterRunner implements Callable<Void> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ClientSocketWriter clientSocketWriter;
	private final ClientSocketManager clientSocketManager;
	private final ClientHandlerSettings clientRunnerSettings;
	private final int index;
	private boolean isWriterRunnable;

	public ClientSocketWriterRunner(ClientSocketWriter clientSocketWriter, ClientSocketManager clientSocketManager,
			ClientHandlerSettings clientRunnerSettings, int index) {
		super();
		this.clientSocketWriter = clientSocketWriter;
		this.clientSocketManager = clientSocketManager;
		this.clientRunnerSettings = clientRunnerSettings;
		this.index = index;
		this.isWriterRunnable = true;
	}

	public void stopSocketWriter() {
		isWriterRunnable = false;
	}

	@Override
	public Void call() throws Exception {
		pushDataForConnectedClients();
		return null;
	}

	private void pushDataForConnectedClients() throws IOException, InterruptedException {
		while (isWriterRunnable) {
			clientSocketWriter.pushDataUsingClients(clientRunnerSettings.getNotificationMessageType(),
					clientSocketManager.getHandshakedClients()
							.toArray(new DeviceSocketModel[clientSocketManager.getHandshakedClients().size()]));
			logger.debug(toString()
					+ "Completed one cycle of notification message type write operation on connected clients ");
			logger.debug(toString()
					+ "******************************************************************************************************");
			Thread.sleep(clientRunnerSettings.getNotifGenIntrvl());
		}
	}

	@Override
	public String toString() {
		return "ClientSocketWriterRunner [index=" + index + "]";
	}

}
