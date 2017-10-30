/**
 * 
 */
package com.hpe.iot.kafka.northbound.sdk.handler.mock;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.m2m.common.ContentInstance;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.model.DeviceInfo;

/**
 * @author sveera
 *
 */
public class MockNorthboundUplinkConsumerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String destination;
	private final BrokerConsumerService<String> brokerConsumerService;
	private final IOTDevicePayloadHolder iotDevicePayloadHolder;

	public MockNorthboundUplinkConsumerService(String destination, BrokerConsumerService<String> brokerConsumerService,
			IOTDevicePayloadHolder iotDevicePayloadHolder) {
		super();
		this.destination = destination;
		this.brokerConsumerService = brokerConsumerService;
		this.iotDevicePayloadHolder = iotDevicePayloadHolder;
	}

	public void startService() {
		brokerConsumerService.consumeData(destination, new IOTConsumerDataHandlerImpl());
		brokerConsumerService.startService();
	}

	public void stopService() {
		brokerConsumerService.stopService();
	}

	private class IOTConsumerDataHandlerImpl implements BrokerConsumerDataHandler<String> {

		private final JsonParser parser = new JsonParser();
		private Unmarshaller unmarshaller;

		public IOTConsumerDataHandlerImpl() {
			super();
		}

		@Override
		public void handleConsumerMessage(String destination, String consumerData) {
			logger.debug("Received payload in " + this.getClass().getSimpleName() + " is " + consumerData);
			if (unmarshaller == null)
				initializeUnmarshaller();
			try {
				RequestPrimitive requestPrimitive = (RequestPrimitive) unmarshaller
						.unmarshal(new StringReader(consumerData));
				List<Object> allObjects = requestPrimitive.getPrimitiveContent().getAny();
				for (Object object : allObjects)
					handleContentInstanceObject(object);
			} catch (JAXBException e) {
				throw new RuntimeException("Failed to process device payload " + this.getClass(), e);
			}
		}

		private void handleContentInstanceObject(Object object) {
			if (object instanceof ContentInstance && ((ContentInstance) object).getContent() instanceof String)
				iotDevicePayloadHolder.holdIOTDeviceData(
						constructDeviceInfoFromJsonPayload((String) ((ContentInstance) object).getContent()));
		}

		private DeviceInfo constructDeviceInfoFromJsonPayload(String deviceInfoJsonPayload) {
			JsonObject downlinkPayload = parser.parse(deviceInfoJsonPayload).getAsJsonObject();
			JsonObject device = downlinkPayload.get("device").getAsJsonObject();
			return new DeviceInfo(
					new DeviceImpl(device.get("manufacturer").getAsString(), device.get("modelId").getAsString(), "1.0",
							device.get("deviceId").getAsString()),
					downlinkPayload.get("messageType").getAsString(), downlinkPayload.get("payload").getAsJsonObject());
		}

		private void initializeUnmarshaller() {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(RequestPrimitive.class);
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new RuntimeException("Failed to initialize " + this.getClass(), e);
			}

		}

	}

}
