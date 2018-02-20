/**
 * 
 */
package com.hpe.iot.http.southbound.inflow.web;

import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static com.handson.iot.dc.util.UtilityLogger.exceptionStackToString;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.model.DeviceModelImpl;
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
	private static final String VERSION = "version";

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final LoggerService loggerService;
	private final SouthboundService southboundService;

	@Autowired
	public SouthboundServiceEndPoint(LoggerService loggerService, SouthboundService southboundService) {
		super();
		this.loggerService = loggerService;
		this.southboundService = southboundService;
	}

	@RequestMapping(path = "/{" + MANUFACTURER + "}/{" + MODEL_ID + "}/{" + VERSION + "}"
			+ "/", method = RequestMethod.POST)
	public String processDevicePayload(@PathVariable(value = MANUFACTURER) String manufacturer,
			@PathVariable(value = MODEL_ID) String modelId, @PathVariable(value = VERSION) String version,
			RequestEntity<byte[]> deviceData) {
		logger.trace("Received payload from the device : " + convertArrayOfByteToString(deviceData.getBody()));
		southboundService.processPayload(manufacturer, modelId, version, deviceData.getBody());
		return new Gson().toJson(new RequestResult("SUCCESS"));
	}

	@RequestMapping(path = "/{" + MANUFACTURER + "}/{" + MODEL_ID + "}/{" + VERSION + "}"
			+ "/", method = RequestMethod.PUT)
	public String processDevicePayloadForUpdate(@PathVariable(value = MANUFACTURER) String manufacturer,
			@PathVariable(value = MODEL_ID) String modelId, @PathVariable(value = VERSION) String version,
			RequestEntity<byte[]> deviceData) {
		return processDevicePayload(manufacturer, modelId, version, deviceData);
	}

	@RequestMapping(path = "/dcinfo", method = RequestMethod.GET)
	public String getDCInfo() {
		logger.trace("Get DC Info request.");
		return new Gson().toJson(new RequestResult("SUCCESS", "This is a Generic HTTP DC ", ""));
	}

	@ExceptionHandler
	public String handleAllExceptions(HttpServletRequest httpServletRequest, Throwable throwable) {
		String pathUrl = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
		logger.trace("Exception cause for Request Url " + pathUrl);
		String pathParameters[] = pathUrl.split("/");
		logExceptionStackTrace(throwable, getClass());
		if (pathParameters != null && pathParameters.length == 5)
			loggerService.log(new DeviceModelImpl(pathParameters[2], pathParameters[3], pathParameters[4]),
					exceptionStackToString(throwable));
		return new Gson().toJson(new RequestResult("FAILED", "",
				throwable.getMessage() == null ? "Internal Server Error." : throwable.getMessage()));
	}

}
