package com.hpe.iot.dc.southbound.service.inflow;

import java.util.List;

/**
 * @author sveera
 *
 */
public interface ExtendedUplinkMessageService extends UplinkMessageService {

	List<String> getMessageTypes();

}
