/**
 * 
 */
package com.hpe.iot.model.factory.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.model.impl.JsonPathDeviceMetaModel;
import com.hpe.iot.model.impl.json.marshling.JsonPathDeviceMetaModels;
import com.hpe.iot.utility.UtilityLogger;

/**
 * @author sveera
 *
 */
public class UplinkJsonPathDeviceModelFactory implements DeviceModelFactory {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final String configurationFilePath;
	private Map<DeviceModel, JsonPathDeviceMetaModel> deviceMetaModels;

	public UplinkJsonPathDeviceModelFactory(String path) {
		super();
		this.configurationFilePath = path;
		this.deviceMetaModels = new ConcurrentHashMap<>();
		initialize();
	}

	private void initialize() {
		try {
			initMetaModel();
		} catch (Exception e) {
			logger.error("Failed to parse XML in path " + configurationFilePath);
			UtilityLogger.logExceptionStackTrace(e, getClass());
		}
		logger.info("Available DeviceMetaModels are " + deviceMetaModels);
	}

	private void initMetaModel() throws JAXBException {
		String fullPath = findFullPath(configurationFilePath);
		File file = new File(fullPath);
		JAXBContext jaxbContext = JAXBContext.newInstance(JsonPathDeviceMetaModels.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JsonPathDeviceMetaModels deviceMetaModels = (JsonPathDeviceMetaModels) unmarshaller.unmarshal(file);
		if (deviceMetaModels != null && deviceMetaModels.getDeviceMetaModels() != null)
			for (JsonPathDeviceMetaModel deviceMetaModel : deviceMetaModels.getDeviceMetaModels())
				this.deviceMetaModels.put(new DeviceModelImpl(deviceMetaModel.getManufacturer(),
						deviceMetaModel.getModelId(), deviceMetaModel.getVersion()), deviceMetaModel);
	}

	private String findFullPath(String path) {
		String[] pathParts = path.split("}");
		return pathParts.length > 1
				? System.getProperty(pathParts[0].substring(1, pathParts[0].length())) + pathParts[1]
				: pathParts[0];
	}

	@Override
	public DeviceModel findDeviceModel(String manufacturer, String modelId, String version) {
		return deviceMetaModels.get(new DeviceModelImpl(manufacturer, modelId, version));
	}

	@Override
	public List<DeviceModel> getAllDeviceModels() {
		return new ArrayList<DeviceModel>(deviceMetaModels.values());
	}

	@Override
	public String toString() {
		return "JsonPathDeviceMetaModelFactory [deviceMetaModels=" + deviceMetaModels + "]";
	}

}
