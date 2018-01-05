package com.hpe.iot.dc.tcp.websocket.plugin.monitor;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.hpe.iot.dc.tcp.southbound.model.ServerSocketToDeviceModel;
import com.hpe.iot.dc.tcp.southbound.socket.listener.SocketChangeListener;
import com.hpe.iot.dc.tcp.web.plugin.monitor.ScriptPlugin;

/**
 * @author sveera
 *
 */
public class SocketChangePublisherService implements WebSocketSessionPublisher, SocketChangeListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<Session> connectedClientSessions;

	public SocketChangePublisherService() {
		super();
		this.connectedClientSessions = new CopyOnWriteArrayList<>();
	}

	@Override
	public void handleChangedCount(ServerSocketToDeviceModel serverSocketToDeviceModel, int deviceCount) {
		logger.trace("Connected Device's modified for device model " + serverSocketToDeviceModel + " with count "
				+ deviceCount);
		ScriptPlugin scriptPlugin = new ScriptPlugin(serverSocketToDeviceModel.getManufacturer(),
				serverSocketToDeviceModel.getModelId(), serverSocketToDeviceModel.getVersion(), deviceCount,
				serverSocketToDeviceModel.getDescription());
		String jsonString = new Gson().toJson(scriptPlugin);
		for (Session webSocketSession : connectedClientSessions)
			if (webSocketSession.isOpen())
				tryPushingMessage(jsonString, webSocketSession);

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
	public void publishNewSession(Session websocketsession) {
		logger.trace("Connected new Websocket Client " + websocketsession.getBasicRemote().toString());
		connectedClientSessions.add(websocketsession);
	}

	@Override
	public void publishClosedSession(Session websocketsession) {
		logger.trace("Disconnected Websocket Client " + websocketsession.getBasicRemote().toString());
		connectedClientSessions.remove(websocketsession);
	}

}
