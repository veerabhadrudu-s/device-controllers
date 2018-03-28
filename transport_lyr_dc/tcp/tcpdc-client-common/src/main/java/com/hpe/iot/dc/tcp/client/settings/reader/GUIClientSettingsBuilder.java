/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

/**
 * @author sveera
 *
 */
public class GUIClientSettingsBuilder extends AbstractClientSettingsBuilder {

	@Override
	public ClientSettingsBuilder noOfClientRunners(String noOfClientRunners) {
		this.noOfClientRunners = 1;
		return this;
	}

	@Override
	public ClientSettingsBuilder noOfClientsPerRunner(String noOfClientsPerRunner) {
		this.noOfClientsPerRunner = 1;
		return this;
	}

}
