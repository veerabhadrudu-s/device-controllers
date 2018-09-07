package com.hpe.iot.dc.tcp.southbound.socketpool.factory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.hpe.iot.dc.tcp.southbound.model.AbstractServerSocketToDeviceModel;

public class TCPServerClientSocketPoolFactoryImplTest {

	private TCPServerClientSocketPoolFactoryImpl tcpServerClientSocketPoolFactoryImpl;
	private final ServerSocketToDeviceModelStub1 serverSocketToDeviceModelStub1 = new ServerSocketToDeviceModelStub1();
	private final ServerSocketToDeviceModelStub2 serverSocketToDeviceModelStub2 = new ServerSocketToDeviceModelStub2();

	@BeforeEach
	public void setUp() throws Exception {
		tcpServerClientSocketPoolFactoryImpl = new TCPServerClientSocketPoolFactoryImpl(Collections.emptyList());
	}

	@Test
	@DisplayName("test is Not Null")
	public void testTCPServerClientSocketPoolFactoryImpl() {
		assertNotNull(tcpServerClientSocketPoolFactoryImpl);
	}

	@Test
	@DisplayName("test Get ServerClientSocketPool")
	public void testGetServerClientSocketPool() throws IOException {
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub1));
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub1));
		assertEquals(1, tcpServerClientSocketPoolFactoryImpl.getDeviceModelToSocketPool().size());
	}

	@Test
	@DisplayName("test Remove ServerClientSocketPool")
	public void testRemoveServerClientSocketPool() throws IOException {
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub1));
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub2));
		assertTrue(tcpServerClientSocketPoolFactoryImpl.removeServerClientSocketPool(serverSocketToDeviceModelStub1));
		assertEquals(1, tcpServerClientSocketPoolFactoryImpl.getDeviceModelToSocketPool().size());
	}

	@Test
	@DisplayName("test Get DeviceModelToSocketPool")
	public void testGetDeviceModelToSocketPool() throws IOException {
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub1));
		assertNotNull(tcpServerClientSocketPoolFactoryImpl.getServerClientSocketPool(serverSocketToDeviceModelStub2));
		tcpServerClientSocketPoolFactoryImpl.getDeviceModelToSocketPool().remove(serverSocketToDeviceModelStub1);
		tcpServerClientSocketPoolFactoryImpl.getDeviceModelToSocketPool().remove(serverSocketToDeviceModelStub2);
		assertEquals(2, tcpServerClientSocketPoolFactoryImpl.getDeviceModelToSocketPool().size());
	}

	private class ServerSocketToDeviceModelStub1 extends AbstractServerSocketToDeviceModel {

		private final String manufacturer = "MMI";
		private final String modelId = "Drivemate";
		private final String version = "1";

		@Override
		public String getBoundLocalAddress() {
			return null;
		}

		@Override
		public int getPortNumber() {
			return 0;
		}

		@Override
		public String getManufacturer() {
			return manufacturer;
		}

		@Override
		public String getModelId() {
			return modelId;
		}

		@Override
		public String getVersion() {
			return version;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((getManufacturer() == null) ? 0 : getManufacturer().hashCode());
			result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
			result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ServerSocketToDeviceModelStub1 other = (ServerSocketToDeviceModelStub1) obj;
			if (getManufacturer() == null) {
				if (other.getManufacturer() != null)
					return false;
			} else if (!getManufacturer().equals(other.getManufacturer()))
				return false;
			if (getModelId() == null) {
				if (other.getModelId() != null)
					return false;
			} else if (!getModelId().equals(other.getModelId()))
				return false;
			if (getVersion() == null) {
				if (other.getVersion() != null)
					return false;
			} else if (!getVersion().equals(other.getVersion()))
				return false;
			return true;
		}

	}

	private class ServerSocketToDeviceModelStub2 extends ServerSocketToDeviceModelStub1 {

		@Override
		public String getModelId() {
			return "Safemate";
		}
	}

}
