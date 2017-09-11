/**
 * 
 */
package com.hpe.iot.dc.northbound.service.inflow.impl;

import java.io.StringWriter;
import java.math.BigInteger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.broker.service.producer.BrokerProducerService;
import com.hpe.broker.service.producer.factory.BrokerProducerServiceFactory;
import com.hpe.iot.m2m.common.RequestPrimitive;
import com.hpe.iot.m2m.common.ResponsePrimitive;

/**
 * @author sveera
 *
 */
public class IOTPublisherHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final BrokerProducerServiceFactory<String> brokerProducerServiceFactory;
	private final String uplinkDestination;
	private final String activeMessageBroker;
	private final Marshaller requestPrimitiveMarshaller;

	public IOTPublisherHandler(BrokerProducerServiceFactory<String> brokerProducerServiceFactory,
			String uplinkDestination, String activeMessageBroker) throws JAXBException {
		super();
		this.brokerProducerServiceFactory = brokerProducerServiceFactory;
		this.uplinkDestination = uplinkDestination;
		this.activeMessageBroker = activeMessageBroker;
		this.requestPrimitiveMarshaller = JAXBContext.newInstance(RequestPrimitive.class).createMarshaller();
		this.requestPrimitiveMarshaller.setProperty("com.sun.xml.bind.xmlHeaders", "<?xml-oneM2m oneM2M=\"1.10\"?>");
	}

	public ResponsePrimitive sendDataToIot(RequestPrimitive requestPrimitive) {
		return trySendingRequestPrimitiveToUiot(requestPrimitive);
	}

	private ResponsePrimitive trySendingRequestPrimitiveToUiot(RequestPrimitive requestPrimitive) {
		ResponsePrimitive responsePrimitive = new ResponsePrimitive();
		responsePrimitive.setRequestIdentifier(requestPrimitive.getRequestIdentifier());
		try {

			BrokerProducerService<String> brokerProducerService = brokerProducerServiceFactory
					.getBrokerProducerService(activeMessageBroker);
			logger.trace("Message will be sent to UIOT over Message broker " + brokerProducerService);
			String requestPrimitiveXml = convertRequestPrimitiveToXml(requestPrimitive);
			brokerProducerService.publishData(uplinkDestination, requestPrimitiveXml);
			responsePrimitive.setResponseStatusCode(new BigInteger("200"));
		} catch (Throwable e) {
			responsePrimitive.setResponseStatusCode(new BigInteger("500"));
		}
		return responsePrimitive;
	}

	private String convertRequestPrimitiveToXml(RequestPrimitive requestPrimitive) throws JAXBException {
		StringWriter stringWriter = new StringWriter();
		requestPrimitiveMarshaller.marshal(requestPrimitive, stringWriter);
		return stringWriter.toString();
	}

}
