package com.hpe.iot.dc.tcp.southbound.service.outflow;

import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToHexString;
import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;

/**
 * @author sveera
 *
 */
public class TCPServerSocketWriter {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ServerClientSocketPool tcpServerClientSocketPool;

	public TCPServerSocketWriter(ServerClientSocketPool tcpServerClientSocketPool) {
		super();
		this.tcpServerClientSocketPool = tcpServerClientSocketPool;
	}

	public void sendMessage(final Device device, byte[] message) {
		logger.debug("Received request to write on device for device Id " + device + " with Data in hex format is "
				+ convertArrayOfByteToHexString(message));
		logger.debug("Received request to write on device for device Id " + device + " with Data in decimal byte format is "
				+ convertArrayOfByteToString(message));		
		ByteBuffer readBuffer = readIntoBuffer(message);
		writeonClientSocket(device, readBuffer, message);
	}

	private ByteBuffer readIntoBuffer(byte[] message) {
		ByteBuffer readBuffer = ByteBuffer.allocate(message.length);
		readBuffer.put(message);
		readBuffer.flip();
		return readBuffer;
	}

	private void writeonClientSocket(final Device device, ByteBuffer readBuffer, byte[] message) {
		SocketChannel clientSocketChannel = tcpServerClientSocketPool.getClientSocket(device);
		logger.trace("Identified Socket channel from Client Socket Pool for Device ID " + device + " is "
				+ clientSocketChannel);
		if (clientSocketChannel != null && clientSocketChannel.isConnected())
			try {
				clientSocketChannel.write(readBuffer);
				logger.debug("Completed writing on to client socket " + clientSocketChannel.toString()
						+ " with message " + convertArrayOfByteToString(message));
			} catch (Exception e) {
				logger.error("Failed to write message on client socket " + clientSocketChannel.toString());
				logExceptionStackTrace(e, getClass());
			}
	}

}
