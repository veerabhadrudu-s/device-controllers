/**
 * 
 */
package com.hpe.broker.service.consumer.activemq;

import static com.hpe.broker.utility.UtilityLogger.logExceptionStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

import com.hpe.broker.service.consumer.BrokerConsumerService;
import com.hpe.broker.service.consumer.handler.BrokerConsumerDataHandler;

/**
 * @author sveera
 *
 */
public class ActiveMQConsumerService implements BrokerConsumerService<String> {

	private static final int MAX_ACTIVE_SESSION_PER_CONN = 10;
	private static final int MAX_CONNECTION_POOL_SIZE = 50;

	private final Logger logger = getLogger(getClass());
	private final String brokerURL;
	private String userName;
	private String password;
	private PooledConnectionFactory pooledConnectionFactory;

	public ActiveMQConsumerService(String brokerURL) {
		super();
		this.brokerURL = brokerURL;
	}

	public ActiveMQConsumerService(String brokerURL, String userName, String password) {
		super();
		this.brokerURL = brokerURL;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public String getName() {
		return "activemq";
	}

	@Override
	public void startService() {

	}

	@Override
	public void stopService() {
		tryStoppingConsumerService();
	}

	private void tryStoppingConsumerService() {
		try {
			if (pooledConnectionFactory != null) {
				pooledConnectionFactory.clear();
				pooledConnectionFactory.stop();
			}
		} catch (Throwable throwable) {
			logExceptionStackTrace(throwable, getClass());
		}
	}

	@Override
	public void consumeData(String destination, BrokerConsumerDataHandler<String> brokerConsumerDataHandler) {
		tryConsumingFromDestination(destination, brokerConsumerDataHandler);
	}

	private void tryConsumingFromDestination(String destination,
			BrokerConsumerDataHandler<String> brokerConsumerDataHandler) {
		try {
			if (pooledConnectionFactory == null)
				initializePooledConnectionFactory();
			Connection activeMQConnection = pooledConnectionFactory.createConnection();
			for (int sessionIndex = 0; sessionIndex < MAX_ACTIVE_SESSION_PER_CONN; sessionIndex++) {
				Session session = activeMQConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				Queue queue = session.createQueue(destination);
				MessageConsumer consumer = session.createConsumer(queue);
				consumer.setMessageListener(new ActivemqMessageListener(destination, brokerConsumerDataHandler));
				activeMQConnection.start();
			}
		} catch (JMSException e) {
			logExceptionStackTrace(e, getClass());
		}
	}

	private void initializePooledConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = userName != null && !userName.isEmpty()
				&& password != null && !password.isEmpty()
						? new ActiveMQConnectionFactory(userName, password, brokerURL)
						: new ActiveMQConnectionFactory(brokerURL);
		pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
		pooledConnectionFactory.setMaxConnections(MAX_CONNECTION_POOL_SIZE);
		pooledConnectionFactory.setMaximumActiveSessionPerConnection(MAX_ACTIVE_SESSION_PER_CONN);
		pooledConnectionFactory.start();
		logger.trace("ActiveMQ Connection Factory instantiated " + pooledConnectionFactory);

	}

	private class ActivemqMessageListener implements MessageListener {

		private final BrokerConsumerDataHandler<String> brokerConsumerDataHandler;
		private final String destination;

		public ActivemqMessageListener(String destination,
				BrokerConsumerDataHandler<String> brokerConsumerDataHandler) {
			this.destination = destination;
			this.brokerConsumerDataHandler = brokerConsumerDataHandler;
		}

		@Override
		public void onMessage(Message message) {
			tryProcessingMessage(message);
		}

		private void tryProcessingMessage(Message message) {
			try {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					brokerConsumerDataHandler.handleConsumerMessage(destination, textMessage.getText());
				}
			} catch (Throwable e) {
				logExceptionStackTrace(e, getClass());
			}
		}
	}

}
