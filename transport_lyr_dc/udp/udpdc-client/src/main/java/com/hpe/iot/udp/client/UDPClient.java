/**
 * 
 */
package com.hpe.iot.udp.client;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author sveera
 *
 */
public class UDPClient {

	public static void main(String args[]) throws SocketException {
		
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
			Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
			while (inetAddresses.hasMoreElements()) {
				InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
				System.out.println("Network interface with name " + networkInterface.getName() + " has ip "
						+ inetAddress.getHostAddress());
			}
		}
	}

}
