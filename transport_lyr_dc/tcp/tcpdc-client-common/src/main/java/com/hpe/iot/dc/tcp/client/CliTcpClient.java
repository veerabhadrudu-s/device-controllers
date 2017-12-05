/**
 * 
 */
package com.hpe.iot.dc.tcp.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.device.counter.DeviceIdCounter;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientToServerMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ServerToClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.runner.ClientSocketHandlerRunner;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientSocketEnvironment;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientSocketHandler;
import com.hpe.iot.dc.tcp.client.settings.ClientSettings;
import com.hpe.iot.dc.tcp.client.settings.reader.ClientType;
import com.hpe.iot.dc.tcp.client.settings.reader.SettingsReader;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManagerImpl;

/**
 * @author sveera
 *
 */
public class CliTcpClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void runClient(final ClientToServerMessageGenerator messageGenerator,
			final ServerToClientMessageGenerator severToClientMessageGenerator) throws IOException {
		ClientSettings clientSettings = new SettingsReader().readSettings(ClientType.CLI);
		logger.info("Using client Settings :- " + clientSettings);
		logger.info("Connecting Clients");
		ExecutorService executorService = Executors.newFixedThreadPool(clientSettings.getNoOfClientRunners() * 3 + 1);
		List<ClientSocketHandlerRunner> clientSocketRunners = connectClientsForPluginScript(executorService,
				clientSettings, messageGenerator, severToClientMessageGenerator);
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
		logger.info("Disconnecting Clients");
		stopAllClientSocketRunners(clientSocketRunners);
		executorService.shutdown();
	}

	protected List<ClientSocketHandlerRunner> connectClientsForPluginScript(final ExecutorService executorService,
			final ClientSettings clientSettings, final ClientToServerMessageGenerator severToClientMessageGenerator,
			ServerToClientMessageGenerator clientToServerMessageGenerator) throws IOException {
		final List<ClientSocketHandlerRunner> clientSocketRunners = new ArrayList<>();
		final DeviceIdCounter deviceIdCounter = new DeviceIdCounter(clientSettings.getStaringDeviceId(),
				clientSettings.getClientRunnerSettings().getNoOfClients() * clientSettings.getNoOfClientRunners());

		for (int clientRunnerCount = 0; clientRunnerCount < clientSettings
				.getNoOfClientRunners(); clientRunnerCount++) {
			ClientSocketManagerImpl clientSocketManagerImpl = new ClientSocketManagerImpl(
					clientSettings.getClientRunnerSettings(), deviceIdCounter, clientRunnerCount);
			ClientSocketEnvironment clientSocketEnvironment = new ClientSocketEnvironment(
					clientSettings.getClientRunnerSettings(), clientSocketManagerImpl, clientSocketManagerImpl,
					severToClientMessageGenerator, clientToServerMessageGenerator, clientRunnerCount);
			ClientSocketHandlerRunner clientSocketRunner = new ClientSocketHandlerRunner(
					new ClientSocketHandler(clientSocketEnvironment, executorService));
			clientSocketRunners.add(clientSocketRunner);
			executorService.submit(clientSocketRunner);
		}
		return clientSocketRunners;
	}

	protected void stopAllClientSocketRunners(List<ClientSocketHandlerRunner> clientSocketRunners) {
		for (Iterator<ClientSocketHandlerRunner> iterator = clientSocketRunners.iterator(); iterator.hasNext();) {
			ClientSocketHandlerRunner clientSocketRunner = (ClientSocketHandlerRunner) iterator.next();
			clientSocketRunner.stopConnectedClients();
		}
	}

}
