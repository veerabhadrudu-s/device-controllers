package com.hpe.iot.dc.southbound.crypto;

import static com.hpe.iot.dc.util.UtilityLogger.logRawDataInDecimalFormat;
import static com.hpe.iot.dc.util.UtilityLogger.logRawDataInHexaDecimalFormat;

import org.junit.Before;
import org.junit.Test;

/**
 * @author sveera
 *
 */
public class DataDecoderTest {

	private DataDecoder dataDecoder;

	@Before
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
