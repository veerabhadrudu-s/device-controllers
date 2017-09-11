package com.hpe.iot.dc.mmi.safemate.southbound.algorithm.crc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm


/**
 * @author sveera
 *
 */
public class MMICRCAlgorithmTest {

	private MMICRCAlgorithmTestDataFactory mmicrcAlgorithmTestDataFactory;

	private MMICRCAlgorithm mmicrcAlgorithm;

	@Before
	public void setUp() throws Exception {
		mmicrcAlgorithm = new MMICRCAlgorithm();
		mmicrcAlgorithmTestDataFactory = new MMICRCAlgorithmTestDataFactory();
	}

	@Test
	public void testIsCRCEqual() {
		for (MMICRCAlgorithmTestData mmicrcAlgorithmTestData : mmicrcAlgorithmTestDataFactory.getDataSets())
			Assert.assertTrue("Failed to match the CRC for dataSet " + mmicrcAlgorithmTestData.toString(),
					mmicrcAlgorithm.isCRCEqual(mmicrcAlgorithmTestData.getExpectedCRC(),
							mmicrcAlgorithmTestData.getPayloadData()));
	}

	@Test
	public void testCalculateCRC() {
		for (MMICRCAlgorithmTestData mmicrcAlgorithmTestData : mmicrcAlgorithmTestDataFactory.getDataSets())
			Assert.assertEquals(
					"Actual CRC and Expected CRC are not same for dataset " + mmicrcAlgorithmTestData.toString(),
					mmicrcAlgorithmTestData.getExpectedCRCHex(),
					mmicrcAlgorithm.calculateCRC(mmicrcAlgorithmTestData.getPayloadData()));
	}

}
