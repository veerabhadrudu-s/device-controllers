package com.hpe.iot.dc.tcp.initializer.groovy;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.bean.pool.ServerBeanPool;
import com.hpe.iot.dc.component.meta.model.DCCommonComponentMetaModel;
import com.hpe.iot.dc.groovy.loader.GroovyScriptClassLoader;
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
import com.hpe.iot.dc.tcp.initializer.groovy.validator.DCComponentValidationStatus;
import com.hpe.iot.dc.tcp.initializer.groovy.validator.DCComponentValidator;
import com.hpe.iot.dc.tcp.initializer.groovy.validator.InvalidDCComponentModel;
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
	private final ServerBeanPool serverBeanPool;
	private final DCComponentValidator dcComponentValidator;
	private final OptionalDCComponentValidator optionalDCComponentValidator;
	private final TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory;

	public GroovyScriptTCPServiceActivator(TCPServerSocketServiceManager tcpServerSocketServiceManager,
			TCPServerClientSocketPoolFactory tcpServerClientSocketPoolFactory, ServerBeanPool serverBeanPool) {
		this.tcpServerSocketServiceManager = tcpServerSocketServiceManager;
		this.serverBeanPool = serverBeanPool;
		this.tcpServerClientSocketPoolFactory = tcpServerClientSocketPoolFactory;
		this.dcComponentValidator = new DCComponentValidator();
		this.optionalDCComponentValidator = new OptionalDCComponentValidator();
	}

	public void stopAllTCPServices() throws IOException {
		tcpServerSocketServiceManager.stopAllTCPServerSocketServices();
	}

	public void startTCPService(String groovyScriptFullPath) throws IOException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		final Class<?>[] loadedClasses = readConcreateClassesFromScript(groovyScriptFullPath);
		if (loadedClasses == null || loadedClasses.length == 0)
			return;
		startTCPService(loadedClasses);
	}

	private Class<?>[] readConcreateClassesFromScript(String groovyScriptFullPath) throws IOException {
		final GroovyScriptClassLoader groovyScriptlClassLoader = new GroovyScriptClassLoader(
				new File(groovyScriptFullPath));
		final Class<?>[] allClassesFromScript = groovyScriptlClassLoader.getLoadedClasses();
		final Class<?>[] loadedClasses = filterAbstractClasses(allClassesFromScript);
		logger.trace("All loaded classes from script : " + groovyScriptFullPath + " is "
				+ Arrays.deepToString(loadedClasses));
		return loadedClasses;
	}

	private void startTCPService(final Class<?>[] loadedClasses)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		final Map<Class<?>, Object> intializedObjects = new HashMap<>();
		final TCPDCComponentMetaModel dcComponentMetaModel = loadDCComponentModelClasses(loadedClasses);
		validateMandatoryDCMetaComponentModel(dcComponentMetaModel);
		logIdentifiedClassTypes(dcComponentMetaModel);
		final ServerSocketToDeviceModel serverSocketToDeviceModel = instantiateClassType(
				dcComponentMetaModel.getServerSocketToDeviceModelClassType(), intializedObjects);
		validateOptionalMetaComponentModel(serverSocketToDeviceModel, dcComponentMetaModel);
		final ServerClientSocketPool tcpServerClientSocketPool = tcpServerClientSocketPoolFactory
				.getServerClientSocketPool(serverSocketToDeviceModel);
		final TCPServerSocketWriter tcpServerSocketWriter = new TCPServerSocketWriter(tcpServerClientSocketPool);
		intializedObjects.put(TCPServerSocketWriter.class, tcpServerSocketWriter);
		intializedObjects.putAll(instantiateZeroArgumentConstructorClasses(loadedClasses));
		intializedObjects.put(DeviceModel.class, new DeviceModelImpl(serverSocketToDeviceModel.getManufacturer(),
				serverSocketToDeviceModel.getModelId()));
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
				dcComponentModel);
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
			TCPDCComponentModel dcComponentModel) throws IOException {
		try {
			tcpServerSocketServiceManager.createTCPServerSocketService(serverSocketToDeviceModel, dcComponentModel,
					tcpServerClientSocketPool, tcpServerSocketWriter);
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

	private void validateMandatoryDCMetaComponentModel(final TCPDCComponentMetaModel tcpDCComponentMetaModel) {
		DCComponentValidationStatus dcComponentValidationStatus = dcComponentValidator
				.validateDCComponentModel(tcpDCComponentMetaModel);
		if (dcComponentValidationStatus.isInValidDCComponentModel()) {
			throw new InvalidDCComponentModel(dcComponentValidationStatus.getMissingClassTypes());
		}
	}

	private void validateOptionalMetaComponentModel(ServerSocketToDeviceModel serverSocketToDeviceModel,
			TCPDCComponentMetaModel dcComponentMetaModel) {
		DCComponentValidationStatus dcComponentValidationStatus = optionalDCComponentValidator
				.validateDCComponentModel(serverSocketToDeviceModel, dcComponentMetaModel);
		if (dcComponentValidationStatus.isInValidDCComponentModel()) {
			throw new InvalidDCComponentModel(dcComponentValidationStatus.getMissingClassTypes());
		}
	}

	private void logIdentifiedClassTypes(TCPDCComponentMetaModel dcComponentMetaModel) {
		logger.info("Identified Message Service Class Types are "
				+ dcComponentMetaModel.getDcCommonComponentMetaModel().getMessageServiceTypes());
		logger.info("Identified Uplink Device DataConverter Class Types are "
				+ dcComponentMetaModel.getSouthBoundDCComponentMetaModel().getDeviceDataConverterTypes());
		logger.debug("SouthBoundDCComponentMetaModel from the script is  "
				+ dcComponentMetaModel.getSouthBoundDCComponentMetaModel());
		logger.debug("NorthBoundDCComponentMetaModel from the script is  "
				+ dcComponentMetaModel.getNorthBoundDCComponentMetaModel());
		logger.debug("DCComponentMetaModel from the script is  " + dcComponentMetaModel);
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
		Map<Class<?>, Object> zeroArgumentConstructorObjects = new HashMap<>();
		for (Class<?> loadedClass : loadedClasses) {
			/*
			 * logger.trace("Interfaces represented by class " + loadedClass + " are " +
			 * ClassUtils.getAllInterfaces(loadedClass));
			 */
			for (Constructor<?> constructor : loadedClass.getConstructors()) {
				if (constructor.getParameterTypes().length == 0) {
					zeroArgumentConstructorObjects.put(loadedClass, constructor.newInstance(new Object[0]));
				}
			}
		}
		logger.info("Instantiated objects with zero argument constructor are " + zeroArgumentConstructorObjects);
		return zeroArgumentConstructorObjects;

	}

	private TCPDCComponentMetaModel loadDCComponentModelClasses(Class<?>[] loadedClasses) {
		TCPDCComponentMetaModel dcComponentModel = new TCPDCComponentMetaModel();
		SouthBoundDCComponentMetaModel southBoundDCComponentModel = dcComponentModel
				.getSouthBoundDCComponentMetaModel();
		NorthBoundDCComponentMetaModel northBoundDCComponentModel = dcComponentModel
				.getNorthBoundDCComponentMetaModel();
		DCCommonComponentMetaModel dcCommonComponentModel = dcComponentModel.getDcCommonComponentMetaModel();
		for (Class<?> loadedClass : loadedClasses) {
			if (UplinkDeviceDataConverter.class.isAssignableFrom(loadedClass))
				southBoundDCComponentModel
						.addDeviceDataConverterType(((Class<? extends UplinkDeviceDataConverter>) loadedClass));
			if (UplinkDataModelTransformer.class.isAssignableFrom(loadedClass))
				southBoundDCComponentModel
						.setDataModelTransformerType((Class<? extends UplinkDataModelTransformer>) loadedClass);
			if (IOTModelConverter.class.isAssignableFrom(loadedClass))
				northBoundDCComponentModel.setIotModelConverterClass((Class<? extends IOTModelConverter>) loadedClass);
			if (DownlinkDeviceDataConverter.class.isAssignableFrom(loadedClass))
				northBoundDCComponentModel
						.addDeviceDataConverterType(((Class<? extends DownlinkDeviceDataConverter>) loadedClass));
			if (DownlinkDataModelTransformer.class.isAssignableFrom(loadedClass))
				northBoundDCComponentModel
						.setDataModelTransformerType((Class<? extends DownlinkDataModelTransformer>) loadedClass);
			if (MessageService.class.isAssignableFrom(loadedClass))
				dcCommonComponentModel.addMessageServiceType((Class<? extends MessageService>) loadedClass);
			if (ServerSocketToDeviceModel.class.isAssignableFrom(loadedClass))
				dcComponentModel.setServerSocketToDeviceModelClassType(
						(Class<? extends ServerSocketToDeviceModel>) loadedClass);
			if (DeviceClientSocketExtractor.class.isAssignableFrom(loadedClass))
				dcComponentModel.setDeviceClientSocketExtractorClassType(
						(Class<? extends DeviceClientSocketExtractor>) loadedClass);

		}
		return dcComponentModel;
	}

}
