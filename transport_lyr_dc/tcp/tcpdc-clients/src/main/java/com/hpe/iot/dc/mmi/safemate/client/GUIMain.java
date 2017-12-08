/**
 * 
 */
package com.hpe.iot.dc.mmi.safemate.client;

import java.io.IOException;

import com.hpe.iot.dc.client.AbstractGuiMain;
import com.hpe.iot.dc.mmi.safemate.MMICRCAlgorithm;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientMessageConsumer;
import com.hpe.iot.dc.mmi.safemate.tcp.client.payload.converters.MMIClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.GUI;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class GUIMain extends AbstractGuiMain {

	public static void main(String[] args) throws IOException {
		new GUIMain().main();
	}

	@Override
	protected ClientMessageGenerator getClientMessageGenerator() {
		return new MMIClientMessageGenerator(new MMICRCAlgorithm());
	}

	@Override
	protected ClientMessageConsumer getClientMessageConsumer() {
		return new MMIClientMessageConsumer();
	}

	@Override
	protected GUI getGUI() {
		return new MMIGUI();
	}
}
