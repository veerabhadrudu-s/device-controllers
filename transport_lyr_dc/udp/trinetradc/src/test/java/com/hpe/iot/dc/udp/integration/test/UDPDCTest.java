/**
 * 
 */
package com.hpe.iot.dc.udp.integration.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.service.factory.MessageServiceFactory;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;
import com.hpe.iot.dc.udp.southbound.service.impl.UDPDataReceiver;

import static org.junit.Assert.assertNotNull;

/**
 * @author sveera
 *
 */
public class UDPDCTest {

	private ClassPathXmlApplicationContext applicationContext;

	@Before
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("spring" + File.separator + "bean-config.xml");
	}

	@Test
	public void testUDPDCInitializer() {
		UDPDataReceiver udpDataReceiver = applicationContext.getBean(UDPDataReceiver.class);
		UplinkDeviceDataConverterFactory uplinkDeviceDataConverterFactory = applicationContext
				.getBean(UplinkDeviceDataConverterFactory.class);
		MessageServiceFactory messageServiceFactory = applicationContext
				.getBean(MessageServiceFactory.class);
		assertNotNull("UDPDataReceiver Cannot be null ", udpDataReceiver);
		assertNotNull("UplinkDeviceDataConverter cannot be null",
				uplinkDeviceDataConverterFactory.getModelConverter("#"));
		assertNotNull("MessageService cannot be null",
				messageServiceFactory.getMessageService("#"));
		applicationContext.close();
	}

}
