/**
 * 
 */
package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sveera
 *
 */
@Deprecated
public class ServerEndpointConfigurator extends ServerEndpointConfig.Configurator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String HTTP_SESSION = "httpSession";

	@Override
	public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
		HttpSession httpSession = (HttpSession) request.getHttpSession();
		logger.trace("Inside modifyHandshake method with HttpSession " + httpSession);
		config.getUserProperties().put(HTTP_SESSION, httpSession);
	}
}
