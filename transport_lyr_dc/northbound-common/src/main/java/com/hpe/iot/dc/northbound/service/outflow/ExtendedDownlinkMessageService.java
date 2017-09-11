package com.hpe.iot.dc.northbound.service.outflow;

import java.util.List;

/**
 * @author sveera
 *
 */
public interface ExtendedDownlinkMessageService extends DownlinkMessageService {

	List<String> getMessageTypes();

}
