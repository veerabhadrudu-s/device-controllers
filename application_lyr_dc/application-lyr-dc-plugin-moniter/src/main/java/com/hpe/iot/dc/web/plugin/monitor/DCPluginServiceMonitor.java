/**
 * 
 */
package com.hpe.iot.dc.web.plugin.monitor;

import static com.handson.iot.dc.util.FileUtility.findFullPath;
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
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.service.initializer.groovy.file.GroovyScriptFileToDeviceModelHolder;

/**
 * @author sveera
 *
 */
@RestController
public class DCPluginServiceMonitor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final GroovyScriptFileToDeviceModelHolder groovyScriptFileToDeviceModelHolder;
	private final String pluginScriptsPath;

	@Autowired
	public DCPluginServiceMonitor(@Qualifier("pluginScriptsPath") String pluginScriptsPath,
			GroovyScriptFileToDeviceModelHolder socketPoolFactoryReader) {
		super();
		this.pluginScriptsPath = findFullPath(pluginScriptsPath);
		this.groovyScriptFileToDeviceModelHolder = socketPoolFactoryReader;

	}

	@RequestMapping(value = "/getPluginScripts", method = RequestMethod.GET, produces = "application/json")
	public String getPluginScripts() {

		Map<String, DeviceModel> scriptDeviceModels = groovyScriptFileToDeviceModelHolder.getScriptDeviceModel();
		List<ScriptPlugin> availableDcScriptPlugins = new ArrayList<>();
		for (Map.Entry<String, DeviceModel> deviceModelToSocketPool : scriptDeviceModels.entrySet())
			availableDcScriptPlugins.add(new ScriptPlugin(deviceModelToSocketPool.getValue().getManufacturer(),
					deviceModelToSocketPool.getValue().getModelId(), deviceModelToSocketPool.getValue().getVersion(),
					deviceModelToSocketPool.getKey()));
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
