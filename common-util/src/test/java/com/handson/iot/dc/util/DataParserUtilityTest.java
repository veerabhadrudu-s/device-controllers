/**
 * 
 */
package com.handson.iot.dc.util;

import static com.handson.iot.dc.util.DataParserUtility.calculateUnsignedDecimalValFromSignedByte;
import static com.handson.iot.dc.util.DataParserUtility.calculateUnsignedDecimalValFromSignedBytes;
import static com.handson.iot.dc.util.DataParserUtility.checkBitValue;
import static com.handson.iot.dc.util.DataParserUtility.convertBytesToASCIIString;
import static com.handson.iot.dc.util.DataParserUtility.convertHexaToFloatPoint;
import static com.handson.iot.dc.util.DataParserUtility.convertHexaValToDecimalVal;
import static com.handson.iot.dc.util.DataParserUtility.convertToHexValue;
import static com.handson.iot.dc.util.DataParserUtility.createDecimalPayloadFromHexaPayload;
import static com.handson.iot.dc.util.DataParserUtility.encodeHex;
import static com.handson.iot.dc.util.DataParserUtility.encodeToLowerCaseHexa;
import static com.handson.iot.dc.util.DataParserUtility.encodeToUpperCaseHexa;
import static com.handson.iot.dc.util.DataParserUtility.findAllClosingMessageByteIndexes;
import static com.handson.iot.dc.util.DataParserUtility.findIndexOfEOFMessage;
import static com.handson.iot.dc.util.DataParserUtility.getByteArrayValue;
import static com.handson.iot.dc.util.DataParserUtility.getByteArrayValueInBigEndian;
import static com.handson.iot.dc.util.DataParserUtility.getByteArrayValueInLittleEndian;
import static com.handson.iot.dc.util.DataParserUtility.isMessageHasStartingBytes;
import static com.handson.iot.dc.util.DataParserUtility.reverseArray;
import static com.handson.iot.dc.util.DataParserUtility.splitArray;
import static com.handson.iot.dc.util.DataParserUtility.truncateEmptyBytes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class DataParserUtilityTest {

	@Test
	@DisplayName("test Convert Bytes To ASCII String")
	public final void testConvertBytesToASCIIString() {
		assertEquals("VEERA", convertBytesToASCIIString(new byte[] { 86, 69, 69, 82, 65, 83 }, 0, 5));
	}

	@Test
	@DisplayName("test Convert Hexa Val To Decimal Val")
	public final void testConvertHexaValToDecimalVal() {
		assertEquals(100, convertHexaValToDecimalVal((byte) 54, (byte) 52));
	}

	@Test
	@DisplayName("test Convert Decimal to Hexa String")
	public final void testConvertToHexValue() {
		assertEquals("8080", convertToHexValue((byte) 128, (byte) 128));
	}

	@Test
	@DisplayName("test Convert Decimal to Hexa Chars")
	public final void testEncodeHexByteArray() {
		assertArrayEquals(new char[] { '8', '0' }, encodeHex((byte) 128));
		assertArrayEquals(new char[] { '8', '0', '8', '0' }, encodeHex((byte) 128, (byte) 128));
	}

	@Test
	@DisplayName("test Convert Decimal to Upper case hexa chars")
	public final void testEncodeToUpperCaseHexa() {
		assertArrayEquals(new char[] { 'F', 'F', 'F', '0' }, encodeToUpperCaseHexa((byte) -1, (byte) -16));
	}

	@Test
	@DisplayName("test Convert Decimal to Lower case hexa chars")
	public final void testEncodeToLowerCaseHexa() {
		assertArrayEquals(new char[] { 'f', 'f', '0', 'f' }, encodeToLowerCaseHexa((byte) -1, (byte) 15));
	}

	@Test
	@DisplayName("test Is Message Has Starting Bytes")
	public final void testIsMessageHasStartingBytes() {
		assert !isMessageHasStartingBytes(new byte[] {}, new byte[] {});
		assert !isMessageHasStartingBytes(new byte[] { 1, 2, 3, 4, 5 }, new byte[] { 1, 2, 3, 4, 5, 6 });
		assert !isMessageHasStartingBytes(new byte[] { 1, 2, 3, 4, 5, 7 }, new byte[] { 1, 2, 3, 4, 5, 6 });
		assert isMessageHasStartingBytes(new byte[] { 1, 2, 3, 4, 5, 6 }, new byte[] { 1, 2, 3, 4, 5, 6 });
	}

	@Test
	@DisplayName("test Find All Closing Message Byte Indexes from Empty Lists")
	public final void testFindAllClosingMessageByteIndexesForEmptyLists() {
		assertThat(findAllClosingMessageByteIndexes(new byte[] {}, new byte[] {}), empty());
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 1, 2, 3, 4 }, new byte[] {}), empty());
		assertThat(findAllClosingMessageByteIndexes(new byte[] {}, new byte[] { 1, 2, 3, 4 }), empty());
	}

	@Test
	@DisplayName("test Find All Closing Message Byte Indexes")
	public final void testFindAllClosingMessageByteIndexes() {
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 5, 6, 7 }, new byte[] { 1, 2, 3, 4 }), empty());
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 5, 6, 7 }, new byte[] { 4, 5, 6, 7 }), contains(3));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 5, 6, 7, 8 }, new byte[] { 4, 5, 6, 7 }),
				contains(3));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 5, 6, 7 }, new byte[] { 4, 5, 6, 7, 8 }), empty());
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 5, 6, 7, 8, 4, 5, 6, 7, 8 },
				new byte[] { 4, 5, 6, 7 }), contains(3, 8));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4 },
				new byte[] { 4, 4 }), contains(1, 3, 7, 9, 11));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4 },
				new byte[] { 4, 4, 4 }), contains(2, 8, 11));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4 },
				new byte[] { 4, 4, 4, 4 }), contains(3, 9));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4 },
				new byte[] { 4, 4, 4, 4, 4 }), contains(4, 10));
		assertThat(findAllClosingMessageByteIndexes(new byte[] { 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4 },
				new byte[] { 4, 4, 4, 4, 4, 4 }), contains(11));
	}

	@Test
	@DisplayName("test Find Index Of EOF Message")
	public final void testFindIndexOfEOFMessage() {
		assertEquals(-1, findIndexOfEOFMessage(new byte[] {}));
		assertEquals(4, findIndexOfEOFMessage(new byte[] { 1, 2, 3, 4, 5, 0 }));
		assertEquals(4, findIndexOfEOFMessage(new byte[] { 0, 2, 3, 4, 5, 0 }));
		assertEquals(4, findIndexOfEOFMessage(new byte[] { 0, 2, 0, 4, 5, 0 }));
		assertEquals(3, findIndexOfEOFMessage(new byte[] { 0, 2, 0, 4, 0, 0 }));
		assertEquals(-1, findIndexOfEOFMessage(new byte[] { 0, 0, 0, 0, 0, 0 }));
	}

	@Test
	@DisplayName("test Truncate Empty Bytes")
	public final void testTruncateEmptyBytes() {
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] {})), emptyArray());
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] { 1, 2, 3, 4, 5, 0 })),
				arrayContaining((byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] { 0, 2, 3, 4, 5, 0 })),
				arrayContaining((byte) 0, (byte) 2, (byte) 3, (byte) 4, (byte) 5));
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] { 0, 2, 0, 4, 5, 0 })),
				arrayContaining((byte) 0, (byte) 2, (byte) 0, (byte) 4, (byte) 5));
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] { 0, 2, 0, 4, 0, 0 })),
				arrayContaining((byte) 0, (byte) 2, (byte) 0, (byte) 4));
		assertThat(convertPrimitiveToWrapperArray(truncateEmptyBytes(new byte[] { 0, 0, 0, 0, 0, 0 })), emptyArray());
	}

	@Test
	@DisplayName("test Calculate Unsigned Decimal Value From Signed Bytes")
	public final void testCalculateUnsignedDecimalValFromSignedBytes() {
		assertEquals("4294967040", calculateUnsignedDecimalValFromSignedBytes(new byte[] { -1, -1, -1, 0 }));
		assertEquals("0", calculateUnsignedDecimalValFromSignedBytes(new byte[] { 0, 0, 0, 0 }));
	}

	@Test
	@DisplayName("test Calculate Unsigned Decimal Value From Signed Byte")
	public final void testCalculateUnsignedDecimalValFromSignedByte() {
		assertEquals(0, calculateUnsignedDecimalValFromSignedByte((byte) 0));
		assertEquals(127, calculateUnsignedDecimalValFromSignedByte((byte) 127));
		assertEquals(128, calculateUnsignedDecimalValFromSignedByte((byte) -128));
		assertEquals(129, calculateUnsignedDecimalValFromSignedByte((byte) -127));
		assertEquals(136, calculateUnsignedDecimalValFromSignedByte((byte) -120));
		assertEquals(255, calculateUnsignedDecimalValFromSignedByte((byte) -1));
	}

	@Test
	@DisplayName("test Reverse Array")
	public final void testReverseArray() {
		assertArrayReversing(emptyArray(), new byte[] {});
		assertArrayReversing(arrayContaining((byte) 0, (byte) 5, (byte) 4, (byte) 3, (byte) 2, (byte) 1),
				new byte[] { 1, 2, 3, 4, 5, 0 });
		assertArrayReversing(arrayContaining((byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0),
				new byte[] { 0, 0, 0, 0, 0, 0 });
	}

	private void assertArrayReversing(Matcher<Byte[]> expectedReveredArrayMatcher, byte[] arrayToBeReversed) {
		reverseArray(arrayToBeReversed);
		assertThat(convertPrimitiveToWrapperArray(arrayToBeReversed), expectedReveredArrayMatcher);
	}

	@Test
	@DisplayName("test Check Bit Value With Invalid Byte Index For InvalidIndexInByteException")
	public final void testCheckBitValueWithInvalidByteIndex() {
		assertThrows(InvalidIndexInByteException.class, () -> checkBitValue((byte) 127, -2));
		assertThrows(InvalidIndexInByteException.class, () -> checkBitValue((byte) 127, 8));
	}

	@Test
	@DisplayName("test Check Bit Value")
	public final void testCheckBitValue() {
		assert checkBitValue((byte) -1, 0);
		assert checkBitValue((byte) -1, 1);
		assert checkBitValue((byte) -1, 2);
		assert checkBitValue((byte) -1, 3);
		assert checkBitValue((byte) -1, 4);
		assert checkBitValue((byte) -1, 5);
		assert checkBitValue((byte) -1, 6);
		assert checkBitValue((byte) -1, 7);
		assert checkBitValue((byte) 127, 0);
		assert checkBitValue((byte) 127, 1);
		assert checkBitValue((byte) 127, 2);
		assert checkBitValue((byte) 127, 3);
		assert checkBitValue((byte) 127, 4);
		assert checkBitValue((byte) 127, 5);
		assert checkBitValue((byte) 127, 6);
		assert !checkBitValue((byte) 127, 7);
	}

	@Test
	@DisplayName("test covert hexa decimal array to decimal array payload")
	public final void testCreateDecimalPayloadFromHexaPayload() {
		assertArrayEquals(new byte[] { 100, -128, -127, 0, 127 },
				createDecimalPayloadFromHexaPayload(new String[] { "64", "80", "81", "00", "7F" }, getClass()));
	}

	@Test
	@DisplayName("test Get Byte Array Value with invalid datatype length For Exception")
	public final void testGetByteArrayValueWithInvalidDataTypeLength() {
		assertThrows(RuntimeException.class, () -> getByteArrayValue(100, 3));
	}

	@Test
	@DisplayName("test Get Byte Array Value In Little Endian")
	public final void testGetByteArrayValueInLittleEndian() {
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInLittleEndian(255, 1)), arrayContaining((byte) -1));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInLittleEndian(255, 2)),
				arrayContaining((byte) -1, (byte) 0));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInLittleEndian(16711935, 4)),
				arrayContaining((byte) -1, (byte) 0, (byte) -1, (byte) 0));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInLittleEndian(71777214294589695l, 8)),
				arrayContaining((byte) -1, (byte) 0, (byte) -1, (byte) 0, (byte) -1, (byte) 0, (byte) -1, (byte) 0));
	}

	@Test
	@DisplayName("test Get Byte Array Value In Big Endian")
	public final void testGetByteArrayValueInBigEndian() {
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInBigEndian(255, 1)), arrayContaining((byte) -1));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInBigEndian(255, 2)),
				arrayContaining((byte) 0, (byte) -1));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInBigEndian(16711935, 4)),
				arrayContaining((byte) 0, (byte) -1, (byte) 0, (byte) -1));
		assertThat(convertPrimitiveToWrapperArray(getByteArrayValueInBigEndian(71777214294589695l, 8)),
				arrayContaining((byte) 0, (byte) -1, (byte) 0, (byte) -1, (byte) 0, (byte) -1, (byte) 0, (byte) -1));
	}

	@Test
	@DisplayName("test Convert Hexa To Float Point")
	public final void testConvertHexaToFloatPoint() {
		assertEquals(1.4E-45, convertHexaToFloatPoint((byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01), 0.09);
		assertEquals(-5.625, convertHexaToFloatPoint((byte) 0xC0, (byte) 0xB4, (byte) 0x00, (byte) 0x00), 0.09);
		assertEquals(329.39062, convertHexaToFloatPoint((byte) 0x43, (byte) 0xA4, (byte) 0xB2, (byte) 0), 0.09);
	}

	@Test
	@DisplayName("test Split Array")
	public final void testSplitArray() {
		assertThat(splitArray(new byte[10], -1), emptyArray());
		assertThat(splitArray(new byte[10], 1), arrayContaining(new byte[10][1]));
		assertThat(splitArray(new byte[10], 2), arrayContaining(new byte[5][2]));
		assertThat(splitArray(new byte[10], 3), arrayContaining(new byte[][] { new byte[] { 0, 0, 0 },
				new byte[] { 0, 0, 0 }, new byte[] { 0, 0, 0 }, new byte[] { 0 } }));
	}

	private Byte[] convertPrimitiveToWrapperArray(byte... primitiveBytes) {
		Byte[] wrapperBytes = new Byte[primitiveBytes.length];
		for (int i = 0; i < primitiveBytes.length; i++)
			wrapperBytes[i] = primitiveBytes[i];
		return wrapperBytes;
	}

}
