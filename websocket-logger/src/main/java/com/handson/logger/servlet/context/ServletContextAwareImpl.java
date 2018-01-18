package com.handson.logger.servlet.context;
/**
 * 
 */

import javax.servlet.ServletContext;
import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.websockets.jsr.ServerWebSocketContainer;

/**
 * @author sveera
 *
 */

public class ServletContextAwareImpl implements ServletContextAware {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void setServletContext(ServletContext servletContext) {
		logger.trace("Inside Servlet Context Aware " + servletContext.getClass().getName());
		WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
		logger.trace("Websocket Container " + webSocketContainer);
		logger.trace("Websocket Container Class Name " + webSocketContainer.getClass().getName());
		if (webSocketContainer instanceof ServerWebSocketContainer)
			if (servletContext instanceof ServletContextImpl)
				((ServerWebSocketContainer) webSocketContainer)
						.setContextToAddFilter((ServletContextImpl) servletContext);
	}

}
