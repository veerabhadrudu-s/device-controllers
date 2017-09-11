/**
 * 
 */
package com.hpe.iot.dc.tcp.initializer.groovy.validator;

import static com.hpe.iot.dc.tcp.southbound.model.ProcessingTaskType.SOCKET_SESSION_BASED_DATA_PROCESSING;

import java.util.ArrayList;
import java.util.List;

import com.hpe.iot.dc.tcp.component.meta.model.TCPDCComponentMetaModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.model.TCPOptions;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;

/**
 * @author sveera
 *
 */
public class OptionalDCComponentValidator {

	public DCComponentValidationStatus validateDCComponentModel(ServerSocketToDeviceModel serverSocketToDeviceModel,
			TCPDCComponentMetaModel dcComponentModel) {
		final List<Class<?>> missingClassTypes = new ArrayList<>();
		boolean isInvalidStatus = false;
		if (serverSocketToDeviceModel.getTCPOptions() == null) {
			missingClassTypes.add(TCPOptions.class);
			isInvalidStatus = true;
		}
		if (serverSocketToDeviceModel.getTCPOptions().getProcessingTaskType()
				.equals(SOCKET_SESSION_BASED_DATA_PROCESSING)
				&& dcComponentModel.getDeviceClientSocketExtractorClassType() == null) {
			missingClassTypes.add(DeviceClientSocketExtractor.class);
			isInvalidStatus = true;
		}
		return new DCComponentValidationStatus(isInvalidStatus, missingClassTypes);
	}

}
