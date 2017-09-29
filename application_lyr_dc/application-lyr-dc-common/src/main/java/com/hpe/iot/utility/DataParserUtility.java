package com.hpe.iot.utility;

import static java.lang.Math.pow;
import static java.util.Arrays.copyOf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sveera
 *
 */
public final class DataParserUtility {

	private DataParserUtility() {
	}

	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
			'e', 'f' };

	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static String convertBytesToASCIIString(byte[] bytes, int index, int length) {
		return new String(bytes, index, length, StandardCharsets.US_ASCII);
	}

	public static int convertHexaValToDecimalVal(byte... bytes) {
		return Integer.parseInt(new String(bytes, StandardCharsets.US_ASCII), 16);
	}

	public static String convertToHexValue(byte... bytes) {
		return new String(encodeHex(bytes));
	}

	public static char[] encodeHex(final byte[] data) {
		return encodeHex(data, true);
	}

	public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	public static boolean isMessageHasStartingBytes(byte[] input, byte[] startingBytes) {
		return isBytesInSameSequance(input, 0, startingBytes);
	}

	public static List<Integer> findAllClosingMessageByteIndexes(byte[] input, byte[] mesgClosingBytes) {
		List<Integer> closingByteIndexs = new ArrayList<>();
		for (int byteIndex = 0; byteIndex < input.length; byteIndex++) {
			if (isClosingFrameByte(input, byteIndex, mesgClosingBytes)) {
				closingByteIndexs.add(byteIndex + (mesgClosingBytes.length - 1));
			}
		}
		return closingByteIndexs;
	}

	private static boolean isClosingFrameByte(byte[] input, int byteIndex, byte[] closingBytes) {
		return isBytesInSameSequance(input, byteIndex, closingBytes);
	}

	private static boolean isBytesInSameSequance(byte[] input, int byteIndex, byte[] sequanceBytes) {
		for (byte byteToCheck : sequanceBytes) {
			if (byteIndex >= input.length || input[byteIndex] != byteToCheck)
				return false;
			byteIndex++;
		}
		return sequanceBytes.length == 0 ? false : true;
	}

	public static int findIndexOfEOFMessage(byte[] input) {
		int eofByteIndex = input.length;
		for (int byteIndex = input.length - 1; byteIndex >= 0; byteIndex--) {
			if (input[byteIndex] == 0) {
				continue;
			}
			eofByteIndex = byteIndex;
			break;
		}
		return eofByteIndex;
	}

	public static byte[] truncateEmptyBytes(byte[] input) {
		int eofMessageByteIndex = findIndexOfEOFMessage(input);
		return copyOf(input, eofMessageByteIndex + 1);
	}

	public static String calculateUnsignedDecimalValFromSignedBytes(byte... input) {
		String hexVal = convertToHexValue(input);
		long decimalVal = Long.parseLong(hexVal.toUpperCase().trim(), 16);
		return Long.toString(decimalVal);
	}

	public static int calculateUnsignedDecimalValFromSignedByte(byte signedByte) {
		return signedByte & 0xFF;
	}

	public static void reverseArray(byte inputArray[]) {
		byte temp;
		for (int i = 0; i < inputArray.length / 2; i++) {
			temp = inputArray[i];
			inputArray[i] = inputArray[inputArray.length - 1 - i];
			inputArray[inputArray.length - 1 - i] = temp;
		}

	}

	public static boolean checkBitValue(byte byteValue, int index) {
		checkIsValidByteIndex(index);
		int unsignedByteVal = calculateUnsignedDecimalValFromSignedByte(byteValue);
		return (unsignedByteVal & (int) pow(2, index)) > 0 ? true : false;
	}

	private static void checkIsValidByteIndex(int index) {
		if ((index < 0 || index > 7))
			throw new InvalidIndexInByteException();
	}

	public static byte[] createBinaryPayloadFromHexaPayload(String[] hexaPayload, Class<?> classType) {
		byte[] decimalBytePayload = new byte[hexaPayload.length];

		for (int byteIndex = 0; byteIndex < decimalBytePayload.length; byteIndex++) {
			decimalBytePayload[byteIndex] = Integer.valueOf(hexaPayload[byteIndex], 16).byteValue();
		}
		return decimalBytePayload;
	}

	public static byte[][] splitArray(byte[] arrayToSplit, int chunkSize) {
		if (chunkSize <= 0)
			return new byte[0][];
		int rest = arrayToSplit.length % chunkSize;
		int chunks = arrayToSplit.length / chunkSize + (rest > 0 ? 1 : 0);
		byte[][] arrays = new byte[chunks][];
		for (int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++)
			arrays[i] = Arrays.copyOfRange(arrayToSplit, i * chunkSize, i * chunkSize + chunkSize);
		if (rest > 0)
			arrays[chunks - 1] = Arrays.copyOfRange(arrayToSplit, (chunks - 1) * chunkSize,
					(chunks - 1) * chunkSize + rest);

		return arrays;
	}

	public static byte[] getByteArrayValue(long value, int noOfBytesExpected) {
		return getByteArrayValueInLittleEndian(value, noOfBytesExpected);
	}

	public static byte[] getByteArrayValueInBigEndian(long value, int noOfBytesExpected) {
		return getByteArrayValueForByteOrderType(value, noOfBytesExpected, ByteOrder.BIG_ENDIAN);
	}

	public static byte[] getByteArrayValueInLittleEndian(long value, int noOfBytesExpected) {
		return getByteArrayValueForByteOrderType(value, noOfBytesExpected, ByteOrder.LITTLE_ENDIAN);
	}

	private static byte[] getByteArrayValueForByteOrderType(long value, int noOfBytesExpected,
			ByteOrder byteOrderType) {
		if (isNotValidNumericalDataTypeSize(noOfBytesExpected))
			throw new RuntimeException("Invalid Numerical DataType Provided");
		if (noOfBytesExpected == 1)
			return getByteArrayValueForByte((byte) value, noOfBytesExpected, byteOrderType);
		else if (noOfBytesExpected == 2)
			return getByteArrayValueForShort((short) value, noOfBytesExpected, byteOrderType);
		else if (noOfBytesExpected == 4)
			return getByteArrayValueForInt((int) value, noOfBytesExpected, byteOrderType);
		else
			return getByteArrayValueForLong(value, noOfBytesExpected, byteOrderType);
	}

	private static boolean isNotValidNumericalDataTypeSize(int noOfBytesExpected) {
		return !(noOfBytesExpected == 1 || noOfBytesExpected == 2 || noOfBytesExpected == 4 || noOfBytesExpected == 8)
				? true
				: false;
	}

	private static byte[] getByteArrayValueForByte(byte byteValue, int noOfBytesExpected, ByteOrder byteOrderType) {
		ByteBuffer b = constructByteBuffer(noOfBytesExpected, byteOrderType);
		b.put(byteValue);
		return b.array();
	}

	private static byte[] getByteArrayValueForShort(short shortValue, int noOfBytesExpected, ByteOrder byteOrderType) {
		ByteBuffer b = constructByteBuffer(noOfBytesExpected, byteOrderType);
		b.putShort(shortValue);
		return b.array();
	}

	private static byte[] getByteArrayValueForInt(int intValue, int noOfBytesExpected, ByteOrder byteOrderType) {
		ByteBuffer b = constructByteBuffer(noOfBytesExpected, byteOrderType);
		b.putInt(intValue);
		return b.array();
	}

	private static byte[] getByteArrayValueForLong(long longValue, int noOfBytesExpected, ByteOrder byteOrderType) {
		ByteBuffer b = constructByteBuffer(noOfBytesExpected, byteOrderType);
		b.putLong(longValue);
		return b.array();
	}

	private static ByteBuffer constructByteBuffer(int noOfBytesExpected, ByteOrder byteOrderType) {
		ByteBuffer b = ByteBuffer.allocate(noOfBytesExpected);
		b.order(byteOrderType);
		return b;
	}

	public static float convertHexaToFloatPoint(byte... bytes) {
		String hexaRepOfFloatValue = convertToHexValue(bytes);
		Long longValue = Long.parseLong(hexaRepOfFloatValue, 16);
		return Float.intBitsToFloat(longValue.intValue());
	}

}
