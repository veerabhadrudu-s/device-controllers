/**
 * 
 */
package com.handson.logger.service.impl;

import static com.handson.util.UtilityLogger.logExceptionStackTrace;
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
import com.handson.logger.service.DeploymentLoggerService;
import com.handson.logger.websocket.session.DeploymentLoggerSessionPublisher;

/**
 * @author sveera
 *
 */
public class WebsocketDeploymentLoggerService implements DeploymentLoggerService, DeploymentLoggerSessionPublisher {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Map<String, List<Session>> connectedUsers;

	public WebsocketDeploymentLoggerService() {
		connectedUsers = new ConcurrentHashMap<>();
	}

	@Override
	public void log(String pluginScriptFile, String logMessage) {
		for (Session userSession : readExistingSessions(pluginScriptFile))
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
	public void publishNewSession(String pluginScriptFile, Session websocketsession) {
		readExistingSessions(pluginScriptFile).add(websocketsession);
	}

	@Override
	public void publishClosedSession(String pluginScriptFile, Session websocketsession) {
		readExistingSessions(pluginScriptFile).remove(websocketsession);
	}

	private List<Session> readExistingSessions(String pluginScriptFile) {
		if (connectedUsers.get(pluginScriptFile) == null)
			connectedUsers.put(pluginScriptFile, new CopyOnWriteArrayList<>());
		return connectedUsers.get(pluginScriptFile);
	}

}
