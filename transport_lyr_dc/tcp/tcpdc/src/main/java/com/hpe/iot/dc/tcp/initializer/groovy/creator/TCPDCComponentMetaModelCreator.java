/**
 * 
 */
package com.hpe.iot.dc.tcp.initializer.groovy.creator;

import com.hpe.iot.dc.component.meta.model.DCCommonComponentMetaModel;
import com.hpe.iot.dc.northbound.component.meta.model.NorthBoundDCComponentMetaModel;
import com.hpe.iot.dc.northbound.converter.inflow.IOTModelConverter;
import com.hpe.iot.dc.northbound.converter.outflow.DownlinkDeviceDataConverter;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;
import com.hpe.iot.dc.service.MessageService;
import com.hpe.iot.dc.southbound.component.meta.model.SouthBoundDCComponentMetaModel;
import com.hpe.iot.dc.southbound.converter.inflow.UplinkDeviceDataConverter;
import com.hpe.iot.dc.southbound.transformer.inflow.UplinkDataModelTransformer;
import com.hpe.iot.dc.tcp.component.meta.model.TCPDCComponentMetaModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;

/**
 * @author sveera
 *
 */
public class TCPDCComponentMetaModelCreator {

	public TCPDCComponentMetaModel createDCComponentMetaModel(Class<?>[] loadedClasses) {
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
