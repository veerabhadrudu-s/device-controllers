/**
 * 
 */
package com.hpe.iot.dc.client;

import java.io.File;
import java.io.IOException;

import com.hpe.iot.dc.tcp.client.GUI;
import com.hpe.iot.dc.tcp.client.GuiTcpClient;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public abstract class GuiMainTemplateMethod {

	public void main() throws IOException {
		System.out.println("Starting the TCP Client. ");
		System.out.println("Console output will be redirected to file : " + System.getProperty("user.dir")
				+ File.separator + "tcpClient.log");
		System.out.println();
		System.out.println();
		new GuiTcpClient().runClient(getClientMessageGenerator(), getClientMessageConsumer(), getGUI());
		System.out.println(" TCP Client program Stopped . Please wait for JVM to Stop.");
	}

	protected abstract ClientMessageGenerator getClientMessageGenerator();

	protected abstract ClientMessageConsumer getClientMessageConsumer();

	protected abstract GUI getGUI();
}
