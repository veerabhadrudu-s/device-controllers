/**
 * 
 */
package com.hpe.iot.dc.tcp.client.settings.reader;

/**
 * @author sveera
 *
 */
public class CLIClientSettingsBuilder extends AbstractClientSettingsBuilder {

	public CLIClientSettingsBuilder() {
		super();
	}

	@Override
	public ClientSettingsBuilder noOfClientRunners(String noOfClientRunners) {
		this.noOfClientRunners = noOfClientRunners == null || noOfClientRunners.trim().isEmpty()
				? this.noOfClientRunners
				: getIntegerValue("noOfClientRunners", noOfClientRunners);
		return this;
	}

	@Override
	public ClientSettingsBuilder noOfClientsPerRunner(String noOfClientsPerRunner) {
		this.noOfClientsPerRunner = noOfClientsPerRunner == null || noOfClientsPerRunner.trim().isEmpty()
				? this.noOfClientsPerRunner
				: getIntegerValue("noOfClientsPerRunner", noOfClientsPerRunner);
		return this;
	}

}
