/**
 * 
 */
package com.hpe.iot.dc.web.plugin.monitor;

import com.hpe.iot.dc.model.DeviceModelImpl;

public class ScriptPlugin extends DeviceModelImpl {

	private final String pluginScriptFileName;

	public ScriptPlugin(String manufacturer, String modelId, String version, String pluginScriptFileName) {
		super(manufacturer, modelId, version);
		this.pluginScriptFileName = pluginScriptFileName;
	}

	public String getPluginScriptFileName() {
		return pluginScriptFileName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((pluginScriptFileName == null) ? 0 : pluginScriptFileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ScriptPlugin))
			return false;
		ScriptPlugin other = (ScriptPlugin) obj;
		if (pluginScriptFileName == null) {
			if (other.pluginScriptFileName != null)
				return false;
		} else if (!pluginScriptFileName.equals(other.pluginScriptFileName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ScriptPlugin [pluginScriptFileName=" + pluginScriptFileName + ", manufacturer=" + getManufacturer()
				+ ", modelId=" + getModelId() + ", version=" + getVersion() + "]";
	}

}