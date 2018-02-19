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

	public void validateDCComponentModel(String scriptFileName, ServerSocketToDeviceModel serverSocketToDeviceModel,
			TCPDCComponentMetaModel dcComponentModel) {
		final List<Class<?>> missingClassTypes = new ArrayList<>();
		if (serverSocketToDeviceModel.getTCPOptions() == null)
			missingClassTypes.add(TCPOptions.class);
		if (serverSocketToDeviceModel.getTCPOptions().getProcessingTaskType()
				.equals(SOCKET_SESSION_BASED_DATA_PROCESSING)
				&& dcComponentModel.getDeviceClientSocketExtractorClassType() == null)
			missingClassTypes.add(DeviceClientSocketExtractor.class);
		if (missingClassTypes.size() > 0)
			throw new InvalidDCComponentModel(scriptFileName, missingClassTypes);
	}

}
