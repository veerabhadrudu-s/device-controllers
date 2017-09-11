/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner.handler;

import com.hpe.iot.dc.tcp.client.notify.ClientHandShakeNotifier;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientToServerMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ServerToClientMessageGenerator;
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
	private final ClientToServerMessageGenerator clientToServerMessageGenerator;
	private final ServerToClientMessageGenerator severToClientMessageGenerator;
	private final int index;

	public ClientSocketEnvironment(ClientHandlerSettings clientHandlerSettings, ClientSocketManager clientSocketManager,
			ClientHandShakeNotifier clientHandshakeNotifier,
			ClientToServerMessageGenerator clientToServerMessageGenerator,
			ServerToClientMessageGenerator severToClientMessageGenerator, int index) {
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

	public ClientToServerMessageGenerator getClientToServerMessageGenerator() {
		return clientToServerMessageGenerator;
	}

	public ServerToClientMessageGenerator getSeverToClientMessageGenerator() {
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
