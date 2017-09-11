/**
 * 
 */
package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import javax.websocket.Session;

/**
 * @author sveera
 *
 */
public interface WebSocketSessionPublisher {

	void publishNewSession(Session websocketsession);

	void publishClosedSession(Session websocketsession);

}
