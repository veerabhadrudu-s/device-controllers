/**
 * 
 */
package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

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
import org.springframework.web.context.support.WebApplicationContextUtils;

import io.undertow.websockets.jsr.ServerWebSocketContainer;

/**
 * @author sveera \n
 * 
 *         This is a Web socket Service Implemented using JSR 356 specification
 *         interfaces and implementation is specific to wildfly-9 server.
 */
// @ServerEndpoint(value =
// "/testWebsocketService",configurator=ServerEndpointConfigurator.class)
@ServerEndpoint(value = "/monitorLiveConnectedDevices")
public class LiveConnectedDeviceMonitor {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final WebSocketSessionPublisher webSocketSessionPublisher;

	public LiveConnectedDeviceMonitor() {
		super();
		this.webSocketSessionPublisher = getWebSocketSessionPublisher();
		logger.trace("Initialized webSocketSessionPublisher is " + webSocketSessionPublisher);
	}

	@OnOpen
	public void onOpen(Session userSession, EndpointConfig configuration) {
		logger.trace("Websocket connection opened with client " + userSession.getBasicRemote().toString());
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
		if (webSocketSessionPublisher != null)
			webSocketSessionPublisher.publishClosedSession(session);
	}

	private WebSocketSessionPublisher getWebSocketSessionPublisher() {
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
		logger.trace("Websocket Container Class Name " + webSocketContainer.getClass().getName());
		if (webSocketContainer instanceof ServerWebSocketContainer) {
			logger.trace("ServletContext implementation Class Name "
					+ ((ServerWebSocketContainer) webSocketContainer).getContextToAddFilter().getClass().getName());
			if (((ServerWebSocketContainer) webSocketContainer).getContextToAddFilter() instanceof ServletContext)
				return extractWebSocketSessionPublisher(webSocketContainer);
		}

		logger.warn("Could not find " + ServerWebSocketContainer.class.getSimpleName()
				+ " from server runtime environemnt");
		return null;
	}

	private WebSocketSessionPublisher extractWebSocketSessionPublisher(WebSocketContainer webSocketContainer) {
		return WebApplicationContextUtils
				.getWebApplicationContext(((ServerWebSocketContainer) webSocketContainer).getContextToAddFilter())
				.getBean(WebSocketSessionPublisher.class);
	}

}
