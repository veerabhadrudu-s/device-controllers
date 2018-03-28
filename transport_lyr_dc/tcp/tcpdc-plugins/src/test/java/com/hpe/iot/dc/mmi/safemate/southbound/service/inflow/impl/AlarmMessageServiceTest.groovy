package com.hpe.iot.dc.mmi.safemate.southbound.service.inflow.impl;

import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.ALARM_MESSAGE_HEX
import static com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection.SAFEMATE_DEVICE_MODEL
import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock;

import com.handson.logger.impl.LiveLoggerAdapter
import com.handson.logger.service.impl.Slf4jLoggerServiceAdaptee
import com.hpe.iot.dc.mmi.safemate.AlarmMessageConverter
import com.hpe.iot.dc.mmi.safemate.AlarmMessageService
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm
import com.hpe.iot.dc.mmi.safemate.MMIServerSocketToDeviceModel
import com.hpe.iot.dc.mmi.safemate.TrackerInfoCreator
import com.hpe.iot.dc.mmi.safemate.testdata.MMITestDataCollection
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModelImpl
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel

/**
 * @author sveera
 *
 */
public class AlarmMessageServiceTest {

	private static final String MESSAGE_TYPE = "0x4203";
	private static final String CONTAINER_NAME = "alarm";
	private MMICRCAlgorithm mmicrcAlgorithm = new MMICRCAlgorithm();
	private TrackerInfoCreator trackerInfoCreator = new TrackerInfoCreator();
	private AlarmMessageService alarmMessageService;
	@Mock
	private IOTPublisherHandler iotPublisherHandler;

	@BeforeEach
	public void setUp() throws Exception {
		initMocks(this);
		/*doAnswer(new MockIOTRequestResponseHandler()).when(requestResponseHandler)
		 .sendRequest(Mockito.any(RequestPrimitive.class));*/
		ServerSocketToDeviceModel serverSocketToDeviceModel=new MMIServerSocketToDeviceModel();
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, new DefaultIOTModelConverterImpl(new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),serverSocketToDeviceModel.getModelId(),serverSocketToDeviceModel.getVersion())));
		alarmMessageService = new AlarmMessageService(iotPublisherService,
				new LiveLoggerAdapter( new Slf4jLoggerServiceAdaptee(),SAFEMATE_DEVICE_MODEL));
	}

	@Test
	public void testGetMessageType() {
		assertEquals(MESSAGE_TYPE,
				alarmMessageService.getMessageType(),"Expected Message Type and Actual Message Type are not same");
	}

	@Test
	public void testExecuteService() {
		AlarmMessageConverter alarmMessageConverter = new AlarmMessageConverter(mmicrcAlgorithm, trackerInfoCreator);
		DeviceInfo deviceInfo = alarmMessageConverter
				.createModel(new MMIServerSocketToDeviceModel(),createDecimalPayloadFromHexaPayload(ALARM_MESSAGE_HEX, getClass()));
		DeviceDataDeliveryStatus deviceDataDeliveryStatus = alarmMessageService.executeService(deviceInfo);
		assertNotNull(deviceDataDeliveryStatus,"Failed to execute NotificationMessageService");
	}
}
