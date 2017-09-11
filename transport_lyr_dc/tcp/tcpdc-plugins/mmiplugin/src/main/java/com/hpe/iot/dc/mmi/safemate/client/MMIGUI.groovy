/**
 * 
 */
package com.hpe.iot.dc.mmi.safemate.client;

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
public class MMIGUI implements GUI {

	private JFrame frame;
	private Map<JButton, String> buttonConfigurations = new HashMap<>();
	private JLabel deviceIdLbl;

	public MMIGUI() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 14));
		frame.setBounds(100, 100, 250, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		JLabel lblNewLabel = new JLabel("MMI Safemate");
		lblNewLabel.setForeground(new Color(153, 51, 255));
		lblNewLabel.setFont(new Font("Gabriola", Font.BOLD, 35));
		lblNewLabel.setBounds(28, 40, 189, 66);		
		JButton sosButton = new JButton("SOS");
		sosButton.setForeground(Color.RED);
		sosButton.setFont(new Font("Verdana", Font.PLAIN, 30));
		sosButton.setBounds(52, 166, 143, 57);
		frame.getContentPane().add(lblNewLabel);
		frame.getContentPane().add(sosButton);
		buttonConfigurations.put(sosButton, "0x4203");
		
		JLabel lblDeviceId = new JLabel("Device ID :");
		lblDeviceId.setFont(new Font("Verdana", Font.BOLD, 12));
		lblDeviceId.setBounds(28, 105, 82, 16);
		frame.getContentPane().add(lblDeviceId);
		
		deviceIdLbl= new JLabel("");
		deviceIdLbl.setBounds(111, 107, 123, 14);
		frame.getContentPane().add(deviceIdLbl);
		EventQueue.invokeLater({
			try {
				frame.setVisible(true);
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
		return frame;
	}

	@Override
	public JLabel getDeviceId() {
		return deviceIdLbl;
	}
}
