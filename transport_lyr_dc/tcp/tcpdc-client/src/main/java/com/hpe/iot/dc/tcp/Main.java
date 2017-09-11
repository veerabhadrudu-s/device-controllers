package com.hpe.iot.dc.tcp;

import java.io.File;
import java.io.IOException;

import com.hpe.iot.dc.tcp.client.CliTcpClient;
import com.hpe.iot.dc.tcp.client.payload.converter.impl.SampleClientToServerMessageGenerator;

/**
 * @author sveera
 *
 */
public class Main {

	public static void main(String args[]) throws IOException {
		System.out.println("Starting the TCP Client. ");
		System.out.println("Console output will be redirected to file : " + System.getProperty("user.dir")
				+ File.separator + "tcpClient.log");
		System.out.println();
		System.out.println();
		System.out.println("Press any Key and Enter to stop the client ..... ");
		new CliTcpClient().runClient(new SampleClientToServerMessageGenerator(), null);
		System.out.println(" TCP Client program Stopped . Please wait for JVM to Stop.");
	}
}
