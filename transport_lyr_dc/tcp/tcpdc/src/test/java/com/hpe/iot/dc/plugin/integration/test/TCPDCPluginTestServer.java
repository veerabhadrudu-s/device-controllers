package com.hpe.iot.dc.plugin.integration.test;

import static javax.swing.BoxLayout.Y_AXIS;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.manager.TCPServerSocketServiceManager;
import com.hpe.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public class TCPDCPluginTestServer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ClassPathXmlApplicationContext applicationContext;

	@Before
	@Ignore
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("bean-config.xml");
	}

	@Test
	@Ignore
	public void testAllTCPDCPlugins() {
		TCPServerSocketServiceManager tcpServerSocketServiceManager = applicationContext
				.getBean(TCPServerSocketServiceManager.class);
		Assert.assertNotNull("TCPDCInitializer Cannot be null ", tcpServerSocketServiceManager);
		EventQueue.invokeLater(() -> {
			TCPServerSocketServiceManagerController ex = new TCPServerSocketServiceManagerController(
					tcpServerSocketServiceManager);
			ex.setVisible(true);
		});
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		scanner.close();
		applicationContext.close();
	}

	private class TCPServerSocketServiceManagerController extends JFrame {

		private static final long serialVersionUID = 1L;

		private final TCPServerSocketServiceManager tcpServerSocketServiceManager;

		public TCPServerSocketServiceManagerController(TCPServerSocketServiceManager tcpServerSocketServiceManager)
				throws HeadlessException {
			super();
			this.tcpServerSocketServiceManager = tcpServerSocketServiceManager;
			initUI();
		}

		private void initUI() {
			showRunningServices(tcpServerSocketServiceManager.getRunningServerSocketServices());
			setTitle("TCP Server Socket Service Monitor ");
			pack();
			setLocationRelativeTo(null);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}

		private void showRunningServices(Set<ServerSocketToDeviceModel> runningServerSocketServices) {
			List<JComponent> allServicesStopButtons = new ArrayList<>();
			for (ServerSocketToDeviceModel serverSocketToDeviceModel : runningServerSocketServices) {
				JButton stopServiceButton = new JButton("Stop Service " + serverSocketToDeviceModel);
				stopServiceButton.setAlignmentX(CENTER_ALIGNMENT);
				addButtonActionListener(serverSocketToDeviceModel, stopServiceButton);
				allServicesStopButtons.add(stopServiceButton);
			}
			createLayout(allServicesStopButtons);
		}

		private void addButtonActionListener(ServerSocketToDeviceModel serverSocketToDeviceModel,
				JButton stopServiceButton) {
			stopServiceButton.addActionListener((ActionEvent event) -> {
				try {
					tcpServerSocketServiceManager.removeTCPServerSocketService(serverSocketToDeviceModel);
				} catch (Exception e) {
					logger.error("Failed to stop Service " + serverSocketToDeviceModel);
					UtilityLogger.logExceptionStackTrace(e, getClass());
				} finally {
					stopServiceButton.setEnabled(false);
				}
			});
		}

		private void createLayout(List<? extends JComponent> allServicesStopButtons) {
			Container containerPane = getContentPane();
			BoxLayout boxLayout = new BoxLayout(containerPane, Y_AXIS);
			containerPane.setLayout(boxLayout);
			for (int buttonIndex = 0; buttonIndex < allServicesStopButtons.size(); buttonIndex++) {
				containerPane.add(allServicesStopButtons.get(buttonIndex));
			}
		}

	}
}
