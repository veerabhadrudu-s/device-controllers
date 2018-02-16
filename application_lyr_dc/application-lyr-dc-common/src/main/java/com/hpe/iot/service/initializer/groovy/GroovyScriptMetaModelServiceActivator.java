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
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.model.factory.GroovyDeviceModelFactory;
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
	private final GroovyDeviceModelFactory groovyDeviceModelFactory;
	private final GroovyScriptModelCreator groovyScriptModelCreator;
	private final DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder;
	private final DirectoryFileScanner directoryFileScanner;

	public GroovyScriptMetaModelServiceActivator(List<String> directoriesForGroovyScripts,
			GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolderImpl,
			SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory,
			NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory,
			GroovyDeviceModelFactory groovyDeviceModelFactory, GroovyScriptModelCreator groovyScriptModelCreator,
			DefaultPayloadExtractorFactoryComponentHolder defaultPayloadExtractorFactoryComponentHolder) {
		super();
		this.directoriesForGroovyScripts = directoriesForGroovyScripts;
		this.groovyScriptFileToDeviceModelHolderImpl = groovyScriptFileToDeviceModelHolderImpl;
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
		GroovyScriptModel groovyScriptModel = groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFullPath);
		validateGroovyScriptModel(groovyScriptModel, groovyScriptFullPath);
		DeviceModel deviceModel = groovyScriptModel.getDeviceModel();
		GroovyScriptDeviceModel groovyScriptDeviceMetaModel = new GroovyScriptDeviceModel(deviceModel.getManufacturer(),
				deviceModel.getModelId(), deviceModel.getVersion());
		groovyDeviceModelFactory.addGroovyDeviceModel(deviceModel.getManufacturer(), deviceModel.getModelId(),
				deviceModel.getVersion(), groovyScriptDeviceMetaModel);
		loadSouthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getSouthboundGroovyScriptModel());
		loadNorthboundPayloadExtractorFactory(deviceModel, groovyScriptModel.getNorthboundGroovyScriptModel());
		groovyScriptFileToDeviceModelHolderImpl.addScriptDeviceModel(getFileNameFromFullPath(groovyScriptFullPath),
				deviceModel);
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

	private void removeGroovyPluginScript(String scriptFullPath) {
		final DeviceModel scriptDeviceModel = groovyScriptFileToDeviceModelHolderImpl
				.getScriptDeviceModel(getFileNameFromFullPath(scriptFullPath));
		logger.info("Identified plugin script model " + scriptDeviceModel + " for the plugin script removal "
				+ getFileNameFromFullPath(scriptFullPath));
		if (scriptDeviceModel != null) {
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
			groovyScriptFileToDeviceModelHolderImpl.removeScriptDeviceModel(scriptFullPath);
			logger.info("Removed plugin script model/handlers of device model " + scriptDeviceModel
					+ " for the plugin script " + getFileNameFromFullPath(scriptFullPath));
		}
	}

}
