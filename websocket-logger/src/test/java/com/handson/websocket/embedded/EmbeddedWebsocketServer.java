/**
 * 
 */
package com.handson.websocket.embedded;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.websockets.jsr.WebSocketDeploymentInfo.ATTRIBUTE_NAME;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.websocket.ContainerProvider;

import org.xnio.OptionMap;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import com.handson.logger.websocket.listener.LiveLoggerConnector;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.websockets.jsr.ServerWebSocketContainer;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;

/**
 * @author sveera
 *
 */
public class EmbeddedWebsocketServer {

	public static final String EMBEDDED_WEBSOCKETS_SERVER_NAME = "embedded-websockets";
	private final Undertow undertow;

	public EmbeddedWebsocketServer() throws IllegalArgumentException, IOException, ServletException {
		final Xnio xnio = Xnio.getInstance("nio", Undertow.class.getClassLoader());
		final XnioWorker xnioWorker = xnio.createWorker(OptionMap.builder().getMap());
		final WebSocketDeploymentInfo webSockets = new WebSocketDeploymentInfo().addEndpoint(LiveLoggerConnector.class)
				.setWorker(xnioWorker);
		final DeploymentManager deploymentManager = defaultContainer().addDeployment(deployment()
				.setClassLoader(LiveLoggerConnector.class.getClassLoader())
				.setContextPath(EMBEDDED_WEBSOCKETS_SERVER_NAME).setDeploymentName(EMBEDDED_WEBSOCKETS_SERVER_NAME)
				.addServletContextAttribute(ATTRIBUTE_NAME, webSockets));
		deploymentManager.deploy();
		if (ContainerProvider.getWebSocketContainer() instanceof ServerWebSocketContainer)
			((ServerWebSocketContainer) ContainerProvider.getWebSocketContainer())
					.setContextToAddFilter(deploymentManager.getDeployment().getServletContext());
		undertow = Undertow.builder().addHttpListener(8080, "localhost").setHandler(deploymentManager.start()).build();
	}

	public void start() {
		undertow.start();
	}

	public void stop() {
		undertow.stop();
	}

}
