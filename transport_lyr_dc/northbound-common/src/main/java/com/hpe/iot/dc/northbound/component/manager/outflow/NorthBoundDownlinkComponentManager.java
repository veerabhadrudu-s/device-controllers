package com.hpe.iot.dc.northbound.component.manager.outflow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.northbound.component.model.NorthBoundDCComponentModel;

/**
 * @author sveera
 *
 */
public class NorthBoundDownlinkComponentManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Map<DeviceModel, NorthBoundDCComponentModel> northBoundDCComponentModels;

	public NorthBoundDownlinkComponentManager() {
		super();
		this.northBoundDCComponentModels = new ConcurrentHashMap<>();
	}

	public Map<DeviceModel, NorthBoundDCComponentModel> getNorthBoundDCComponentModels() {
		return northBoundDCComponentModels;
	}

	public void addNorthBoundDCComponentModel(NorthBoundDCComponentModel northBoundDCComponentModel) {
		if (northBoundDCComponentModel == null)
			return;
		logger.info("Adding NorthBoundDCComponentModel " + northBoundDCComponentModel + " for device model "
				+ northBoundDCComponentModel.getDeviceModel());
		northBoundDCComponentModels.put(northBoundDCComponentModel.getDeviceModel(), northBoundDCComponentModel);
	}

	public NorthBoundDCComponentModel getNorthBoundDCComponentModel(DeviceModel deviceModel) {
		return northBoundDCComponentModels.get(deviceModel);
	}

	public void removeNorthBoundDCComponentModel(DeviceModel deviceModel) {
		NorthBoundDCComponentModel northBoundDCComponentModel = northBoundDCComponentModels.remove(deviceModel);
		if (northBoundDCComponentModel != null)
			logger.info("Removing NorthBoundDCComponentModel " + northBoundDCComponentModel + " for device model "
					+ deviceModel);
	}

	@Override
	public String toString() {
		return "NorthBoundDownlinkServiceManager [northBoundDCComponentModels=" + northBoundDCComponentModels + "]";
	}

}
