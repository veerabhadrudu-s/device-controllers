package com.hpe.iot.dc.mmi.safemate.southbound.algorithm.crc;

import java.util.Arrays;

/**
 * @author sveera
 *
 */
public class MMICRCAlgorithmTestData {

	private final byte[] payloadData;
	private final int expectedCRC;
	private final String expectedCRCHex;

	public MMICRCAlgorithmTestData(byte[] payloadData, int expectedCRC, String expectedCRCHex) {
		super();
		this.payloadData = payloadData;
		this.expectedCRC = expectedCRC;
		this.expectedCRCHex = expectedCRCHex;
	}

	public byte[] getPayloadData() {
		return payloadData;
	}

	public int getExpectedCRC() {
		return expectedCRC;
	}

	public String getExpectedCRCHex() {
		return expectedCRCHex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + expectedCRC;
		result = prime * result + ((expectedCRCHex == null) ? 0 : expectedCRCHex.hashCode());
		result = prime * result + Arrays.hashCode(payloadData);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MMICRCAlgorithmTestData other = (MMICRCAlgorithmTestData) obj;
		if (expectedCRC != other.expectedCRC)
			return false;
		if (expectedCRCHex == null) {
			if (other.expectedCRCHex != null)
				return false;
		} else if (!expectedCRCHex.equals(other.expectedCRCHex))
			return false;
		if (!Arrays.equals(payloadData, other.payloadData))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MMICRCAlgorithmTestData [payloadData=" + Arrays.toString(payloadData) + ", expectedCRC=" + expectedCRC
				+ ", expectedCRCHex=" + expectedCRCHex + "]";
	}

}
