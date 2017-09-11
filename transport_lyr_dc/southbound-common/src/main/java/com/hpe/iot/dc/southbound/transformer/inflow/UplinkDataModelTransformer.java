package com.hpe.iot.dc.southbound.transformer.inflow;

import java.util.List;

import com.hpe.iot.dc.model.DeviceInfo;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface UplinkDataModelTransformer {

	List<DeviceInfo> convertToModel(DeviceModel deviceModel, byte[] input);

}