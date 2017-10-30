/**
 * 
 */
package com.hpe.iot.northbound.service.outflow.impl;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.factory.BrokerConsumerServiceFactory;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.m2m.common.Notification;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandService;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandServiceHandler;

/**
 * @author sveera
 *
 */
public class DownlinkCommandServiceImpl implements DownlinkCommandService {

	private final DownlinkCommandServiceHandler downlinkCommandServiceHandler;
	private final BrokerConsumerService<String> brokerConsumerService;
	private final String downlinkConsumerDestination;

	public DownlinkCommandServiceImpl(DownlinkCommandServiceHandler downlinkCommandServiceHandler,
			BrokerConsumerServiceFactory<String> brokerConsumerServiceFactory, String activeMessageBroker,
			String downlinkConsumerDestination) {
		super();
		this.downlinkCommandServiceHandler = downlinkCommandServiceHandler;
		this.downlinkConsumerDestination = downlinkConsumerDestination;
		this.brokerConsumerService = brokerConsumerServiceFactory.getBrokerConsumerService(activeMessageBroker);
	}

	@Override
	public void startService() {
		brokerConsumerService.startService();
		BrokerConsumerDataHandler<String> brokerConsumerDataHandler = new BrokerConsumerDataHandlerImpl(
				downlinkCommandServiceHandler);
		brokerConsumerService.consumeData(downlinkConsumerDestination, brokerConsumerDataHandler);

	}

	@Override
	public void stopService() {
		brokerConsumerService.stopService();

	}

	private class BrokerConsumerDataHandlerImpl implements BrokerConsumerDataHandler<String> {

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		private final DownlinkCommandServiceHandler northboundService;
		private Unmarshaller unmarshaller;

		public BrokerConsumerDataHandlerImpl(DownlinkCommandServiceHandler northboundService) {
			this.northboundService = northboundService;
		}

		@Override
		public void handleConsumerMessage(String destiantion, String consumerData) {
			logger.trace("Received downlink message from message broker is " + consumerData);
			if (unmarshaller == null)
				initializeUnmarshaller();
			try {
				RequestPrimitive requestPrimitive = (RequestPrimitive) unmarshaller
						.unmarshal(new StringReader(consumerData));
				List<Object> allObjects = requestPrimitive.getPrimitiveContent().getAny();
				for (Object object : allObjects)
					handleNotificationObject(object);
			} catch (JAXBException e) {
				throw new RuntimeException("Failed to process device payload " + this.getClass(), e);
			}
		}

		private void initializeUnmarshaller() {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(RequestPrimitive.class);
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (JAXBException e) {
				throw new RuntimeException("Failed to initialize " + this.getClass(), e);
			}

		}

		private void handleNotificationObject(Object object) {
			JAXBElement<?> jax = (JAXBElement<?>) object;
			logger.trace("JAXBElement has instance of " + jax.getValue().getClass());
			if (jax.getValue() instanceof Notification) {
				Notification notification = (Notification) jax.getValue();
				if (notification.getNotificationEvent().getRepresentation() instanceof String)
					northboundService.processPayload(constructDeviceInfoFromJsonPayload(
							(String) notification.getNotificationEvent().getRepresentation()));
			}

		}

		private DeviceInfo constructDeviceInfoFromJsonPayload(String deviceInfoJsonPayload) {
			JsonParser parser = new JsonParser();
			JsonObject contentInstance = parser.parse(deviceInfoJsonPayload).getAsJsonObject().get("m2m:cin")
					.getAsJsonObject();
			logger.trace("Content Instance is " + contentInstance);
			String downlinkPayloadString = contentInstance.get("con").getAsString();
			JsonObject downlinkPayload = parser.parse(downlinkPayloadString).getAsJsonObject();
			logger.debug("Downlink Payload is " + downlinkPayload);
			JsonObject device = downlinkPayload.get("device").getAsJsonObject();
			return new DeviceInfo(
					new DeviceImpl(device.get("manufacturer").getAsString(), device.get("modelId").getAsString(),
							device.get("version").getAsString(), device.get("deviceId").getAsString()),
					downlinkPayload.get("messageType").getAsString(), downlinkPayload.get("payload").getAsJsonObject());
		}

	}

}
