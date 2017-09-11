package com.hpe.broker.service.producer.activemq;

import static com.hpe.broker.utility.UtilityLogger.logExceptionStackTrace;
import static org.slf4j.LoggerFactory.getLogger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;

import com.hpe.broker.service.producer.BrokerProducerService;

/**
 * @author sveera
 *
 */
public class ActiveMQProducerService implements BrokerProducerService<String> {

	private static final int MAX_ACTIVE_SESSION_PER_CONN = 10;
	private static final int MAX_CONNECTION_POOL_SIZE = 50;

	private final Logger logger = getLogger(getClass());
	private final String brokerURL;
	private String userName;
	private String password;
	private PooledConnectionFactory pooledConnectionFactory;

	public ActiveMQProducerService(String brokerURL) {
		super();
		this.brokerURL = brokerURL;
	}

	public ActiveMQProducerService(String brokerURL, String userName, String password) {
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
		tryStoppingProducerService();
	}

	private void tryStoppingProducerService() {
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
	public void publishData(String destinationName, String value) {
		trySendingMessage(destinationName, value);
	}

	private void trySendingMessage(String destinationName, String value) {
		Connection activemqConnection = null;
		try {
			if (pooledConnectionFactory == null)
				initializePooledConnectionFactory();
			activemqConnection = pooledConnectionFactory.createConnection();
			Session activeSession = activemqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TextMessage textMessage = activeSession.createTextMessage();
			textMessage.setText(value);
			Destination destination = activeSession.createQueue(destinationName);
			MessageProducer producer = activeSession.createProducer(destination);
			producer.send(destination, textMessage);
		} catch (Throwable e) {
			logExceptionStackTrace(e, getClass());
			throw new RuntimeException(e);
		} finally {
			tryClosingResources(activemqConnection);
		}
	}

	private void tryClosingResources(Connection activemqConnection) {
		if (activemqConnection != null) {
			try {
				activemqConnection.close();
			} catch (JMSException e) {
				logExceptionStackTrace(e, getClass());
			}
		}
	}

	private void initializePooledConnectionFactory() {
		ActiveMQConnectionFactory activeMQConnectionFactory = userName != null && !userName.isEmpty()
				&& password != null && !password.isEmpty()
						? new ActiveMQConnectionFactory(userName, password, brokerURL)
						: new ActiveMQConnectionFactory(brokerURL);
		activeMQConnectionFactory.setUseAsyncSend(true);
		pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
		pooledConnectionFactory.setMaxConnections(MAX_CONNECTION_POOL_SIZE);
		pooledConnectionFactory.setMaximumActiveSessionPerConnection(MAX_ACTIVE_SESSION_PER_CONN);
		pooledConnectionFactory.start();
		logger.trace("ActiveMQ Connection Factory instantiated " + pooledConnectionFactory);

	}

	@Override
	public String toString() {
		return "ActiveMQProducerService [brokerURL=" + brokerURL + ", userName=" + userName + ", password=" + password
				+ "]";
	}

}
