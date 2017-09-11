package com.hpe.iot.dc.trinetra.southbound.service.inflow.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.trinetra.dao.impl.InMemoryDeviceDaoRepository;
import com.hpe.iot.dc.trinetra.model.DeviceAddress;
import com.hpe.iot.dc.trinetra.southbound.converter.impl.test.TestData;
import com.hpe.iot.dc.udp.southbound.service.impl.UDPDatagramSender;

/**
 * @author sveera
 *
 */
public class IpAddressMessageServiceTest {

	private IpAddressMessageService ipAddressOperationService;

	private InMemoryDeviceDaoRepository inMemoryDeviceDaoRepository = new InMemoryDeviceDaoRepository();

	@Mock
	private UDPDatagramSender udpDatagramSender;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Mockito.doNothing().when(udpDatagramSender).sendUDPDatagram(Mockito.anyString(), Mockito.anyInt(),
				Mockito.any(byte[].class));
		ipAddressOperationService = new IpAddressMessageService(udpDatagramSender, inMemoryDeviceDaoRepository);
	}

	@Test
	public void testGetOperator() {
		Assert.assertSame("Expected and actual Operators are not same", "#",
				ipAddressOperationService.getMessageType());
	}

	@Test
	public void testExecuteService() {
		DeviceInfo dataModel = new DeviceInfo(new DeviceImpl(TestData.TRINETRA, TestData.VEHICAL_TRACKING, "006945"),
				"#", new byte[10]);
		DeviceAddress deviceAddress = new DeviceAddress("100.72.84.192", "4038");
		dataModel.getDeviceData().put(DeviceAddress.DEVICE_ADDRESS, deviceAddress);
		ipAddressOperationService.executeService(dataModel);
	}

}
