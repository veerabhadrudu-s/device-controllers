package com.hpe.iot.dc.southbound.crypto;

import static com.handson.iot.dc.util.UtilityLogger.logRawDataInDecimalFormat;
import static com.handson.iot.dc.util.UtilityLogger.logRawDataInHexaDecimalFormat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class DataDecoderTest {

	private DataDecoder dataDecoder;

	@BeforeEach
	public void setUp() throws Exception {
		dataDecoder = new DataDecoder();
	}

	@Test
	public void testBase64Decoding() {
		byte[] decodedData = dataDecoder.decode("AAJkAMYZHgSh4kA=");
		logRawDataInDecimalFormat(decodedData, getClass());
		logRawDataInHexaDecimalFormat(decodedData, getClass());
	}

}
