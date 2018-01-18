/**
 * 
 */
package com.handson.logger.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.logger.service.LoggerService;
import com.handson.logger.websocket.session.WebSocketSessionPublisher;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class WebsocketLoggerServiceAdaptee implements LoggerService, WebSocketSessionPublisher {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<DeviceModel, List<Session>> connectedUsers;

	public WebsocketLoggerServiceAdaptee() {
		connectedUsers = new ConcurrentHashMap<>();
	}

	@Override
	public void log(DeviceModel deviceModel, String message) {
		for (Session userSession : readExistingSessions(deviceModel))
			if (userSession.isOpen())
				tryPushingMessage(message, userSession);
	}

	private void tryPushingMessage(String jsonString, Session webSocketSession) {
		try {
			webSocketSession.getBasicRemote().sendText(jsonString);
		} catch (IOException e) {
			logger.error("Failed to Write on websocket Client");
			logExceptionStackTrace(e, getClass());
		}
	}

	@Override
	public void publishNewSession(DeviceModel deviceModel, Session websocketsession) {
		readExistingSessions(deviceModel).add(websocketsession);
	}

	@Override
	public void publishClosedSession(DeviceModel deviceModel, Session websocketsession) {
		readExistingSessions(deviceModel).remove(websocketsession);
	}

	private List<Session> readExistingSessions(DeviceModel deviceModel) {
		if (connectedUsers.get(deviceModel) == null)
			connectedUsers.put(deviceModel, new CopyOnWriteArrayList<>());
		return connectedUsers.get(deviceModel);
	}

	public void logExceptionStackTrace(Throwable ex, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error(errors.toString());
	}

	public String toStringExceptionStackTrace(Throwable ex, Class<?> classType) {
		Logger logger = LoggerFactory.getLogger(classType);
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		logger.error(errors.toString());
		return errors.toString();
	}
}
