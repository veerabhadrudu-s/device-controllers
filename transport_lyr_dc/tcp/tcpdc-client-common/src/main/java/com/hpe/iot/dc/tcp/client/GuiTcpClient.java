/**
 * 
 */
package com.hpe.iot.dc.tcp.client;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.client.device.counter.DeviceIdCounter;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.runner.ClientSocketHandlerRunner;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientSocketEnvironment;
import com.hpe.iot.dc.tcp.client.runner.handler.ClientSocketHandler;
import com.hpe.iot.dc.tcp.client.settings.ClientSettings;
import com.hpe.iot.dc.tcp.client.settings.reader.ClientType;
import com.hpe.iot.dc.tcp.client.settings.reader.SettingsReader;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManager;
import com.hpe.iot.dc.tcp.client.socket.ClientSocketManagerImpl;
import com.hpe.iot.dc.tcp.client.writer.ClientSocketWriter;

/**
 * @author sveera
 *
 */
public class GuiTcpClient {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void runClient(final ClientMessageGenerator messageGenerator,
			final ClientMessageConsumer severToClientMessageGenerator, GUI gui) throws IOException {
		ClientSettings clientSettings = new SettingsReader().readSettings(ClientType.GUI);
		logger.info("Using client Settings :- " + clientSettings);
		logger.info("Connecting Clients");
		ExecutorService executorService = Executors.newFixedThreadPool(clientSettings.getNoOfClientRunners() * 3 + 1);
		ClientEnvironment clientEnvironment = connectClientForPluginScript(executorService, clientSettings,
				messageGenerator, severToClientMessageGenerator);
		executorService.submit(clientEnvironment.getClientSocketRunner());
		JFrame application = gui.getApplicationFrame();
		Map<JButton, String> buttonConfigurations = gui.getButtonConfiguration();
		gui.getDeviceId().setText(Long.toString(clientSettings.getStaringDeviceId()));
		bindButtonConfigurations(buttonConfigurations, clientEnvironment, executorService);
		bindOnCloseOperation(application, clientEnvironment, executorService);
	}

	protected ClientEnvironment connectClientForPluginScript(final ExecutorService executorService,
			final ClientSettings clientSettings, final ClientMessageGenerator severToClientMessageGenerator,
			ClientMessageConsumer clientToServerMessageGenerator) throws IOException {
		final DeviceIdCounter deviceIdCounter = new DeviceIdCounter(clientSettings.getStaringDeviceId(),
				clientSettings.getClientRunnerSettings().getNoOfClients() * clientSettings.getNoOfClientRunners());
		ClientSocketManagerImpl clientSocketManagerImpl = new ClientSocketManagerImpl(
				clientSettings.getClientRunnerSettings(), deviceIdCounter, 1);
		ClientSocketEnvironment clientSocketEnvironment = new ClientSocketEnvironment(
				clientSettings.getClientRunnerSettings(), clientSocketManagerImpl, clientSocketManagerImpl,
				severToClientMessageGenerator, clientToServerMessageGenerator, 1);
		ClientSocketHandlerRunner clientSocketRunner = new ClientSocketHandlerRunner(
				new ClientSocketHandler(clientSocketEnvironment, executorService));
		return new ClientEnvironment(clientSocketManagerImpl, clientSocketEnvironment, clientSocketRunner);
	}

	private void bindButtonConfigurations(Map<JButton, String> buttonConfigurations,
			ClientEnvironment clientEnvironment, ExecutorService executorService) {
		Set<JButton> allButtons = buttonConfigurations.keySet();
		enableleAllButtons(allButtons, false);
		executorService.submit(() -> {
			while (clientEnvironment.getClientSocketManager().getHandshakedClients().size() == 0)
				;
			for (Map.Entry<JButton, String> buttonConfiguration : buttonConfigurations.entrySet()) {
				JButton actionButton = buttonConfiguration.getKey();
				actionButton.addActionListener((actionEvent) -> {
					ClientSocketWriter clientSocketWriter = clientEnvironment.getClientSocketEnvironment()
							.getClientSocketWriter();
					try {
						clientSocketWriter.pushDataUsingClients(buttonConfiguration.getValue(),
								clientEnvironment.getClientSocketManager().getHandshakedClients().get(0));
					} catch (Exception e) {
						logExceptionStackTrace(e, getClass());
					}
				});
			}
			enableleAllButtons(allButtons, true);
		});
	}

	private void enableleAllButtons(Set<JButton> allButtons, boolean isEnabled) {
		for (JButton button : allButtons)
			button.setEnabled(isEnabled);
	}

	private void bindOnCloseOperation(JFrame application, ClientEnvironment clientEnvironment,
			ExecutorService executorService) {
		application.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stopAllClientSocketRunners(clientEnvironment.getClientSocketRunner());
				executorService.shutdown();
				e.getWindow().dispose();
			}
		});
	}

	protected void stopAllClientSocketRunners(ClientSocketHandlerRunner... clientSocketHandlerRunners) {
		for (ClientSocketHandlerRunner clientSocketHandlerRunner : clientSocketHandlerRunners)
			clientSocketHandlerRunner.stopConnectedClients();
	}

	private class ClientEnvironment {

		private final ClientSocketManager clientSocketManager;
		private final ClientSocketEnvironment clientSocketEnvironment;
		private final ClientSocketHandlerRunner clientSocketRunner;

		public ClientEnvironment(ClientSocketManager clientSocketManager,
				ClientSocketEnvironment clientSocketEnvironment, ClientSocketHandlerRunner clientSocketRunner) {
			super();
			this.clientSocketManager = clientSocketManager;
			this.clientSocketEnvironment = clientSocketEnvironment;
			this.clientSocketRunner = clientSocketRunner;
		}

		public ClientSocketManager getClientSocketManager() {
			return clientSocketManager;
		}

		public ClientSocketEnvironment getClientSocketEnvironment() {
			return clientSocketEnvironment;
		}

		public ClientSocketHandlerRunner getClientSocketRunner() {
			return clientSocketRunner;
		}

	}

}
