/**
 * 
 */
package com.handson.logger.websocket.session;

import javax.websocket.Session;

/**
 * @author sveera
 *
 */
public interface DeploymentLoggerSessionPublisher {

	void publishNewSession(String pluginScriptFile, Session websocketsession);

	void publishClosedSession(String pluginScriptFile, Session websocketsession);

}
