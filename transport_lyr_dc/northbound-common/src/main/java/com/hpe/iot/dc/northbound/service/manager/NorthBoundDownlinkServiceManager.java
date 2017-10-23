package com.hpe.iot.dc.northbound.service.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceImpl;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.dc.model.constants.ModelConstants;
import com.hpe.iot.dc.northbound.component.manager.outflow.NorthBoundDownlinkComponentManager;
import com.hpe.iot.dc.northbound.component.model.NorthBoundDCComponentModel;
import com.hpe.iot.dc.northbound.service.outflow.activator.NorthBoundServiceActivator;
import com.hpe.iot.dc.northbound.transformer.outflow.DownlinkDataModelTransformer;

/**
 * @author sveera
 *
 */
public class NorthBoundDownlinkServiceManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final NorthBoundDownlinkComponentManager northBoundDownlinkComponentManager;

	public NorthBoundDownlinkServiceManager(NorthBoundDownlinkComponentManager northBoundDownlinkComponentManager) {
		super();
		this.northBoundDownlinkComponentManager = northBoundDownlinkComponentManager;
	}

	public void sendDownlinkData(JsonObject jsonObject) {
		JsonObject deviceJson = (JsonObject) jsonObject.get(ModelConstants.DEVICE_KEY);
		Gson gson = new Gson();
		Device device = gson.fromJson(deviceJson, DeviceImpl.class);
		DeviceModel deviceModel = new DeviceModelImpl(device.getManufacturer(), device.getModelId(),
				device.getVersion());
		NorthBoundDCComponentModel northBoundDCComponentModel = northBoundDownlinkComponentManager
				.getNorthBoundDCComponentModel(deviceModel);
		if (northBoundDCComponentModel == null) {
			logger.warn("Downlink flow not supported or not implemented for this device model " + deviceModel);
			return;
		}
		DownlinkDataModelTransformer downlinkDataModelTransformer = northBoundDCComponentModel
				.getDataModelTransformer();
		NorthBoundServiceActivator northBoundServiceActivator = northBoundDCComponentModel.getServiceActivator();
		List<DeviceInfo> devcieInfos = downlinkDataModelTransformer.convertToModel(deviceModel, jsonObject);
		for (DeviceInfo deviceInfo : devcieInfos)
			northBoundServiceActivator.processMessage(deviceInfo);
	}

}
