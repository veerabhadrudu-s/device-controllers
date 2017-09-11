/**
 * 
 */
package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * @author sveera
 * 
 * Not working as expected.
 */
@Deprecated
public class SpringLiveConnectedDeviceMonitor extends TextWebSocketHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WebSocketSessionPublisher webSocketSessionPublisher;
	
	public SpringLiveConnectedDeviceMonitor(WebSocketSessionPublisher webSocketSessionPublisher) {
		super();
		this.webSocketSessionPublisher = webSocketSessionPublisher;
		logger.trace("Inside Constructor of LiveConnectedDeviceMonitor");
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession websocketsession) throws Exception {
		//webSocketSessionPublisher.publishNewSession(websocketsession);
	}

	@Override
	public void handleTransportError(WebSocketSession websocketsession, Throwable throwable) throws Exception {
		logger.error("Error with connected websocket client " + websocketsession.getRemoteAddress().toString());
		logExceptionStackTrace(throwable, getClass());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession websocketsession, CloseStatus closestatus) throws Exception {
		//webSocketSessionPublisher.publishClosedSession(websocketsession);
	}

}
