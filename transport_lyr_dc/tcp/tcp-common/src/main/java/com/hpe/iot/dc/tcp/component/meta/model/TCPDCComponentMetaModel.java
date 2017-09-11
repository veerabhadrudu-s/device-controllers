package com.hpe.iot.dc.tcp.component.meta.model;

import com.hpe.iot.dc.component.meta.model.DCCommonComponentMetaModel;
import com.hpe.iot.dc.northbound.component.meta.model.NorthBoundDCComponentMetaModel;
import com.hpe.iot.dc.southbound.component.meta.model.SouthBoundDCComponentMetaModel;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.service.inflow.session.DeviceClientSocketExtractor;

/**
 * @author sveera
 *
 */
public class TCPDCComponentMetaModel {

	private final DCCommonComponentMetaModel dcCommonComponentMetaModel = new DCCommonComponentMetaModel();
	private final SouthBoundDCComponentMetaModel southBoundDCComponentMetaModel = new SouthBoundDCComponentMetaModel();
	private final NorthBoundDCComponentMetaModel northBoundDCComponentMetaModel = new NorthBoundDCComponentMetaModel();
	private Class<? extends DeviceClientSocketExtractor> deviceClientSocketExtractorClassType;
	private Class<? extends ServerSocketToDeviceModel> serverSocketToDeviceModelClassType;

	public Class<? extends ServerSocketToDeviceModel> getServerSocketToDeviceModelClassType() {
		return serverSocketToDeviceModelClassType;
	}

	public void setServerSocketToDeviceModelClassType(
			Class<? extends ServerSocketToDeviceModel> serverSocketToDeviceModelClassType) {
		this.serverSocketToDeviceModelClassType = serverSocketToDeviceModelClassType;
	}

	public DCCommonComponentMetaModel getDcCommonComponentMetaModel() {
		return dcCommonComponentMetaModel;
	}

	public SouthBoundDCComponentMetaModel getSouthBoundDCComponentMetaModel() {
		return southBoundDCComponentMetaModel;
	}

	public NorthBoundDCComponentMetaModel getNorthBoundDCComponentMetaModel() {
		return northBoundDCComponentMetaModel;
	}

	public Class<? extends DeviceClientSocketExtractor> getDeviceClientSocketExtractorClassType() {
		return deviceClientSocketExtractorClassType;
	}

	public void setDeviceClientSocketExtractorClassType(
			Class<? extends DeviceClientSocketExtractor> deviceClientSocketExtractorClassType) {
		this.deviceClientSocketExtractorClassType = deviceClientSocketExtractorClassType;
	}

	@Override
	public String toString() {
		return "TCPDCComponentMetaModel [dcCommonComponentMetaModel=" + dcCommonComponentMetaModel
				+ ", southBoundDCComponentMetaModel=" + southBoundDCComponentMetaModel
				+ ", northBoundDCComponentMetaModel=" + northBoundDCComponentMetaModel
				+ ", deviceClientSocketExtractorClassType=" + deviceClientSocketExtractorClassType
				+ ", serverSocketToDeviceModelClassType=" + serverSocketToDeviceModelClassType + "]";
	}

}
