/**
 * 
 */
package com.hpe.iot.dc.tcp.client;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author sveera
 *
 */
public interface GUI {

	Map<JButton,String> getButtonConfiguration();
	
	JFrame getApplicationFrame();
	
	JLabel getDeviceId();
	
}
