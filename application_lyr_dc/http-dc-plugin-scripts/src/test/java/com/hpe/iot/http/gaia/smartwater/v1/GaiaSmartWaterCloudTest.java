package com.hpe.iot.http.gaia.smartwater.v1;

import static com.hpe.iot.http.test.constants.TestConstants.GAIA;
import static com.hpe.iot.http.test.constants.TestConstants.GAIA_MODEL;
import static com.hpe.iot.http.test.constants.TestConstants.GAIA_VERSION;
import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload;
import static com.handson.iot.dc.util.UtilityLogger.convertArrayOfByteToString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.southbound.http.utility.HttpClientUtility;

public class GaiaSmartWaterCloudTest {

	private static final String SOUTHBOUND = "/southbound";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JsonParser jsonParser = new JsonParser();
	private final String dcIpAddress = "10.3.239.75";

	@Disabled
	@Test
	public void testUplinkNotification() throws URISyntaxException, IOException {
		logger.trace("Uplink data used for posting is " + convertArrayOfByteToString(getUplinkNotficationData()));
		JsonObject expectedResponse = getExpectedSuccessResponse();
		URI uri = new URIBuilder().setScheme("http").setHost(dcIpAddress).setPort(80)
				.setPath("/httpdc" + SOUTHBOUND + "/" + GAIA + "/" + GAIA_MODEL + "/" + GAIA_VERSION + "/").build();
		String responseContent = new HttpClientUtility().postRequestOnHttp(uri.toString(), new HashMap<>(),
				getUplinkNotficationData());
		JsonObject actualResponse = jsonParser.parse(responseContent).getAsJsonObject();
		assertEquals(expectedResponse, actualResponse,"Expected and Actual Responses are not same");
	}

	private byte[] getUplinkNotficationData() {
		String[] uplinkBinary26ByteData = new String[] { "00", "6A", "1A", "AA", "A1", "00", "00", "28", "00", "00",
				"13", "95", "00", "00", "00", "04", "00", "00", "00", "00", "00", "00", "00", "04", "00", "00" };
		return createDecimalPayloadFromHexaPayload(uplinkBinary26ByteData, getClass());
	}

	protected JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}
}
