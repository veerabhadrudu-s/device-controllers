/**
 * 
 */
package com.hpe.iot.service.initializer.groovy;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.LiveLogger;
import com.handson.logger.impl.LiveLoggerAdapter;
import com.handson.logger.service.LoggerService;
import com.hpe.iot.bean.pool.ServerBeanPool;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.groovy.loader.GroovyScriptClassLoader;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.service.initializer.groovy.model.GroovyScriptMetaModel;
import com.hpe.iot.service.initializer.groovy.model.GroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.NorthboundGroovyScriptMetaModel;
import com.hpe.iot.service.initializer.groovy.model.NorthboundGroovyScriptModel;
import com.hpe.iot.service.initializer.groovy.model.SouthboundGroovyScriptMetaModel;
import com.hpe.iot.service.initializer.groovy.model.SouthboundGroovyScriptModel;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;

/**
 * @author sveera
 *
 */
public class GroovyScriptModelCreator {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final ServerBeanPool serverBeanPool;

	public GroovyScriptModelCreator(ServerBeanPool serverBeanPool) {
		super();
		this.serverBeanPool = serverBeanPool;
	}

	public GroovyScriptModel createGroovyScriptModel(String groovyScriptFullPath) throws IOException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String groovyScriptFileName = getFileNameFromFullPath(groovyScriptFullPath);
		Class<?>[] allClassTypes = readConcreateClassesFromScript(groovyScriptFullPath);
		if (allClassTypes.length == 0)
			throw new ScriptFileValidationError(
					"Groovy Script file " + groovyScriptFileName + " is either empty or it has only abstract classes.");

		return constructGroovyScriptModel(groovyScriptFileName, allClassTypes);
	}

	private GroovyScriptModel constructGroovyScriptModel(String groovyScriptFileName, Class<?>[] allClassTypes)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		GroovyScriptMetaModel groovyScriptMetaModel = new GroovyScriptMetaModel();
		for (Class<?> classType : allClassTypes) {
			if (DeviceModel.class.isAssignableFrom(classType))
				groovyScriptMetaModel.setDeviceModelClasstype((Class<DeviceModel>) classType);
			if (DeviceIdExtractor.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getSouthboundGroovyScriptMetaModel()
						.setDeviceIdExtractorClasstype((Class<? extends DeviceIdExtractor>) classType);
			if (MessageTypeExtractor.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getSouthboundGroovyScriptMetaModel()
						.setMessageTypeExtractorClasstype((Class<? extends MessageTypeExtractor>) classType);
			if (PayloadDecipher.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getSouthboundGroovyScriptMetaModel()
						.setPayloadDecipherClasstype((Class<? extends PayloadDecipher>) classType);
			if (UplinkPayloadProcessor.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getSouthboundGroovyScriptMetaModel()
						.setUplinkPayloadProcessorClasstype((Class<? extends UplinkPayloadProcessor>) classType);
			if (PayloadCipher.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getNorthboundGroovyScriptMetaModel()
						.setPayloadCipherClasstype((Class<? extends PayloadCipher>) classType);
			if (DownlinkPayloadProcessor.class.isAssignableFrom(classType))
				groovyScriptMetaModel.getNorthboundGroovyScriptMetaModel()
						.setDownlinkPayloadProcessorClasstype((Class<? extends DownlinkPayloadProcessor>) classType);
		}
		return constructGroovyScriptModelFromScriptMetaModel(groovyScriptFileName, groovyScriptMetaModel,
				allClassTypes);
	}

	private GroovyScriptModel constructGroovyScriptModelFromScriptMetaModel(String groovyScriptFileName,
			GroovyScriptMetaModel groovyScriptMetaModel, Class<?>[] allClassTypes)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		GroovyScriptModel groovyScriptModel = new GroovyScriptModel();
		Map<Class<?>, Object> allObjects = new HashMap<>();
		allObjects.putAll(instantiateZeroArgumentConstructorClasses(allClassTypes));
		loadDeviceModelClassType(groovyScriptFileName, groovyScriptMetaModel, groovyScriptModel, allObjects);
		loadSouthboundGroovyScriptModel(groovyScriptMetaModel.getSouthboundGroovyScriptMetaModel(),
				groovyScriptModel.getSouthboundGroovyScriptModel(), allObjects);
		loadNorthboundGroovyScriptModel(groovyScriptMetaModel.getNorthboundGroovyScriptMetaModel(),
				groovyScriptModel.getNorthboundGroovyScriptModel(), allObjects);
		validateGroovyScriptModel(groovyScriptFileName, groovyScriptModel);
		return groovyScriptModel;
	}

	private void loadDeviceModelClassType(String groovyScriptFileName, GroovyScriptMetaModel groovyScriptMetaModel,
			GroovyScriptModel groovyScriptModel, Map<Class<?>, Object> allObjects)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		if (groovyScriptMetaModel.getDeviceModelClasstype() == null)
			throw new ScriptFileValidationError(
					DeviceModel.class.getSimpleName() + " not defined in the groovy script " + groovyScriptFileName);
		Constructor<?> deviceModelConstructor = groovyScriptMetaModel.getDeviceModelClasstype().getConstructors()[0];
		Class<?>[] constructorParameterTypes = deviceModelConstructor.getParameterTypes();
		validateDeviceModelClassTypeToAvoidCircularDependancyWithLiveLoggerAdapter(groovyScriptMetaModel,
				constructorParameterTypes);
		groovyScriptModel
				.setDeviceModel(instantiateClassType(groovyScriptMetaModel.getDeviceModelClasstype(), allObjects));
		allObjects.put(LiveLogger.class,
				new LiveLoggerAdapter(serverBeanPool.getBean(LoggerService.class),
						new DeviceModelImpl(groovyScriptModel.getDeviceModel().getManufacturer(),
								groovyScriptModel.getDeviceModel().getModelId(),
								groovyScriptModel.getDeviceModel().getVersion())));

	}

	private void validateDeviceModelClassTypeToAvoidCircularDependancyWithLiveLoggerAdapter(
			GroovyScriptMetaModel groovyScriptMetaModel, Class<?>[] constructorParameterTypes) {
		for (Class<?> constructorParameterType : constructorParameterTypes)
			if (LiveLogger.class.isAssignableFrom(constructorParameterType))
				throw new ScriptFileValidationError(
						LiveLogger.class.getSimpleName() + " class type can't be constructor argument for class type "
								+ groovyScriptMetaModel.getDeviceModelClasstype().getSimpleName()
								+ " which is an implementation class for " + DeviceModel.class.getName());
	}

	private void validateGroovyScriptModel(String groovyScriptFileName, GroovyScriptModel groovyScriptModel) {
		if (groovyScriptModel.getSouthboundGroovyScriptModel().getDeviceIdExtractor() == null)
			throw new ScriptFileValidationError(DeviceIdExtractor.class.getSimpleName()
					+ " not defined in the groovy script " + groovyScriptFileName);
	}

	private void loadSouthboundGroovyScriptModel(SouthboundGroovyScriptMetaModel southboundGroovyScriptMetaModel,
			SouthboundGroovyScriptModel southboundGroovyScriptModel, Map<Class<?>, Object> allObjects)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		if (southboundGroovyScriptMetaModel.getDeviceIdExtractorClasstype() != null)
			southboundGroovyScriptModel.setDeviceIdExtractor(
					instantiateClassType(southboundGroovyScriptMetaModel.getDeviceIdExtractorClasstype(), allObjects));
		if (southboundGroovyScriptMetaModel.getMessageTypeExtractorClasstype() != null)
			southboundGroovyScriptModel.setMessageTypeExtractor(instantiateClassType(
					southboundGroovyScriptMetaModel.getMessageTypeExtractorClasstype(), allObjects));
		if (southboundGroovyScriptMetaModel.getPayloadDecipherClasstype() != null)
			southboundGroovyScriptModel.setPayloadDecipher(
					instantiateClassType(southboundGroovyScriptMetaModel.getPayloadDecipherClasstype(), allObjects));
		if (southboundGroovyScriptMetaModel.getUplinkPayloadProcessorClasstype() != null)
			southboundGroovyScriptModel.setUplinkPayloadProcessor(instantiateClassType(
					southboundGroovyScriptMetaModel.getUplinkPayloadProcessorClasstype(), allObjects));
	}

	private void loadNorthboundGroovyScriptModel(NorthboundGroovyScriptMetaModel northboundGroovyScriptMetaModel,
			NorthboundGroovyScriptModel northboundGroovyScriptModel, Map<Class<?>, Object> allObjects)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (northboundGroovyScriptMetaModel.getPayloadCipherClasstype() != null)
			northboundGroovyScriptModel.setPayloadCipher(
					instantiateClassType(northboundGroovyScriptMetaModel.getPayloadCipherClasstype(), allObjects));
		if (northboundGroovyScriptMetaModel.getDownlinkPayloadProcessorClasstype() != null)
			northboundGroovyScriptModel.setDownlinkPayloadProcessor(instantiateClassType(
					northboundGroovyScriptMetaModel.getDownlinkPayloadProcessorClasstype(), allObjects));

	}

	private Class<?>[] readConcreateClassesFromScript(String groovyScriptFullPath) throws IOException {
		final GroovyScriptClassLoader groovyScriptlClassLoader = new GroovyScriptClassLoader(
				new File(groovyScriptFullPath));
		final Class<?>[] allClassesFromScript = groovyScriptlClassLoader.getLoadedClasses();
		final Class<?>[] loadedClasses = filterAbstractClasses(allClassesFromScript);
		logger.trace("All loaded classes from script : " + groovyScriptFullPath + " is " + deepToString(loadedClasses));
		return loadedClasses;
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

	class ScriptFileValidationError extends RuntimeException {

		private static final long serialVersionUID = 1L;

		public ScriptFileValidationError(String message) {
			super(message);
		}

	}

}
