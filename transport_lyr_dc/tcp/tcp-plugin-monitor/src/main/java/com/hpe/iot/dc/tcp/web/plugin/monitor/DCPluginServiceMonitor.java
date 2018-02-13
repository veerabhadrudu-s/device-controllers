/**
 * 
 */
package com.hpe.iot.dc.tcp.web.plugin.monitor;

import static com.handson.iot.dc.util.UtilityLogger.exceptionStackToString;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

	private final SocketPoolFactoryReader socketPoolFactoryReader;
	private final String pluginScriptsPath;

	@Autowired
	public DCPluginServiceMonitor(@Qualifier("pluginScriptsPath") String pluginScriptsPath,
			SocketPoolFactoryReader socketPoolFactoryReader) {
		super();
		this.pluginScriptsPath = findPath(pluginScriptsPath);
		this.socketPoolFactoryReader = socketPoolFactoryReader;

	}

	private String findPath(String pluginScriptsPath) {
		String path;
		String[] pathParts = pluginScriptsPath.split("}");
		if (pathParts.length > 1)
			path = System.getProperty(pathParts[0].substring(1, pathParts[0].length())) + pathParts[1];
		else
			path = pathParts[0];
		return path;
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

	@RequestMapping(value = "/uploadPluginScript", headers = ("content-type=multipart/*"), method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file,
			HttpServletResponse httpServletResponse) {
		if (file.isEmpty())
			throw new EmptyFileException();
		String uploadedFileName = file.getOriginalFilename();
		String destnationFullPath = pluginScriptsPath + File.separator + uploadedFileName;
		try {
			copyFileUsingStream(file.getBytes(), destnationFullPath);
			logger.info("Plugin script uplaoded to the path : " + destnationFullPath);
		} catch (Throwable e) {
			throw new FileUploadFailureException(e);
		}
		httpServletResponse.setStatus(SC_ACCEPTED);
		return formResponse(
				"Plugin script uploaded successfully.Service for script will be started after validation of script.")
						.toString();
	}

	@ExceptionHandler(EmptyFileException.class)
	public String handleEmptyFileExcpetion(HttpServletResponse httpServletResponse) {
		httpServletResponse.setStatus(SC_BAD_REQUEST);
		return formResponse("Uploaded File is Empty").toString();
	}

	@ExceptionHandler(FileUploadFailureException.class)
	public String handleFileUploadFailureException(FileUploadFailureException fileUploadFailureException,
			HttpServletResponse httpServletResponse) {
		httpServletResponse.setStatus(SC_INTERNAL_SERVER_ERROR);
		logger.error(exceptionStackToString(fileUploadFailureException));
		return formResponse("Failed to upload plugin Script").toString();
	}

	private void copyFileUsingStream(byte[] uploadedFileData, String destnationFullPath) throws IOException {
		try (FileOutputStream fileOutputStream = new FileOutputStream(destnationFullPath);
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);) {
			bufferedOutputStream.write(uploadedFileData);
		}
	}

	private JsonObject formResponse(String responseStatus) {
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.addProperty("status", responseStatus);
		return jsonResponse;
	}

}
