package com.hpe.iot.service.initializer.groovy;

import static com.hpe.iot.utility.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.GroovyDeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.service.initializer.ServiceActivator;
import com.hpe.iot.service.initializer.groovy.model.GroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.NorthboundGroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.SouthboundGroovyScriptModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;
import com.hpe.iot.utility.DirectoryFileScanner;

/**
 * @author sveera
 *
 */
public class GroovyScriptMetaModelServiceActivator implements ServiceActivator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<String> directoriesForGroovyScripts;
	private final DirectoryFileScanner directoryFileScanner;
	private final SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	private final NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;
	private final GroovyDeviceModelFactory groovyDeviceModelFactory;
	private final GroovyScriptModelCreator groovyScriptModelCreator;
	private final DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder;

	public GroovyScriptMetaModelServiceActivator(List<String> directoriesForGroovyScripts,
			SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory,
			NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory,
			GroovyDeviceModelFactory groovyDeviceModelFactory, GroovyScriptModelCreator groovyScriptModelCreator,
			DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder) {
		super();
		this.directoriesForGroovyScripts = directoriesForGroovyScripts;
		this.southboundPayloadExtractorFactory = southboundPayloadExtractorFactory;
		this.northboundPayloadExtractorFactory = northboundPayloadExtractorFactory;
		this.groovyDeviceModelFactory = groovyDeviceModelFactory;
		this.groovyScriptModelCreator = groovyScriptModelCreator;
		this.defaultPayloadExtractorFactoryComponentHolder = defaultPayloadExtractorFactoryComponentHolder;
		this.directoryFileScanner = new DirectoryFileScanner();
	}

	@Override
	public void stopAllServices() {

	}

	@Override
	public void startAllServices() {
		try {
			for (String directoryForGroovyScript : directoriesForGroovyScripts)
				loadAllGroovyMetaModelServices(directoryForGroovyScript);
		} catch (Exception e) {
			logger.error("Failed to initialize GroovyScriptMetaModelServiceActivator for groovy script plugins ");
			logExceptionStackTrace(e, getClass());
		}
	}

	private void loadAllGroovyMetaModelServices(String directoryForGroovyScript) throws IOException {
		String absolutePath = configureDirectoryPath(directoryForGroovyScript);
		logger.trace("Path for script files to be scanned is " + absolutePath);
		List<String> fileNames = directoryFileScanner.getDirectoryFileNames(absolutePath);
		List<String> groovyFileNames = filterInvalidFileNames(fileNames);
		for (String groovyScriptName : groovyFileNames)
			loadGroovyPluginScript(groovyScriptName);
	}

	private String configureDirectoryPath(String directoryPathForGroovyScript) {
		String[] pathParts = directoryPathForGroovyScript.split("}");
		String absolutePath;
		if (pathParts.length > 1)
			absolutePath = System.getProperty(pathParts[0].substring(1, pathParts[0].length())) + pathParts[1];
		else
			absolutePath = pathParts[0];
		return absolutePath;
	}

	private List<String> filterInvalidFileNames(List<String> fileNames) {
		List<String> groovyFileNames = new ArrayList<>();
		for (String fileName : fileNames)
			if (fileName.endsWith(".groovy"))
				groovyFileNames.add(fileName);
			else
				logger.warn("Invalid file identified in the directory with name " + fileName);
		return groovyFileNames;
	}

	private void loadGroovyPluginScript(String groovyScriptName) throws IOException {
		try {
			startAllServicesForScript(groovyScriptName);
		} catch (Throwable e) {
			logger.error("Failed to initialze Groovy Plugin Script " + groovyScriptName);
			logExceptionStackTrace(e, getClass());
		}

	}

	private void startAllServicesForScript(String groovyScriptFullPath) throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		GroovyScriptModel groovyScriptModel = groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFullPath);
		validateGroovyScriptModel(groovyScriptModel, groovyScriptFullPath);
		DeviceModel deviceModel = groovyScriptModel.getDeviceModel();
		GroovyScriptDeviceModel groovyScriptDeviceMetaModel = new GroovyScriptDeviceModel(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion());
		groovyDeviceModelFactory.addGroovyDeviceModel(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), groovyScriptDeviceMetaModel);
		loadSouthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getSouthboundGroovyScriptModel());
		loadNorthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getNorthboundGroovyScriptModel());
	}

	private void loadSouthboundPayloadExtractorFactory(DeviceModel deviceModel,
			SouthboundGroovyScriptModel southboundGroovyScriptModel) {
		DeviceIdExtractor deviceIdExtractor = southboundGroovyScriptModel.getDeviceIdExtractor();
		MessageTypeExtractor messageTypeExtractor = southboundGroovyScriptModel.getMessageTypeExtractor();
		PayloadDecipher payloadDecipher = southboundGroovyScriptModel.getPayloadDecipher();
		UplinkPayloadProcessor uplinkPayloadProcessor = southboundGroovyScriptModel.getUplinkPayloadProcessor();
		if (messageTypeExtractor == null)
			messageTypeExtractor = defaultPayloadExtractorFactoryComponentHolder.getDefaultMessageTypeExtractor();
		if (payloadDecipher == null)
			payloadDecipher = defaultPayloadExtractorFactoryComponentHolder.getDefaultPayloadDecipher();
		if (uplinkPayloadProcessor == null)
			uplinkPayloadProcessor = defaultPayloadExtractorFactoryComponentHolder.getDefaultUplinkPayloadProcessor();
		southboundPayloadExtractorFactory.addDeviceIdExtractor(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), deviceIdExtractor);
		southboundPayloadExtractorFactory.addMessageTypeExtractor(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion(), messageTypeExtractor);
		southboundPayloadExtractorFactory.addPayloadDecipher(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), payloadDecipher);
		southboundPayloadExtractorFactory.addUplinkPayloadProcessor(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion(), uplinkPayloadProcessor);
	}

	private void loadNorthboundPayloadExtractorFactory(DeviceModel deviceModel,
			NorthboundGroovyScriptModel northboundGroovyScriptModel) {
		PayloadCipher payloadCipher = northboundGroovyScriptModel.getPayloadCipher();
		if (payloadCipher == null)
			payloadCipher = defaultPayloadExtractorFactoryComponentHolder.getDefaultpayloadCipher();
		northboundPayloadExtractorFactory.addPayloadCipher(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), payloadCipher);
		if (northboundGroovyScriptModel.getDownlinkPayloadProcessor() != null)
			northboundPayloadExtractorFactory.addDownlinkPayloadProcessor(deviceModel.getManufacturer(),
					deviceModel.getModelId(), deviceModel.getVersion(),
					northboundGroovyScriptModel.getDownlinkPayloadProcessor());
	}

	private void validateGroovyScriptModel(GroovyScriptModel groovyScriptModel, String groovyScriptFullPath) {
		if (groovyScriptModel.getDeviceModel() == null)
			throw new RuntimeException(
					DeviceModel.class.getSimpleName() + " not defined in the groovy script " + groovyScriptFullPath);
		if (groovyScriptModel.getSouthboundGroovyScriptModel().getDeviceIdExtractor() == null)
			throw new RuntimeException(DeviceIdExtractor.class.getSimpleName() + " not defined in the groovy script "
					+ groovyScriptFullPath);
	}

}
