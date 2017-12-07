/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.handler;

import com.hpe.iot.dc.tcp.client.notify.ClientHandShakeNotifier;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManager;
import com.hpe.iot.dc.tcp.client.writer.ClientSocketWriter;

/**
 * @author sveera
 *
 */
public class ClientSocketEnvironment {

	private final ClientHandlerSettings clientHandlerSettings;
	private final ClientSocketManager clientSocketManager;
	private final ClientHandShakeNotifier clientHandshakeNotifier;
	private final ClientSocketWriter clientSocketWriter;
	private final ClientMessageGenerator clientToServerMessageGenerator;
	private final ClientMessageConsumer severToClientMessageGenerator;
	private final int index;

	public ClientSocketEnvironment(ClientHandlerSettings clientHandlerSettings, ClientSocketManager clientSocketManager,
			ClientHandShakeNotifier clientHandshakeNotifier,
			ClientMessageGenerator clientToServerMessageGenerator,
			ClientMessageConsumer severToClientMessageGenerator, int index) {
		super();
		this.clientHandlerSettings = clientHandlerSettings;
		this.clientSocketManager = clientSocketManager;
		this.clientHandshakeNotifier = clientHandshakeNotifier;
		this.clientToServerMessageGenerator = clientToServerMessageGenerator;
		this.severToClientMessageGenerator = severToClientMessageGenerator;
		this.index = index;
		this.clientSocketWriter = new ClientSocketWriter(index, clientToServerMessageGenerator);
	}

	public ClientHandlerSettings getClientHandlerSettings() {
		return clientHandlerSettings;
	}

	public ClientSocketManager getClientSocketManager() {
		return clientSocketManager;
	}

	public ClientHandShakeNotifier getClientHandshakeNotifier() {
		return clientHandshakeNotifier;
	}

	public ClientSocketWriter getClientSocketWriter() {
		return clientSocketWriter;
	}

	public ClientMessageGenerator getClientMessageGenerator() {
		return clientToServerMessageGenerator;
	}

	public ClientMessageConsumer getClientMessageConsumer() {
		return severToClientMessageGenerator;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "ClientSocketEnvironment [clientRunnerSettings=" + clientHandlerSettings + ", clientSocketManager="
				+ clientSocketManager + ", clientHandshakeNotifier=" + clientHandshakeNotifier + ", clientSocketWriter="
				+ clientSocketWriter + ", clientToServerMessageGenerator=" + clientToServerMessageGenerator
				+ ", severToClientMessageGenerator=" + severToClientMessageGenerator + ", index=" + index + "]";
	}

}
