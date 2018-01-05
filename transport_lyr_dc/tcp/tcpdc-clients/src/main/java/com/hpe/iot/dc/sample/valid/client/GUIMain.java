/**
 * 
 */
package com.hpe.iot.dc.sample.valid.client;

import java.io.IOException;

import com.hpe.iot.dc.client.GuiMainTemplateMethod;
import com.hpe.iot.dc.sample.valid.tcp.client.payload.converters.SampleClientMessageGenerator;
import com.hpe.iot.dc.tcp.client.GUI;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageConsumer;
import com.hpe.iot.dc.tcp.client.payload.converter.ClientMessageGenerator;

/**
 * @author sveera
 *
 */
public class GUIMain extends GuiMainTemplateMethod {

	public static void main(String[] args) throws IOException {
		new GUIMain().main();
	}

	@Override
	protected ClientMessageGenerator getClientMessageGenerator() {
		return new SampleClientMessageGenerator();
	}

	@Override
	protected ClientMessageConsumer getClientMessageConsumer() {
		return null;
	}

	@Override
	protected GUI getGUI() {
		return new SampleGUI();
	}
}
