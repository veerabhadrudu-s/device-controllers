package com.hpe.iot.dc.mmi.safemate.southbound.service.inflow.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.NOTIFICATION_MESSAGE_HEX
import static com.hpe.iot.dc.util.DataParserUtility.createBinaryPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.mockito.Mockito.doAnswer;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock;
import org.mockito.Mockito;

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.NotificationMessageConverter
import com.hpe.iot.dc.mmi.safemate.NotificationMessageService
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.northbound.mock.MockIOTRequestResponseHandler
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModelImpl
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel
import com.hpe.iot.m2m.common.RequestPrimitive;

/**
 * @author sveera
 *
 */
public class NotificationMessageServiceTest {

	private static final String MESSAGE_TYPE = "0x4206";
	private static final String CONTAINER_NAME = "notification";
	private MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private TrackerInfoCreator trackerInfoCreator = new TrackerInfoCreator();
	private NotificationMessageService notificationMessageService;
	@Mock
	private IOTPublisherHandler iotPublisherHandler;

	@BeforeEach
	public void setUp() throws Exception {
		initMocks(this);
		doAnswer(new MockIOTRequestResponseHandler()).when(iotPublisherHandler)
				.sendDataToIot(Mockito.any(RequestPrimitive.class));
		ServerSocketToDeviceModel serverSocketToDeviceModel=new MMIServerSocketToDeviceModel();
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, new DefaultIOTModelConverterImpl(new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),serverSocketToDeviceModel.getModelId(),serverSocketToDeviceModel.getVersion())));
		notificationMessageService = new NotificationMessageService(iotPublisherService);
	}

	@Test
	public void testGetMessageType() {
		assertEquals(MESSAGE_TYPE,notificationMessageService.getMessageType(),
				"Expected Message Type and Actual Message Type are not same");
	}

	@Test
	public void testExecuteService() {
		NotificationMessageConverter ipAddressMetaModelConverter = new NotificationMessageConverter(mmicrcAlgorithm,
				trackerInfoCreator);
		DeviceInfo deviceInfo = ipAddressMetaModelConverter
				.createModel(new MMIServerSocketToDeviceModel(),createBinaryPayloadFromHexaPayload(NOTIFICATION_MESSAGE_HEX, getClass()));
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = notificationMessageService.executeService(deviceInfo);
		assertNotNull(deviceDataDeliveryStatus,"Failed to execute NotificationMessageService");
	}
}
