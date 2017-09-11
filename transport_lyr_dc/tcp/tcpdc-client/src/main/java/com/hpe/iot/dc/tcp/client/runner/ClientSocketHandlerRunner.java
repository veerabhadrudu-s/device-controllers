/**
 * 
 */
package com.hpe.iot.dc.tcp.client.runner;

import java.util.concurrent.Callable;

import com.hpe.iot.dc.tcp.client.runner.handler.ClientSocketHandler;

/**
 * @author sveera
 *
 */
public class ClientSocketHandlerRunner implements Callable<Void> {

	private ClientSocketHandler clientSocketHandler;

	public ClientSocketHandlerRunner(ClientSocketHandler clientSocketHandler) {
		this.clientSocketHandler = clientSocketHandler;
	}

	@Override
	public Void call() throws Exception {
		clientSocketHandler.startSocketHandler();
		return null;
	}

	public void stopConnectedClients() {
		clientSocketHandler.stopSocketHandler();
	}

	@Override
	public String toString() {
		return "SocketClientRunner [clientSocketHandler=" + clientSocketHandler + "]";
	}

}