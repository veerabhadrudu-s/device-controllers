package com.hpe.iot.dc.mmi.safemate.northbound.mock;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.m2m.common.RequestPrimitive;

/**
 * @author sveera
 *
 */
public class MockIOTRequestResponseHandler implements Answer<Void> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public Void answer(InvocationOnMock invocation) throws Throwable {
		RequestPrimitive dataToBeWritten = (RequestPrimitive) invocation.getArguments()[0];
		logger.info("IOT Request Primitive Sent to Platform " + dataToBeWritten);
		return null;
	}

}
