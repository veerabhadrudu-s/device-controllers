/**
 * 
 */
package com.hpe.broker.service.kafka;

/**
 * @author sveera
 *
 */

import java.util.Properties;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

public class KafkaBrokerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final int port;
	private final String indexDir;
	private KafkaServerStartable kafka;
	private TestingServer server;

	public KafkaBrokerService(int port, String indexDir) {
		this.port = port;
		this.indexDir = indexDir;
	}

	public void startService() {
		try {
			server = new TestingServer();
			String zookeeperConnectionString = server.getConnectString();
			ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
			CuratorFramework zookeeper = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
			zookeeper.start();
			Properties kafkaConnectProperties = new Properties();
			kafkaConnectProperties.setProperty("zookeeper.connect", zookeeperConnectionString);
			kafkaConnectProperties.setProperty("broker.id", "0");
			kafkaConnectProperties.setProperty("port", "" + port);
			kafkaConnectProperties.setProperty("log.dir", indexDir);
			kafkaConnectProperties.setProperty("auto.create.topics.enable", "true");
			KafkaConfig config = new KafkaConfig(kafkaConnectProperties);
			kafka = new KafkaServerStartable(config);
			kafka.startup();
			logger.debug("Started embedded kafka broker");
		} catch (Exception ex) {
			throw new RuntimeException("Could not start kafka test broker", ex);
		}
	}

	public void stopService() throws Exception {
		kafka.shutdown();
		server.stop();
		server.close();
	}

}
