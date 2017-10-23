/**
 * 
 */
package com.hpe.iot.dc.tcp.web.plugin.monitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.factory.SocketPoolFactoryReader;

/**
 * @author sveera
 *
 */
@RestController
public class DCPluginServiceMonitor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SocketPoolFactoryReader socketPoolFactoryReader;

	@Autowired
	public DCPluginServiceMonitor(SocketPoolFactoryReader socketPoolFactoryReader) {
		super();
		this.socketPoolFactoryReader = socketPoolFactoryReader;
	}

	@RequestMapping(value = "/getPluginScripts", method = RequestMethod.GET, produces = "application/json")
	public String getPluginScripts() {

		Map<ServerSocketToDeviceModel, ServerClientSocketPool> deviceModelToSocketPools = socketPoolFactoryReader
				.getDeviceModelToSocketPool();
		List<ScriptPlugin> availableDcScriptPlugins = new ArrayList<>();
		for (Map.Entry<ServerSocketToDeviceModel, ServerClientSocketPool> deviceModelToSocketPool : deviceModelToSocketPools
				.entrySet()) {
			ServerSocketToDeviceModel socketToDeviceModel = deviceModelToSocketPool.getKey();
			availableDcScriptPlugins.add(new ScriptPlugin(socketToDeviceModel.getManufacturer(),
					socketToDeviceModel.getModelId(), socketToDeviceModel.getVersion(),
					deviceModelToSocketPool.getValue().getDevices().size(), socketToDeviceModel.getDescription()));
		}
		String availableDCPluginsJString = new Gson().toJson(availableDcScriptPlugins.toArray());
		logger.info("Loaded DC Plugin Scripts information  " + availableDCPluginsJString);
		JsonParser parser = new JsonParser();
		JsonArray availableDCPlugins = parser.parse(availableDCPluginsJString).getAsJsonArray();
		JsonObject responseJSON = new JsonObject();
		responseJSON.add("data", availableDCPlugins);
		return responseJSON.toString();
	}

}
