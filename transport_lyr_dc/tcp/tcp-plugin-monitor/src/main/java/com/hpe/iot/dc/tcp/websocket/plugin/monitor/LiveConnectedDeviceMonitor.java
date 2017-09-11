/**
 * 
 */
package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import javax.servlet.ServletContext;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import io.undertow.websockets.jsr.ServerWebSocketContainer;

/**
 * @author sveera \n
 * 
 *         This is a Web socket Service Implemented using JSR 356 specification
 *         interfaces and implementation is specific to wildfly-9 server.
 */
// @ServerEndpoint(value = "/testWebsocketService", configurator =ServerEndpointConfigurator.class)
@ServerEndpoint(value = "/monitorLiveConnectedDevices")
public class LiveConnectedDeviceMonitor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@OnOpen
	public void onOpen(Session userSession, EndpointConfig configuration) {
		logger.trace("Websocket connection opened with client " + userSession.getBasicRemote().toString());
		WebSocketSessionPublisher webSocketSessionPublisher = getWebSocketSessionPublisher();
		if (webSocketSessionPublisher != null)
			webSocketSessionPublisher.publishNewSession(userSession);
	}

	@OnMessage
	public void receivedMessage(Session session, String msg) {
		logger.info("Received Message from Connected Client is " + msg);
	}

	@OnError
	public void error(Session session, Throwable error) {
		logger.error("Error with connected websocket client " + session.getBasicRemote().toString());
		logExceptionStackTrace(error, getClass());
	}

	@OnClose
	public void close(Session session, CloseReason reason) {
		logger.trace("Closed websocket connection with client with reason " + reason.getCloseCode() + ":"
				+ reason.getReasonPhrase());
		WebSocketSessionPublisher webSocketSessionPublisher = getWebSocketSessionPublisher();
		if (webSocketSessionPublisher != null)
			webSocketSessionPublisher.publishClosedSession(session);

	}

	private WebSocketSessionPublisher getWebSocketSessionPublisher() {
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
		if (webSocketContainer instanceof ServerWebSocketContainer) {
			ServerWebSocketContainer serverWebSocketContainer = (ServerWebSocketContainer) webSocketContainer;
			logger.trace("Websocket Container " + webSocketContainer);
			logger.trace("Websocket Container Class Name " + webSocketContainer.getClass().getName());
			ServletContext servletContext = serverWebSocketContainer.getContextToAddFilter();
			logger.trace("servletContext " + servletContext.getClass().getName());
			if (servletContext instanceof ServletContext) {
				WebApplicationContext applicationContext = WebApplicationContextUtils
						.getWebApplicationContext(servletContext);
				logger.trace("WebApplicationContext " + applicationContext.getClass().getName());
				return (WebSocketSessionPublisher) applicationContext.getBean(WebSocketSessionPublisher.class);
			}

		}
		return null;

	}

}
