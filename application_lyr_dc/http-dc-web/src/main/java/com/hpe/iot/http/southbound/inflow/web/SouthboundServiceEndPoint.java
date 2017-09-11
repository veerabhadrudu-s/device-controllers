/**
 * 
 */
package com.hpe.iot.http.southbound.inflow.web;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.southbound.service.inflow.SouthboundService;

/**
 * @author sveera
 *
 */
@RestController
@RequestMapping(path = "/southbound")
public class SouthboundServiceEndPoint {

	private static final String MANUFACTURER = "manufacturer";
	private static final String MODEL_ID = "modelId";

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JsonParser parser = new JsonParser();

	private final SouthboundService southboundService;

	@Autowired
	public SouthboundServiceEndPoint(SouthboundService southboundService) {
		super();
		this.southboundService = southboundService;
	}

	@RequestMapping(path = "/{" + MANUFACTURER + "}/{" + MODEL_ID + "}", method = RequestMethod.POST)
	public String processDevicePayload(@RequestBody String deviceData,
			@PathVariable(value = MANUFACTURER) String manufacturer, @PathVariable(value = MODEL_ID) String modelId) {
		logger.trace("Received payload from the device : " + deviceData);
		JsonObject payload = parser.parse(deviceData).getAsJsonObject();
		southboundService.processPayload(manufacturer, modelId, payload);
		return new Gson().toJson(new RequestResult("SUCCESS"));
	}

	@RequestMapping(path = "/dcinfo", method = RequestMethod.GET)
	public String getDCInfo() {
		logger.trace("Get DC Info request.");
		return new Gson().toJson(new RequestResult("SUCCESS", "This is a Generic HTTP DC ", ""));
	}

	@ExceptionHandler
	public String handleAllExceptions(Throwable throwable) {
		logExceptionStackTrace(throwable, getClass());
		return new Gson().toJson(new RequestResult("FAILED", "",
				throwable.getMessage() == null ? "Internal Server Error." : throwable.getMessage()));
	}

}
