/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.handler;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.model.DeviceSocketModel;
import com.hpe.iot.dc.tcp.client.runner.reader.ClientSocketReaderRunner;
import com.hpe.iot.dc.tcp.client.runner.writer.ClientSocketWriterRunner;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManager;
import com.hpe.iot.dc.tcp.client.writer.ClientSocketWriter;

/**
 * @author sveera
 *
 */
public class ClientSocketHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ClientSocketEnvironment clientSocketEnvironment;
	private final ExecutorService executorService;
	private final ClientSocketWriterRunner clientSocketWriterRunner;
	private ClientSocketReaderRunner clientSocketReaderRunner;

	private boolean isHandlerRunnable;

	public ClientSocketHandler(ClientSocketEnvironment clientSocketEnvironment, ExecutorService executorService) {
		this.clientSocketEnvironment = clientSocketEnvironment;
		this.clientSocketWriterRunner = new ClientSocketWriterRunner(clientSocketEnvironment.getClientSocketWriter(),
				clientSocketEnvironment.getClientSocketManager(), clientSocketEnvironment.getClientHandlerSettings(),
				clientSocketEnvironment.getIndex());
		this.executorService = executorService;
		this.isHandlerRunnable = true;

	}

	public void stopSocketHandler() {
		isHandlerRunnable = false;
		clientSocketWriterRunner.stopSocketWriter();
		if (clientSocketReaderRunner != null)
			clientSocketReaderRunner.stopSocketReader();
		clientSocketEnvironment.getClientSocketManager().stopAllClients();
	}

	public void startSocketHandler() {
		try {
			clientSocketEnvironment.getClientSocketManager().connectClients();
			if (isAllDevicesConnected())
				startHandling();
		} catch (Throwable e) {
			logger.error("Failed to run Client socket handler " + toString());
			logExceptionStackTrace(e, getClass());
		} finally {
			stopSocketHandler();
		}
	}

	private boolean isAllDevicesConnected() {
		logger.info("Total Connected devices count in client socket handler " + toString() + " is "
				+ clientSocketEnvironment.getClientSocketManager().getDeviceClients().size());
		boolean isAllClientsConnected = clientSocketEnvironment.getClientSocketManager().getDeviceClients()
				.size() == clientSocketEnvironment.getClientHandlerSettings().getNoOfClients() ? true : false;
		return isAllClientsConnected;
	}

	private void startHandling() throws IOException, InterruptedException {
		Future<Void> readerResponse = null;
		if (clientSocketEnvironment.getSeverToClientMessageGenerator() != null)
			readerResponse = intializeDownLinkFlowForClients();
		else
			completeHandShakeForConnectedClients();
		Future<Void> writerResponse = executorService.submit(clientSocketWriterRunner);
		checkForAnyExceptionsInRunners(writerResponse, readerResponse);
	}

	private Future<Void> intializeDownLinkFlowForClients() throws IOException, InterruptedException {
		final ClientSocketManager clientSocketManager = clientSocketEnvironment.getClientSocketManager();
		final ClientHandlerSettings clientHandlerSettings = clientSocketEnvironment.getClientHandlerSettings();
		final ClientSocketWriter clientSocketWriter = clientSocketEnvironment.getClientSocketWriter();
		Future<Void> readerResponse = activateMessageListener();
		if (clientHandlerSettings.getHandShakeMsgType() != null
				&& clientHandlerSettings.getHandShakeResponseMsgType() != null)
			clientSocketWriter.pushDataUsingClients(clientHandlerSettings.getHandShakeMsgType(), clientSocketManager
					.getDeviceClients().toArray(new DeviceSocketModel[clientSocketManager.getDeviceClients().size()]));
		else
			completeHandShakeForConnectedClients();
		return readerResponse;
	}

	private Future<Void> activateMessageListener() throws IOException {
		clientSocketReaderRunner = new ClientSocketReaderRunner(
				clientSocketEnvironment.getSeverToClientMessageGenerator(),
				clientSocketEnvironment.getClientSocketWriter(), clientSocketEnvironment.getClientHandshakeNotifier(),
				clientSocketEnvironment.getClientSocketManager(), clientSocketEnvironment.getClientHandlerSettings(),
				clientSocketEnvironment.getIndex());
		return executorService.submit(clientSocketReaderRunner);
	}

	private void completeHandShakeForConnectedClients() {
		for (DeviceSocketModel deviceSocketModel : clientSocketEnvironment.getClientSocketManager().getDeviceClients())
			clientSocketEnvironment.getClientHandshakeNotifier().handshakeCompleted(deviceSocketModel.getDeviceId());
	}

	private void checkForAnyExceptionsInRunners(Future<Void> writerResponse, Future<Void> readerResponse) {
		boolean isWriterExecutionException, isReaderExecutionException;
		while (isHandlerRunnable) {
			isWriterExecutionException = tryForExecutionException(writerResponse);
			isReaderExecutionException = tryForExecutionException(readerResponse);
			if (isWriterExecutionException | isReaderExecutionException)
				isHandlerRunnable = false;
		}
	}

	private boolean tryForExecutionException(Future<Void> futureResponse) {
		boolean isExecutionException = false;
		if (futureResponse == null)
			return isExecutionException;
		try {
			futureResponse.get(1000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			// logger.trace("Time out exception occurred ");
		} catch (Throwable exception) {
			logExceptionStackTrace(exception.getCause(), getClass());
			isExecutionException = true;
		}
		return isExecutionException;
	}

	@Override
	public String toString() {
		return "ClientSocketHandler [index=" + clientSocketEnvironment.getIndex() + "]";
	}

}
