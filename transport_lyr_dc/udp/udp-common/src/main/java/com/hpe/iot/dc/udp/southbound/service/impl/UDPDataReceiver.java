package com.hpe.iot.dc.udp.southbound.service.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.southbound.service.inflow.activator.SouthBoundServiceActivator;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.udp.model.UDPDevice;
import com.hpe.iot.dc.udp.model.impl.UDPDeviceImpl;
import com.hpe.iot.dc.udp.southbound.service.UDPDatagramServiceProvider;

@Service
@DependsOn("udpDatagramServiceProvider")
public class UDPDataReceiver {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final UDPDatagramServiceProvider udpDatagramServiceProvider;
	private final UplinkDataModelTransformer dataModelTransformer;
	private final SouthBoundServiceActivator serviceActivator;
	private final DatagramSocketListener datagramSocketListener;
	private final ManagedExecutorService managedExecutorService;
	private final DeviceModel deviceModel;

	@Autowired
	public UDPDataReceiver(DeviceModel deviceModel, UDPDatagramServiceProvider udpDatagramServiceProvider,
			UplinkDataModelTransformer dataModelTransformer, SouthBoundServiceActivator serviceActivator,
			ManagedExecutorService managedExecutorService) {
		this.udpDatagramServiceProvider = udpDatagramServiceProvider;
		this.dataModelTransformer = dataModelTransformer;
		this.serviceActivator = serviceActivator;
		this.managedExecutorService = managedExecutorService;
		this.datagramSocketListener = new DatagramSocketListener();
		this.deviceModel = deviceModel;

	}

	@PostConstruct
	public void intializeUdpPortListning() {
		managedExecutorService.execute(datagramSocketListener);
	}

	@PreDestroy
	public void clearFlag() {
		datagramSocketListener.resetPortListeningFlag();
	}

	private class DatagramSocketListener implements Runnable {

		private volatile boolean isPortListening;
		private DatagramSocket datagramSocket;

		public DatagramSocketListener() {
			super();
			this.isPortListening = true;
			datagramSocket = udpDatagramServiceProvider.getDatagramSocket();
		}

		public void run() {
			while (isPortListening) {
				readDataFromSocket();
			}
		}

		private void readDataFromSocket() {
			try {
				readDataUsingDatagramPacket();
			} catch (IOException e) {
				logger.error("Error processing UDP message", e);
			} catch (Throwable e) {
				logger.error("Error processing UDP message", e);
			}

		}

		private void readDataUsingDatagramPacket() throws IOException {
			DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
			datagramSocket.receive(datagramPacket);
			logger.trace("Datagram Packet received from IP address " + datagramPacket.getAddress());
			logger.trace("Datagram Packet received from port " + datagramPacket.getPort());
			byte[] readData = datagramPacket.getData();
			UDPDevice udpDevice = new UDPDeviceImpl(deviceModel.getManufacturer(), deviceModel.getModelId(),
					deviceModel.getVersion(), datagramPacket.getAddress(), datagramPacket.getPort());
			List<DeviceInfo> dataFrames = dataModelTransformer.convertToModel(udpDevice, readData);
			for (DeviceInfo dataFrame : dataFrames)
				serviceActivator.processMessage(dataFrame);
		}

		public void resetPortListeningFlag() {
			isPortListening = false;
		}
	}
}
