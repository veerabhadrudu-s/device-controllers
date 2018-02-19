/**
 * 
 */
package com.handson.logger.service.impl;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.lang.String.format;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.handson.logger.service.LoggerService;
import com.handson.logger.websocket.session.LiveLoggerSessionPublisher;
import com.hpe.iot.dc.model.DeviceModel;

/**
 * @author sveera
 *
 */
public class WebsocketLiveLoggerServiceAdaptee implements LoggerService, LiveLoggerSessionPublisher {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<DeviceModel, List<Session>> connectedUsers;

	public WebsocketLiveLoggerServiceAdaptee() {
		connectedUsers = new ConcurrentHashMap<>();
	}

	@Override
	public void log(DeviceModel deviceModel, String logMessage) {
		for (Session userSession : readExistingSessions(deviceModel))
			if (userSession.isOpen())
				tryPushingMessage(logMessage, userSession);
	}

	private void tryPushingMessage(String logMessage, Session webSocketSession) {
		try {
			JsonObject jsonLog = new JsonObject();
			jsonLog.addProperty("logMessage", format("%s : %s", new Date().toString(), logMessage));
			webSocketSession.getBasicRemote().sendText(jsonLog.toString());
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

}
