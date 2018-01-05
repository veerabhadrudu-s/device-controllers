package com.hpe.iot.dc.mmi.safemate.client;

import java.io.IOException;

import com.hpe.iot.dc.client.CliMainTemplateMethod;
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientMessageConsumer;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class CliMain extends CliMainTemplateMethod {

	public static void main(String[] args) throws IOException {
		new CliMain().main();
	}

	@Override
	protected ClientMessageGenerator getClientMessageGenerator() {
		return new MMIClientMessageGenerator(new MMICRCAlgorithm());
	}

	@Override
	protected ClientMessageConsumer getClientMessageConsumer() {
		return new MMIClientMessageConsumer();
	}
}
