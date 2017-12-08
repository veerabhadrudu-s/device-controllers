/**
 * 
 */
package com.hpe.iot.dc.sample.valid.client;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.hpe.iot.dc.tcp.client.GUI;

/**
 * @author sveera
 *
 */
public class SampleGUI implements GUI {

	private JFrame frmMmiDeepInstalled;
	private Map<JButton, String> buttonConfigurations = new HashMap<>();
	private JLabel deviceIdLbl;

	public SampleGUI() {
		initialize();
	}

	private void initialize() {
		frmMmiDeepInstalled = new JFrame();
		frmMmiDeepInstalled.setTitle("TCP Sample Client");
		frmMmiDeepInstalled.getContentPane().setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		frmMmiDeepInstalled.setBounds(100, 100, 482, 443);
		frmMmiDeepInstalled.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMmiDeepInstalled.getContentPane().setLayout(null);
		frmMmiDeepInstalled.setResizable(false);
		JLabel lblNewLabel = new JLabel("Sample TCP Client\r\n");
		lblNewLabel.setForeground(new Color(153, 51, 255));
		lblNewLabel.setFont(new Font("Garamond", Font.BOLD, 35));
		lblNewLabel.setBounds(71, 42, 346, 66);
		JLabel lblDeviceId = new JLabel("Device ID :");
		lblDeviceId.setFont(new Font("Verdana", Font.BOLD, 22));
		lblDeviceId.setBounds(49, 136, 148, 47);
		frmMmiDeepInstalled.getContentPane().add(lblDeviceId);
		deviceIdLbl = new JLabel("");
		deviceIdLbl.setFont(new Font("Tahoma", Font.PLAIN, 22));
		deviceIdLbl.setBounds(227, 136, 204, 47);
		frmMmiDeepInstalled.getContentPane().add(deviceIdLbl);
		frmMmiDeepInstalled.getContentPane().add(lblNewLabel);
		JLabel lblNewLabel_1 = new JLabel(
				"<html><body>This Sample Client immediately sends uplink data after socket is connected with server.<br/>"
						+ "This client doesn't have any handshake message type.<br/>"
						+ "To verify the connectivity status check the logs.</body></html>");
		lblNewLabel_1.setForeground(Color.RED);
		lblNewLabel_1.setFont(new Font("Verdana", Font.BOLD, 15));
		lblNewLabel_1.setBounds(36, 194, 395, 210);
		frmMmiDeepInstalled.getContentPane().add(lblNewLabel_1);
		EventQueue.invokeLater(() -> {
			try {
				frmMmiDeepInstalled.setVisible(true);
			} catch (Exception e) {
				logExceptionStackTrace(e, getClass());
			}
		});
	}

	@Override
	public Map<JButton, String> getButtonConfiguration() {
		return buttonConfigurations;
	}

	@Override
	public JFrame getApplicationFrame() {
		return frmMmiDeepInstalled;
	}

	@Override
	public JLabel getDeviceId() {
		return deviceIdLbl;
	}
}
