package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import java.util.ArrayList;
import java.util.List;

import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.component.meta.model.TCPDCComponentMetaModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;

/**
 * @author sveera
 *
 */
public class DCComponentValidator {

	public DCComponentValidationStatus validateDCComponentModel(TCPDCComponentMetaModel dcComponentModel) {
		final List<Class<?>> missingClassTypes = new ArrayList<>();
		boolean isInvalidStatus = false;
		if (dcComponentModel.getServerSocketToDeviceModelClassType() == null) {
			missingClassTypes.add(ServerSocketToDeviceModel.class);
			isInvalidStatus = true;
		}
		if (dcComponentModel.getSouthBoundDCComponentMetaModel().getDataModelTransformerType() == null) {
			missingClassTypes.add(UplinkDataModelTransformer.class);
			isInvalidStatus = true;
		}
		if (dcComponentModel.getDcCommonComponentMetaModel().getMessageServiceTypes().size() == 0) {
			missingClassTypes.add(MessageService.class);
			isInvalidStatus = true;
		}
		return new DCComponentValidationStatus(isInvalidStatus, missingClassTypes);
	}

}
