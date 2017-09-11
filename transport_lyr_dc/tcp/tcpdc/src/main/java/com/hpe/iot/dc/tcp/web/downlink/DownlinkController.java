/**
 * 
 */
package com.hpe.iot.dc.tcp.web.downlink;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.northbound.service.manager.NorthBoundDownlinkServiceManager;

/**
 * @author sveera
 *
 */
@RestController
public class DownlinkController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final NorthBoundDownlinkServiceManager northBoundDownlinkServiceManager;

	@Autowired
	public DownlinkController(NorthBoundDownlinkServiceManager northBoundDownlinkServiceManager) {
		super();
		this.northBoundDownlinkServiceManager = northBoundDownlinkServiceManager;
	}

	@RequestMapping(value = "/processDownlinkData", method = RequestMethod.POST)
	public String sendDownlinkData(@RequestBody final String body, HttpServletRequest req) {
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
		northBoundDownlinkServiceManager.sendDownlinkData(downlinkPayload);
		return "Downlink Command Data Received";
	}

	@ExceptionHandler
	public void handleAllExceptions(Throwable throwable) {
		logger.error("Exception occured while processing downlink command from UIOT " + throwable.getMessage());
		logExceptionStackTrace(throwable, getClass());
	}
}
