package com.hpe.iot.service.initializer.groovy;

import static com.handson.iot.dc.util.FileUtility.findFullPath;
import static com.handson.iot.dc.util.FileUtility.getFileNameFromFullPath;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.util.DirectoryFileScanner;
import com.handson.logger.service.DeploymentLoggerService;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.impl.GroovyAndUplinkJsonPathDeviceModelFactory;
import com.hpe.iot.model.impl.GroovyScriptDeviceModel;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.service.initializer.ScriptServiceActivator;
import com.hpe.iot.service.initializer.groovy.file.impl.GroovyScriptFileToDeviceModelHolderImpl;
import com.hpe.iot.service.initializer.groovy.model.GroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.NorthboundGroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.SouthboundGroovyScriptModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;

/**
 * @author sveera
 *
 */
public class GroovyScriptMetaModelServiceActivator implements ScriptServiceActivator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<String> directoriesForGroovyScripts;
	private final GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolderImpl;
	private final SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	private final NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;
	private final GroovyAndUplinkJsonPathDeviceModelFactory groovyAndUplinkJsonPathDeviceModelFactory;
	private final GroovyScriptModelCreator groovyScriptModelCreator;
	private final DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder;
	private final DeploymentLoggerService deploymentLoggerService;
	private final DirectoryFileScanner directoryFileScanner;

	public GroovyScriptMetaModelServiceActivator(List<String> directoriesForGroovyScripts,
			GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolderImpl,
			SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory,
			NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory,
			GroovyAndUplinkJsonPathDeviceModelFactory groovyAndUplinkJsonPathDeviceModelFactory,
			GroovyScriptModelCreator groovyScriptModelCreator,
			DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder,
			DeploymentLoggerService deploymentLoggerService) {
		super();
		this.directoriesForGroovyScripts = directoriesForGroovyScripts;
		this.groovyScriptFileToDeviceModelHolderImpl = groovyScriptFileToDeviceModelHolderImpl;
		this.southboundPayloadExtractorFactory = southboundPayloadExtractorFactory;
		this.northboundPayloadExtractorFactory = northboundPayloadExtractorFactory;
		this.groovyAndUplinkJsonPathDeviceModelFactory = groovyAndUplinkJsonPathDeviceModelFactory;
		this.groovyScriptModelCreator = groovyScriptModelCreator;
		this.defaultPayloadExtractorFactoryComponentHolder = defaultPayloadExtractorFactoryComponentHolder;
		this.deploymentLoggerService = deploymentLoggerService;
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

	@Override
	public void startService(String scriptFullPath) {
		loadGroovyPluginScript(scriptFullPath);
	}

	@Override
	public void stopService(String scriptFullPath) {
		removeGroovyPluginScript(scriptFullPath);
	}

	private void loadAllGroovyMetaModelServices(String directoryForGroovyScript) throws IOException {
		String absolutePath = findFullPath(directoryForGroovyScript);
		logger.trace("Path for script files to be scanned is " + absolutePath);
		List<String> fileNames = directoryFileScanner.getDirectoryFileNames(absolutePath);
		List<String> groovyFileNames = filterInvalidFileNames(fileNames);
		for (String groovyScriptName : groovyFileNames)
			loadGroovyPluginScript(groovyScriptName);
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

	private void loadGroovyPluginScript(String groovyScriptName) {
		try {
			startAllServicesForScript(groovyScriptName);
		} catch (Throwable e) {
			logger.error("Failed to initialze Groovy Plugin Script " + groovyScriptName);
			logExceptionStackTrace(e, getClass());
		}

	}

	private void startAllServicesForScript(String groovyScriptFullPath) throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final String groovyScriptFileName = getFileNameFromFullPath(groovyScriptFullPath);
		GroovyScriptModel groovyScriptModel = groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFullPath);
		deploymentLoggerService.log(groovyScriptFileName, "Completed loading plugin script and validation");
		DeviceModel deviceModel = groovyScriptModel.getDeviceModel();
		GroovyScriptDeviceModel groovyScriptDeviceMetaModel = new GroovyScriptDeviceModel(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion());
		groovyAndUplinkJsonPathDeviceModelFactory.addGroovyDeviceModel(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion(), groovyScriptDeviceMetaModel);
		loadSouthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getSouthboundGroovyScriptModel());
		loadNorthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getNorthboundGroovyScriptModel());
		groovyScriptFileToDeviceModelHolderImpl.addScriptDeviceModel(groovyScriptFileName, deviceModel);
		deploymentLoggerService.log(groovyScriptFileName, "Completed registering plugin script model");
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

	private void removeGroovyPluginScript(String scriptFullPath) {
		String groovyScriptFileName = getFileNameFromFullPath(scriptFullPath);
		final DeviceModel scriptDeviceModel = groovyScriptFileToDeviceModelHolderImpl
				.getScriptDeviceModel(groovyScriptFileName);
		logger.info("Identified plugin script model " + scriptDeviceModel + " for the plugin script removal "
				+ groovyScriptFileName);
		if (scriptDeviceModel != null) {
			groovyAndUplinkJsonPathDeviceModelFactory.removeGroovyDeviceModel(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			southboundPayloadExtractorFactory.removePayloadDecipher(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			southboundPayloadExtractorFactory.removeDeviceIdExtractor(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			southboundPayloadExtractorFactory.removeMessageTypeExtractor(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			southboundPayloadExtractorFactory.removeMessageTypeExtractor(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			southboundPayloadExtractorFactory.removeUplinkPayloadProcessor(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			northboundPayloadExtractorFactory.removePayloadCipher(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			northboundPayloadExtractorFactory.removeDownlinkPayloadProcessor(scriptDeviceModel.getManufacturer(),
					scriptDeviceModel.getModelId(), scriptDeviceModel.getVersion());
			groovyScriptFileToDeviceModelHolderImpl.removeScriptDeviceModel(groovyScriptFileName);
			logger.info("Removed plugin script model/handlers of device model " + scriptDeviceModel
					+ " for the plugin script " + groovyScriptFileName);
			deploymentLoggerService.log(groovyScriptFileName, "Completed de-registering plugin script model");
		}
	}

}
