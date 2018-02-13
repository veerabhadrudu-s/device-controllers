package com.hpe.iot.dc.mmi.safemate.southbound.channel.mock;

import java.nio.ByteBuffer;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.handson.iot.dc.util.UtilityLogger;

/**
 * @author sveera
 *
 */
public class MockSocketWriting implements Answer<Void> {

	@Override
	public Void answer(InvocationOnMock invocation) throws Throwable {
		ByteBuffer dataToBeWritten = (ByteBuffer) invocation.getArguments()[0];
		int availableDataLength = dataToBeWritten.remaining();
		byte[] data = new byte[availableDataLength];
		dataToBeWritten.get(data, 0, availableDataLength);
		UtilityLogger.logRawDataInDecimalFormat(data, getClass());
		return null;
	}

}
