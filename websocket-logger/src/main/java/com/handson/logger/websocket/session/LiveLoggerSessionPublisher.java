/**
 * 
 */
package com.handson.logger.websocket.session;

import javax.websocket.Session;

import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public interface LiveLoggerSessionPublisher {

	void publishNewSession(DeviceModel deviceModel, Session websocketsession);

	void publishClosedSession(DeviceModel deviceModel, Session websocketsession);

}
