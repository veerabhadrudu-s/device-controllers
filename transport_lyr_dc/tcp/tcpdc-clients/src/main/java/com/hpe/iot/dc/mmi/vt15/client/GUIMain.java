/**
 * 
 */
package com.hpe.iot.dc.mmi.vt15.client;

import java.io.File;
import java.io.IOException;

import com.hpe.iot.dc.mmi.vt15.tcp.client.payload.converters.MMIVT15ClientToServerMessageGenerator;
import com.hpe.iot.dc.tcp.client.GuiTcpClient;

/**
 * @author sveera
 *
 */
class GUIMain {

	public static void main(String[] args) throws IOException {
		System.out.println("Starting the TCP Client. ");
		System.out.println("Console output will be redirected to file : " + System.getProperty("user.dir")
				+ File.separator + "tcpClient.log");
		System.out.println();
		System.out.println();
		new GuiTcpClient().runClient(new MMIVT15ClientToServerMessageGenerator(), null, new MMIVT15GUI());
	}
}
