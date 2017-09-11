package com.hpe.iot.dc.northbound.transformer.outflow;

import java.util.List;

import com.google.gson.JsonObject;
import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface DownlinkDataModelTransformer {

	List<DeviceInfo> convertToModel(DeviceModel deviceModel, JsonObject downlinkData);

}