/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.client;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

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
public class MMIVT15GUI implements GUI {

	private JFrame frmMmiDeepInstalled;
	private Map<JButton, String> buttonConfigurations = new HashMap<>();
	private JLabel deviceIdLbl;

	public MMIVT15GUI() {
		initialize();
	}

	private void initialize() {
		frmMmiDeepInstalled = new JFrame();
		frmMmiDeepInstalled.setTitle("MMI Deep installed vehicle tracker Client");
		frmMmiDeepInstalled.getContentPane().setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		frmMmiDeepInstalled.setBounds(100, 100, 482, 443);
		frmMmiDeepInstalled.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMmiDeepInstalled.getContentPane().setLayout(null);
		frmMmiDeepInstalled.setResizable(false);
		JLabel lblNewLabel = new JLabel("MMI VT15");
		lblNewLabel.setForeground(new Color(153, 51, 255));
		lblNewLabel.setFont(new Font("Garamond", Font.BOLD, 35));
		lblNewLabel.setBounds(145, 40, 189, 66);
		JLabel lblDeviceId = new JLabel("Device ID :");
		lblDeviceId.setFont(new Font("Verdana", Font.BOLD, 22));
		lblDeviceId.setBounds(49, 136, 148, 47);
		frmMmiDeepInstalled.getContentPane().add(lblDeviceId);
		deviceIdLbl = new JLabel("");
		deviceIdLbl.setFont(new Font("Tahoma", Font.PLAIN, 22));
		deviceIdLbl.setBounds(227, 136, 204, 47);
		frmMmiDeepInstalled.getContentPane().add(deviceIdLbl);
		
		JButton serialDataButton = new JButton("Serial Data");
		serialDataButton.setForeground(new Color(65, 105, 225));
		serialDataButton.setFont(new Font("Verdana", Font.PLAIN, 30));
		serialDataButton.setBounds(10, 227, 222, 56);
		frmMmiDeepInstalled.getContentPane().add(lblNewLabel);
		frmMmiDeepInstalled.getContentPane().add(serialDataButton);
		buttonConfigurations.put(serialDataButton, "serial_packet");
		
		JButton bulkHistoricalButton = new JButton("Bulk Data");
		bulkHistoricalButton.setForeground(new Color(65, 105, 225));
		bulkHistoricalButton.setFont(new Font("Verdana", Font.PLAIN, 30));
		bulkHistoricalButton.setBounds(244, 227, 222, 56);
		buttonConfigurations.put(bulkHistoricalButton, "bulk_tracking_packet");
		frmMmiDeepInstalled.getContentPane().add(bulkHistoricalButton);
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