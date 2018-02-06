/**
 * 
 */
package com.handson.logger.websocket.listener;

import static com.handson.logger.constants.DeviceModelConstants.MANUFACTURER;
import static com.handson.logger.constants.DeviceModelConstants.MODEL_ID;
import static com.handson.logger.constants.DeviceModelConstants.VERSION;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocket.Connection;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.handson.logger.LiveLogger;
import com.handson.logger.constants.DeviceModelConstants;
import com.handson.logger.impl.LiveLoggerAdapter;
import com.handson.logger.service.impl.WebsocketLiveLoggerServiceAdaptee;
import com.handson.logger.websocket.session.LiveLoggerSessionPublisher;
import com.handson.websocket.embedded.EmbeddedWebsocketServer;
import com.hpe.iot.dc.model.Device;
import com.hpe.iot.dc.model.DeviceImpl;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

/**
 * @author sveera
 */
public class LiveLoggerConnectorIntergationTest {
	private static final String LOG_MESSAGE_KEY = "logMessage";
	@Mocked
	private WebApplicationContext webApplicationContext;
	private EmbeddedWebsocketServer embeddedWebsocketListener;
	private WebsocketLiveLoggerServiceAdaptee loggerServiceAdaptee;
	private LiveLogger liveLogger;
	private WebsocketClientListenerSpy websocketClientListenerSpy;
	private Connection clientConnection;

	@BeforeEach
	public void setUp() throws Exception {
		loggerServiceAdaptee = new WebsocketLiveLoggerServiceAdaptee();
		liveLogger = new LiveLoggerAdapter(loggerServiceAdaptee, DeviceModelConstants.DEVICE_MODEL);
		embeddedWebsocketListener = new EmbeddedWebsocketServer();
		websocketClientListenerSpy = new WebsocketClientListenerSpy();
		embeddedWebsocketListener.start();
		mockSpringContainer();
		clientConnection = connectToLoggerWebsocketService();
	}

	@AfterEach
	public void tearDown() throws IllegalArgumentException, IOException, ServletException {
		clientConnection.close();
		embeddedWebsocketListener.stop();
	}

	@Test
	@DisplayName("test liveLogger with null input")
	public void testLoggerWithNull_ExpectatingNoNullPointerException() {
		liveLogger.log(null);
		assertNull(websocketClientListenerSpy.getLoggedMessage());
	}

	@Test
	@DisplayName("test liveLogger with String data")
	public void testLoggerWithStringData() throws InterruptedException {
		String message = "test log message";
		liveLogger.log(message);
		assertLoggedMessages(message);
	}

	@Test
	@DisplayName("test liveLogger with Device Data Type data")
	public void testLoggerWithDeviceDataType() throws InterruptedException {
		Device device = new DeviceImpl(DeviceModelConstants.MANUFACTURER, DeviceModelConstants.MODEL_ID,
				DeviceModelConstants.VERSION, "10000");
		liveLogger.log(device);
		assertLoggedMessages(device.toString());
	}

	@Test
	@DisplayName("test liveLogger with Integer Wrapper Type data")
	public void testLoggerWithWrapperDataType() throws InterruptedException {
		int logMessage = 1000;
		liveLogger.log(new Integer(logMessage));
		assertLoggedMessages(new Integer(logMessage).toString());
	}

	private void assertLoggedMessages(String expectedLoggedMessage) throws InterruptedException {
		waitForLogProcessing();
		String socketMessage = websocketClientListenerSpy.getLoggedMessage();
		JsonParser parser = new JsonParser();
		JsonObject jsonLogMessage = (JsonObject) parser.parse(socketMessage);
		assertTrue(jsonLogMessage.get(LOG_MESSAGE_KEY).getAsString().endsWith(expectedLoggedMessage),
				"Expected and actual logged messages are not same");
	}

	private Connection connectToLoggerWebsocketService() throws Exception {
		// WebSocketClient client = new WebSocketClientFactory().newWebSocketClient();
		WebSocketClient client = new WebSocketClient();
		Future<Connection> connectionFuture = client.open(new URI(getConnectionString()), websocketClientListenerSpy);
		Thread.sleep(1000);
		return connectionFuture.get(2, TimeUnit.SECONDS);
	}

	private String getConnectionString() {
		return "ws://localhost:8080" + "/liveLoggerConnector/" + MANUFACTURER + "/" + MODEL_ID + "/" + VERSION;
	}

	private void waitForLogProcessing() throws InterruptedException {
		Thread.sleep(1000);
	}

	private class WebsocketClientListenerSpy implements WebSocket.OnTextMessage {

		private String loggedMessage;

		public void onClose(int arg0, String arg1) {
		}

		@Override
		public void onOpen(Connection arg0) {
		}

		@Override
		public void onMessage(String loggedMessage) {
			this.loggedMessage = loggedMessage;
		}

		public String getLoggedMessage() {
			return loggedMessage;
		}
	}

	public void mockSpringContainer() {
		new MockUp<WebApplicationContextUtils>() {
			@Mock
			public WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
				return webApplicationContext;
			}
		};
		new Expectations() {
			{
				webApplicationContext.getBean(LiveLoggerSessionPublisher.class);
				result = loggerServiceAdaptee;
			}
		};

	}
}
