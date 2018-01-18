package com.handson.logger.websocket.listener;

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
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.handson.logger.websocket.session.WebSocketSessionPublisher;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;

import io.undertow.websockets.jsr.ServerWebSocketContainer;

/**
 * @author sveera \n
 * 
 *         This is a Web socket Service Implemented using JSR 356 specification
 *         interfaces and implementation is specific to wildfly-9 server.
 */
@ServerEndpoint(value = "/liveLoggerConnector/{manufacturer}/{modelId}/{version}")
public class LiveLoggerConnector {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final WebSocketSessionPublisher webSocketSessionPublisher;

	public LiveLoggerConnector() {
		super();
		this.webSocketSessionPublisher = getWebSocketSessionPublisher();
		logger.trace("Initialized webSocketSessionPublisher is " + webSocketSessionPublisher);
	}

	@OnOpen
	public void onOpen(@PathParam("manufacturer") String manufacturer, @PathParam("modelId") String modelId,
			@PathParam("version") String version, Session websocketsession, EndpointConfig configuration) {
		DeviceModel deviceModel = new DeviceModelImpl(manufacturer, modelId, version);
		logger.trace("Websocket connection opened from client " + websocketsession.getBasicRemote().toString()
				+ " for device model" + deviceModel);
		if (webSocketSessionPublisher != null)
			webSocketSessionPublisher.publishNewSession(deviceModel, websocketsession);
	}

	@OnMessage
	public void receivedMessage(Session session, String msg) {
		logger.info("Received Message from Connected Client is " + msg);
	}

	@OnError
	public void error(Session session, Throwable error) {
		logger.error("Error with connected websocket client " + session.getBasicRemote().toString());
	}

	@OnClose
	public void close(@PathParam("manufacturer") String manufacturer, @PathParam("modelId") String modelId,
			@PathParam("version") String version, Session websocketsession, CloseReason reason) {
		DeviceModel deviceModel = new DeviceModelImpl(manufacturer, modelId, version);
		logger.trace("Closed websocket connection with client with reason " + reason.getCloseCode() + ":"
				+ reason.getReasonPhrase() + " for client " + websocketsession.getBasicRemote().toString()
				+ " connected on device model " + deviceModel);
		if (webSocketSessionPublisher != null)
			webSocketSessionPublisher.publishClosedSession(deviceModel, websocketsession);
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
