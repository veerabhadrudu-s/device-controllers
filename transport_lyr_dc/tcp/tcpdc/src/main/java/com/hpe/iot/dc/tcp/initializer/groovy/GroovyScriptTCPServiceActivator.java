package com.hpe.iot.dc.tcp.initializer.groovy;

import static com.handson.iot.dc.util.FileUtility.getFileNameFromFullPath;
import static java.util.Arrays.deepToString;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.groovy.loader.GroovyScriptClassLoader;
import com.handson.logger.LiveLogger;
import com.handson.logger.impl.LiveLoggerAdapter;
import com.handson.logger.service.DeploymentLoggerService;
import com.handson.logger.service.LoggerService;
import com.hpe.iot.dc.bean.pool.ServerBeanPool;
import com.hpe.iot.dc.model.DeviceDataDeliveryStatus;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.dc.northbound.component.meta.model.NorthBoundDCComponentMetaModel;
import com.hpe.iot.dc.northbound.component.model.NorthBoundDCComponentModel;
import com.hpe.iot.dc.northbound.converter.inflow.IOTModelConverter;
import com.hpe.iot.dc.northbound.converter.inflow.impl.DefaultIOTModelConverterImpl;
import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.converter.outflow.factory.DownlinkDeviceDataConverterFactory;
import com.hpe.iot.dc.northbound.converter.outflow.factory.impl.DownlinkDeviceDataConverterFactoryImpl;
import com.hpe.iot.dc.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherHandler;
import com.hpe.iot.dc.northbound.service.inflow.impl.IOTPublisherServiceImpl;
import com.hpe.iot.dc.northbound.service.outflow.activator.NorthBoundServiceActivator;
import com.hpe.iot.dc.northbound.service.outflow.factory.DownlinkMessageServiceFactory;
import com.hpe.iot.dc.northbound.service.outflow.factory.impl.DownlinkMessageServiceFactoryImpl;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;
import com.hpe.iot.dc.northbound.transformer.outflow.impl.DefaultDownlinkDataModelTransformerImpl;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.component.meta.model.SouthBoundDCComponentMetaModel;
import com.hpe.iot.dc.southbound.component.model.SouthBoundDCComponentModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.converter.inflow.factory.UplinkDeviceDataConverterFactory;
import com.hpe.iot.dc.southbound.converter.inflow.factory.impl.UplinkDeviceDataConverterFactoryImpl;
import com.hpe.iot.dc.southbound.service.inflow.activator.SouthBoundServiceActivator;
import com.hpe.iot.dc.southbound.service.inflow.factory.UplinkMessageServiceFactory;
import com.hpe.iot.dc.southbound.service.inflow.factory.impl.UplinkMessageServiceFactoryImpl;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.component.meta.model.TCPDCComponentMetaModel;
import com.hpe.iot.dc.tcp.component.model.TCPDCComponentModel;
import com.hpe.iot.dc.tcp.initializer.groovy.creator.TCPDCComponentMetaModelCreator;
import com.hpe.iot.dc.tcp.initializer.groovy.validator.DCComponentValidator;
import com.hpe.iot.dc.tcp.initializer.groovy.validator.OptionalDCComponentValidator;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;
import com.hpe.iot.dc.tcp.southbound.service.manager.TCPServerSocketServiceManager;
import com.hpe.iot.dc.tcp.southbound.service.outflow.TCPServerSocketWriter;
import com.hpe.iot.dc.tcp.southbound.socketpool.ServerClientSocketPool;
import com.hpe.iot.dc.tcp.southbound.socketpool.factory.TCPServerClientSocketPoolFactory;

/**
 * @author sveera
 *
 */
public class GroovyScriptTCPServiceActivator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final TCPServerSocketServiceManager tcpServerSocketServiceManager;
	private final TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory;
	private final DeploymentLoggerService deploymentLoggerService;
	private final ServerBeanPool serverBeanPool;
	private final DCComponentValidator dcComponentValidator;
	private final OptionalDCComponentValidator optionalDCComponentValidator;
	private final TCPDCComponentMetaModelCreator tcpDCComponentMetaModelCreator;
	private final Map<String, ServerSocketToDeviceModel> startedTcpScripts;

	public GroovyScriptTCPServiceActivator(TCPServerSocketServiceManager tcpServerSocketServiceManager,
			TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory,
			DeploymentLoggerService deploymentLoggerService, ServerBeanPool serverBeanPool) {
		this.tcpServerSocketServiceManager = tcpServerSocketServiceManager;
		this.tcpServerClientSocketPoolFactory = tcpServerClientSocketPoolFactory;
		this.deploymentLoggerService = deploymentLoggerService;
		this.serverBeanPool = serverBeanPool;
		this.dcComponentValidator = new DCComponentValidator();
		this.optionalDCComponentValidator = new OptionalDCComponentValidator();
		this.tcpDCComponentMetaModelCreator = new TCPDCComponentMetaModelCreator();
		this.startedTcpScripts = new ConcurrentHashMap<>();
	}

	public void stopAllTCPServices() throws IOException {
		tcpServerSocketServiceManager.stopAllTCPServerSocketServices();
	}

	public void restartTCPService(String groovyScriptFullPath) throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		stopTCPService(groovyScriptFullPath);
		startTCPService(groovyScriptFullPath);
	}

	public void stopTCPService(String groovyScriptFullPath) throws IOException {
		ServerSocketToDeviceModel serverSocketToDeviceModel = startedTcpScripts
				.remove(getFileNameFromFullPath(groovyScriptFullPath));
		if (serverSocketToDeviceModel != null)
			tcpServerSocketServiceManager.stopTCPServerSocketService(serverSocketToDeviceModel);
	}

	public void startTCPService(String groovyScriptFullPath) throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?>[] loadedClasses = readConcreateClassesFromScript(groovyScriptFullPath);
		if (loadedClasses == null || loadedClasses.length == 0)
			return;
		ServerSocketToDeviceModel serverSocketToDeviceModel = startTCPService(
				getFileNameFromFullPath(groovyScriptFullPath), loadedClasses);
		startedTcpScripts.put(getFileNameFromFullPath(groovyScriptFullPath), serverSocketToDeviceModel);
	}

	private Class<?>[] readConcreateClassesFromScript(String groovyScriptFullPath) throws IOException {
		final GroovyScriptClassLoader groovyScriptlClassLoader = new GroovyScriptClassLoader(
				new File(groovyScriptFullPath));
		final Class<?>[] allClassesFromScript = groovyScriptlClassLoader.getLoadedClasses();
		final Class<?>[] loadedClasses = filterAbstractClasses(allClassesFromScript);
		logger.trace("All loaded classes from script : " + groovyScriptFullPath + " is " + deepToString(loadedClasses));
		return loadedClasses;
	}

	private ServerSocketToDeviceModel startTCPService(String scriptFileName, final Class<?>[] loadedClasses)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		final Map<Class<?>, Object> intializedObjects = new HashMap<>();
		intializedObjects.putAll(instantiateZeroArgumentConstructorClasses(loadedClasses));
		final TCPDCComponentMetaModel dcComponentMetaModel = constructTCPDCComponentMetaModel(scriptFileName,
				loadedClasses);
		final ServerSocketToDeviceModel serverSocketToDeviceModel = instantiateClassType(
				dcComponentMetaModel.getServerSocketToDeviceModelClassType(), intializedObjects);
		startTCPService(scriptFileName, loadedClasses, intializedObjects, dcComponentMetaModel,
				serverSocketToDeviceModel);
		return serverSocketToDeviceModel;
	}

	private TCPDCComponentMetaModel constructTCPDCComponentMetaModel(String scriptFileName,
			final Class<?>[] loadedClasses) {
		final TCPDCComponentMetaModel dcComponentMetaModel = tcpDCComponentMetaModelCreator
				.createDCComponentMetaModel(loadedClasses);
		logIdentifiedClassTypes(scriptFileName, dcComponentMetaModel);
		validateMandatoryDCMetaComponentModel(scriptFileName, dcComponentMetaModel);
		return dcComponentMetaModel;
	}

	private void startTCPService(final String scriptFileName, final Class<?>[] loadedClasses,
			final Map<Class<?>, Object> intializedObjects, final TCPDCComponentMetaModel dcComponentMetaModel,
			final ServerSocketToDeviceModel serverSocketToDeviceModel)
			throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
		validateOptionalMetaComponentModel(scriptFileName, serverSocketToDeviceModel, dcComponentMetaModel);
		final DeviceModel deviceModel = new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),
				serverSocketToDeviceModel.getModelId(), serverSocketToDeviceModel.getVersion());
		final ServerClientSocketPool tcpServerClientSocketPool = tcpServerClientSocketPoolFactory
				.getServerClientSocketPool(serverSocketToDeviceModel);
		final TCPServerSocketWriter tcpServerSocketWriter = new TCPServerSocketWriter(tcpServerClientSocketPool);
		intializedObjects.put(LiveLogger.class, instantiateLiveLogger(deviceModel));
		intializedObjects.put(TCPServerSocketWriter.class, tcpServerSocketWriter);
		intializedObjects.put(DeviceModel.class, deviceModel);
		Class<? extends IOTModelConverter> iotModelConverType = dcComponentMetaModel.getNorthBoundDCComponentMetaModel()
				.getIotModelConverterClass() == null ? DefaultIOTModelConverterImpl.class
						: dcComponentMetaModel.getNorthBoundDCComponentMetaModel().getIotModelConverterClass();
		IOTModelConverter iotModelConverter = instantiateClassType(iotModelConverType, intializedObjects);
		intializedObjects.put(IOTModelConverter.class, iotModelConverter);
		IOTPublisherHandler iotPublisherHandler = serverBeanPool.getBean(IOTPublisherHandler.class);
		intializedObjects.put(IOTPublisherHandler.class, iotPublisherHandler);
		IOTPublisherService<DeviceInfo, DeviceDataDeliveryStatus> iotPublisherService = new IOTPublisherServiceImpl(
				iotPublisherHandler, iotModelConverter);
		intializedObjects.put(IOTPublisherService.class, iotPublisherService);
		List<MessageService> allMessageServices = instantiateListObjectTypes(
				dcComponentMetaModel.getDcCommonComponentMetaModel().getMessageServiceTypes(), intializedObjects);
		SouthBoundDCComponentModel southBoundDCComponentModel = instantiateSouthBoundDcComponentModel(intializedObjects,
				dcComponentMetaModel.getSouthBoundDCComponentMetaModel(), allMessageServices);
		NorthBoundDCComponentModel northBoundDCComponentModel = instantiateNorthBoundDcComponentModel(intializedObjects,
				dcComponentMetaModel.getNorthBoundDCComponentMetaModel(), allMessageServices);
		TCPDCComponentModel dcComponentModel = intializeTCPComponent(dcComponentMetaModel, intializedObjects,
				southBoundDCComponentModel, northBoundDCComponentModel);
		tryStartingTCPService(serverSocketToDeviceModel, tcpServerClientSocketPool, tcpServerSocketWriter,
				serverBeanPool.getBean(LoggerService.class), dcComponentModel);
	}

	private LiveLogger instantiateLiveLogger(DeviceModel deviceModel) {
		LoggerService loggerService = serverBeanPool.getBean(LoggerService.class);
		return new LiveLoggerAdapter(loggerService, deviceModel);
	}

	private TCPDCComponentModel intializeTCPComponent(TCPDCComponentMetaModel dcComponentMetaModel,
			Map<Class<?>, Object> intializedObjects, SouthBoundDCComponentModel southBoundDCComponentModel,
			NorthBoundDCComponentModel northBoundDCComponentModel)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<? extends DeviceClientSocketExtractor> deviceClientSocketExtractorClassType = dcComponentMetaModel
				.getDeviceClientSocketExtractorClassType();
		DeviceClientSocketExtractor deviceClientSocketExtractor = null;
		if (deviceClientSocketExtractorClassType != null)
			deviceClientSocketExtractor = instantiateClassType(deviceClientSocketExtractorClassType, intializedObjects);
		return deviceClientSocketExtractor != null
				? new TCPDCComponentModel(northBoundDCComponentModel, southBoundDCComponentModel,
						deviceClientSocketExtractor)
				: new TCPDCComponentModel(northBoundDCComponentModel, southBoundDCComponentModel);
	}

	// TODO : This method need to be improved as this is not right way of
	// handling exception.
	private void tryStartingTCPService(final ServerSocketToDeviceModel serverSocketToDeviceModel,
			final ServerClientSocketPool tcpServerClientSocketPool, final TCPServerSocketWriter tcpServerSocketWriter,
			final LoggerService loggerService, TCPDCComponentModel dcComponentModel) throws IOException {
		try {
			tcpServerSocketServiceManager.createTCPServerSocketService(serverSocketToDeviceModel, dcComponentModel,
					tcpServerClientSocketPool, tcpServerSocketWriter, loggerService);
		} catch (Throwable ex) {
			tcpServerClientSocketPoolFactory.removeServerClientSocketPool(serverSocketToDeviceModel);
			throw ex;
		}
	}

	private NorthBoundDCComponentModel instantiateNorthBoundDcComponentModel(
			final Map<Class<?>, Object> intializedObjects,
			final NorthBoundDCComponentMetaModel northBoundDCComponentMetaModel,
			final List<MessageService> allMessageServices)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (northBoundDCComponentMetaModel.getDataModelTransformerType() == null
				&& northBoundDCComponentMetaModel.getDeviceDataConverterTypes().isEmpty())
			return null;

		List<DownlinkDeviceDataConverter> deviceDataConverters = instantiateListObjectTypes(
				northBoundDCComponentMetaModel.getDeviceDataConverterTypes(), intializedObjects);
		DownlinkDeviceDataConverterFactory deviceDataConverterFactory = new DownlinkDeviceDataConverterFactoryImpl(
				deviceDataConverters);
		intializedObjects.put(DownlinkDeviceDataConverterFactory.class, deviceDataConverterFactory);
		Class<? extends DownlinkDataModelTransformer> downlinkDataModelTransformerType = northBoundDCComponentMetaModel
				.getDataModelTransformerType() == null ? DefaultDownlinkDataModelTransformerImpl.class
						: northBoundDCComponentMetaModel.getDataModelTransformerType();
		DownlinkDataModelTransformer dataModelTransformer = instantiateClassType(downlinkDataModelTransformerType,
				intializedObjects);
		DownlinkMessageServiceFactory downLinkMessageServiceFactory = new DownlinkMessageServiceFactoryImpl(
				allMessageServices);
		NorthBoundServiceActivator northBoundServiceActivator = new NorthBoundServiceActivator(
				downLinkMessageServiceFactory);
		NorthBoundDCComponentModel northBoundDCComponentModel = new NorthBoundDCComponentModel(dataModelTransformer,
				northBoundServiceActivator, instantiateClassType(DeviceModel.class, intializedObjects));
		return northBoundDCComponentModel;

	}

	private SouthBoundDCComponentModel instantiateSouthBoundDcComponentModel(
			final Map<Class<?>, Object> intializedObjects,
			final SouthBoundDCComponentMetaModel southBoundDCComponentMetaModel,
			final List<MessageService> allMessageServices)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		List<UplinkDeviceDataConverter> deviceDataConverters = instantiateListObjectTypes(
				southBoundDCComponentMetaModel.getDeviceDataConverterTypes(), intializedObjects);
		UplinkDeviceDataConverterFactory deviceDataConverterFactory = new UplinkDeviceDataConverterFactoryImpl(
				deviceDataConverters);
		intializedObjects.put(UplinkDeviceDataConverterFactory.class, deviceDataConverterFactory);
		UplinkDataModelTransformer dataModelTransformer = instantiateClassType(
				southBoundDCComponentMetaModel.getDataModelTransformerType(), intializedObjects);
		UplinkMessageServiceFactory upLinkMessageServiceFactory = new UplinkMessageServiceFactoryImpl(
				allMessageServices);
		SouthBoundServiceActivator southBoundServiceActivator = new SouthBoundServiceActivator(
				upLinkMessageServiceFactory);
		SouthBoundDCComponentModel southBoundDCComponentModel = new SouthBoundDCComponentModel(dataModelTransformer,
				southBoundServiceActivator, instantiateClassType(DeviceModel.class, intializedObjects));
		return southBoundDCComponentModel;
	}

	private void validateMandatoryDCMetaComponentModel(String scriptFileName,
			final TCPDCComponentMetaModel tcpDCComponentMetaModel) {
		dcComponentValidator.validateDCComponentModel(scriptFileName, tcpDCComponentMetaModel);
		deploymentLoggerService.log(scriptFileName, "Mandatory validation completed");
	}

	private void validateOptionalMetaComponentModel(String scriptFileName,
			ServerSocketToDeviceModel serverSocketToDeviceModel, TCPDCComponentMetaModel dcComponentMetaModel) {
		optionalDCComponentValidator.validateDCComponentModel(scriptFileName, serverSocketToDeviceModel,
				dcComponentMetaModel);
		deploymentLoggerService.log(scriptFileName, "Optional validation completed");
	}

	private void logIdentifiedClassTypes(String scriptFileName, TCPDCComponentMetaModel dcComponentMetaModel) {
		logger.info("Identified Message Service Class Types are "
				+ dcComponentMetaModel.getDcCommonComponentMetaModel().getMessageServiceTypes());
		logger.info("Identified Uplink Device DataConverter Class Types are "
				+ dcComponentMetaModel.getSouthBoundDCComponentMetaModel().getDeviceDataConverterTypes());
		logger.debug("SouthBoundDCComponentMetaModel from the script is  "
				+ dcComponentMetaModel.getSouthBoundDCComponentMetaModel());
		logger.debug("NorthBoundDCComponentMetaModel from the script is  "
				+ dcComponentMetaModel.getNorthBoundDCComponentMetaModel());
		logger.debug("DCComponentMetaModel from the script is  " + dcComponentMetaModel);
		deploymentLoggerService.log(scriptFileName, "Loading script file completed");
	}

	private Class<?>[] filterAbstractClasses(Class<?>[] allClassesFromScript) {
		List<Class<?>> concreateClasses = new ArrayList<>();
		for (Class<?> classType : allClassesFromScript) {
			if (Modifier.isAbstract(classType.getModifiers()) || Modifier.isInterface(classType.getModifiers()))
				continue;
			concreateClasses.add(classType);
		}
		return concreateClasses.toArray(new Class<?>[concreateClasses.size()]);
	}

	private <T> List<T> instantiateListObjectTypes(List<Class<? extends T>> listOfClassTypes,
			Map<Class<?>, Object> intializedObjects)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<T> listOfObjects = new ArrayList<>();
		for (Class<? extends T> classType : listOfClassTypes) {
			logger.info("Instantiating class type " + classType + " in list of Types " + listOfClassTypes);
			T newObject = instantiateClassType(classType, intializedObjects);
			listOfObjects.add(newObject);
		}
		return listOfObjects;

	}

	private <T> T instantiateClassType(Class<? extends T> classType, Map<Class<?>, Object> intializedObjects)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return intializedObjects.get(classType) != null ? (T) intializedObjects.get(classType)
				: instantiateNewClassType(classType, intializedObjects);
	}

	private <T> T instantiateNewClassType(Class<? extends T> classType, Map<Class<?>, Object> intializedObjects)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<?> constructorType = classType.getConstructors()[0];
		Class<?>[] constructorParameterTypes = constructorType.getParameterTypes();
		Object[] constructorArguments = getAllConstructorArguments(classType, constructorParameterTypes,
				intializedObjects);
		T newInstance = (T) constructorType.newInstance(constructorArguments);
		intializedObjects.put(classType, newInstance);
		return newInstance;
	}

	private Object[] getAllConstructorArguments(Class<?> classType, Class<?>[] parameterTypes,
			Map<Class<?>, Object> intializedObjects) {
		Object[] constructorArguments = new Object[parameterTypes.length];
		for (int parameterIndex = 0; parameterIndex < parameterTypes.length; parameterIndex++) {
			Object object = intializedObjects.get(parameterTypes[parameterIndex]);
			object = object == null ? serverBeanPool.getBean(parameterTypes[parameterIndex]) : object;
			if (object == null) {
				throw new DependancyNotFoundException("Failed to Identify constructor dependancy object with type "
						+ parameterTypes[parameterIndex] + " for class " + classType);
			}
			constructorArguments[parameterIndex] = object;
		}
		return constructorArguments;
	}

	private Map<Class<?>, Object> instantiateZeroArgumentConstructorClasses(Class<?>[] loadedClasses)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Map<Class<?>, Object> zeroArgumentConstructorObjects = new HashMap<>();
		for (Class<?> loadedClass : loadedClasses)
			if (loadedClass.getConstructors().length == 1
					&& loadedClass.getConstructors()[0].getParameterTypes().length == 0)
				zeroArgumentConstructorObjects.put(loadedClass,
						loadedClass.getConstructors()[0].newInstance(new Object[0]));
		logger.info(
				"Instantiated objects with zero argument constructor are " + zeroArgumentConstructorObjects.toString());
		return zeroArgumentConstructorObjects;

	}

}
