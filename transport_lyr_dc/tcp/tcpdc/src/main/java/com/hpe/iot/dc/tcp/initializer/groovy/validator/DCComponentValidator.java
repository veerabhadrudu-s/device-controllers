package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.handson.logger.LiveLogger;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.component.meta.model.TCPDCComponentMetaModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;

/**
 * @author sveera
 *
 */
public class DCComponentValidator {

	public void validateDCComponentModel(String scriptFileName, TCPDCComponentMetaModel dcComponentModel) {
		final List<Class<?>> missingClassTypes = new ArrayList<>();
		if (dcComponentModel.getServerSocketToDeviceModelClassType() == null)
			missingClassTypes.add(ServerSocketToDeviceModel.class);
		if (dcComponentModel.getSouthBoundDCComponentMetaModel().getDataModelTransformerType() == null)
			missingClassTypes.add(UplinkDataModelTransformer.class);
		if (dcComponentModel.getDcCommonComponentMetaModel().getMessageServiceTypes().size() == 0)
			missingClassTypes.add(MessageService.class);
		if (missingClassTypes.size() > 0)
			throw new InvalidDCComponentModel(scriptFileName, missingClassTypes);
		validateServerSocketToDeviceModelTypeToAvoidCircularDependancyWithLiveLoggerAdapter(scriptFileName,
				dcComponentModel.getServerSocketToDeviceModelClassType());
	}

	private void validateServerSocketToDeviceModelTypeToAvoidCircularDependancyWithLiveLoggerAdapter(
			String groovyScriptFileName,
			Class<? extends ServerSocketToDeviceModel> serverSocketToDeviceModelClassType) {
		Constructor<?> serverSocketToDeviceModelConstructor = serverSocketToDeviceModelClassType.getConstructors()[0];
		Class<?>[] constructorParameterTypes = serverSocketToDeviceModelConstructor.getParameterTypes();
		for (Class<?> constructorParameterType : constructorParameterTypes)
			if (LiveLogger.class.isAssignableFrom(constructorParameterType))
				throw new InvalidDCComponentModel(
						LiveLogger.class.getSimpleName() + " class type can't be constructor argument for class type "
								+ serverSocketToDeviceModelClassType.getSimpleName()
								+ " which is an implementation class of " + ServerSocketToDeviceModel.class.getName());

	}

}
