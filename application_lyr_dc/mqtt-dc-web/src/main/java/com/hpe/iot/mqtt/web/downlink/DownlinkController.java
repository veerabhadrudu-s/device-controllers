/**
 * 
 */
package com.hpe.iot.mqtt.web.downlink;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.model.DeviceInfo;
import com.hpe.iot.northbound.service.outflow.DownlinkCommandServiceHandler;

/**
 * @author sveera
 *
 */
@RestController
public class DownlinkController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final DownlinkCommandServiceHandler downlinkCommandServiceHandler;

	@Autowired
	public DownlinkController(DownlinkCommandServiceHandler downlinkCommandServiceHandler) {
		super();
		this.downlinkCommandServiceHandler = downlinkCommandServiceHandler;
	}

	@RequestMapping(value = "/processDownlinkData", method = RequestMethod.POST)
	public String sendDownlinkData(@RequestBody final String body, HttpEntity<?> httpEntity, HttpServletRequest req) {
		logger.debug("Received Message body is " + body);
		JsonParser parser = new JsonParser();
		JsonObject jsonRequest = parser.parse(body).getAsJsonObject();
		JsonObject contentInstance = parser.parse(
				jsonRequest.get("m2m:sgn").getAsJsonObject().get("nev").getAsJsonObject().get("rep").getAsString())
				.getAsJsonObject().get("m2m:cin").getAsJsonObject();
		logger.debug("Content Instance is " + contentInstance);
		String downlinkPayloadString = contentInstance.get("con").getAsString();
		JsonObject downlinkPayload = parser.parse(downlinkPayloadString).getAsJsonObject();
		logger.debug("Downlink Payload is " + downlinkPayload);
		JsonObject device = downlinkPayload.get("device").getAsJsonObject();
		DeviceInfo deviceInfo = new DeviceInfo(
				new DeviceImpl(device.get("manufacturer").getAsString(), device.get("modelId").getAsString(),
						device.get("deviceId").getAsString()),
				downlinkPayload.get("messageType").getAsString(), downlinkPayload.get("payload").getAsJsonObject());
		downlinkCommandServiceHandler.processPayload(deviceInfo);
		return "Downlink Request Received";
	}

	@ExceptionHandler
	public void handleAllExceptions(Throwable throwable) {
		logger.error("Exception occured while processing downlink command from UIOT " + throwable.getMessage());
		logExceptionStackTrace(throwable, getClass());
	}
}
