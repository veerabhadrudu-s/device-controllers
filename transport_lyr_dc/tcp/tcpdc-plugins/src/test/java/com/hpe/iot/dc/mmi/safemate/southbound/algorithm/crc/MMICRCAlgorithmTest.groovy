package com.hpe.iot.dc.mmi.safemate.southbound.algorithm.crc;

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm


/**
 * @author sveera
 *
 */
public class MMICRCAlgorithmTest {

	private MMICRCAlgorithmTestDataFactory mmicrcAlgorithmTestDataFactory;

	private MMICRCAlgorithm mmicrcAlgorithm;

	@BeforeEach
	public void setUp() throws Exception {
		mmicrcAlgorithm = new MMICRCAlgorithm();
		mmicrcAlgorithmTestDataFactory = new MMICRCAlgorithmTestDataFactory();
	}

	@Test
	public void testIsCRCEqual() {
		for (MMICRCAlgorithmTestData mmicrcAlgorithmTestData : mmicrcAlgorithmTestDataFactory.getDataSets())
			assertTrue(mmicrcAlgorithm.isCRCEqual(mmicrcAlgorithmTestData.getExpectedCRC(),
					mmicrcAlgorithmTestData.getPayloadData()),
					"Failed to match the CRC for dataSet " + mmicrcAlgorithmTestData.toString());
	}

	@Test
	public void testCalculateCRC() {
		for (MMICRCAlgorithmTestData mmicrcAlgorithmTestData : mmicrcAlgorithmTestDataFactory.getDataSets())
			assertEquals(mmicrcAlgorithmTestData.getExpectedCRCHex(),
					mmicrcAlgorithm.calculateCRC(mmicrcAlgorithmTestData.getPayloadData()),
					"Actual CRC and Expected CRC are not same for dataset " + mmicrcAlgorithmTestData.toString());
	}
}
