/**
 * 
 */
package com.hpe.iot.dc.mmi.safemate.client

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientToServerMessageGenerator
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMISeverToClientMessageGenerator
import com.hpe.iot.dc.tcp.client.GuiTcpClient

/**
 * @author sveera
 *
 */
class GUIMain {

	public static void main(String[] args) throws IOException {
		println ("Starting the TCP Client. ");
		println("Console output will be redirected to file : " + System.getProperty("user.dir")
				+ File.separator + "tcpClient.log");
		println();
		println();		
		new GuiTcpClient().runClient(new MMIClientToServerMessageGenerator(new MMICRCAlgorithm()),
				new MMISeverToClientMessageGenerator(),new MMIGUI());		
	}
}
