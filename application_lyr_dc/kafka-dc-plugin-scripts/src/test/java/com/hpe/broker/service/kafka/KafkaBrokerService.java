/**
 * 
 */
package com.hpe.broker.service.kafka;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.File;
import java.io.IOException;

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
	private final String kafkaDataDirectory;
	private KafkaServerStartable kafka;
	private TestingServer server;
	private String zookeeperConnectionString;

	public KafkaBrokerService(int port, String kafkaDataDirectory) {
		this.port = port;
		this.kafkaDataDirectory = kafkaDataDirectory;
	}

	public void startService() {
		try {
			server = new TestingServer();
			zookeeperConnectionString = server.getConnectString();
			ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
			CuratorFramework zookeeper = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
			zookeeper.start();
			Properties kafkaConnectProperties = new Properties();
			kafkaConnectProperties.setProperty("zookeeper.connect", zookeeperConnectionString);
			kafkaConnectProperties.setProperty("broker.id", "0");
			kafkaConnectProperties.setProperty("log.dir", kafkaDataDirectory);
			kafkaConnectProperties.setProperty("auto.create.topics.enable", "true");
			kafkaConnectProperties.setProperty("listeners", "PLAINTEXT://localhost:" + port);
			KafkaConfig config = new KafkaConfig(kafkaConnectProperties);
			kafka = new KafkaServerStartable(config);
			kafka.startup();
			logger.debug("Started embedded kafka broker");
		} catch (Exception ex) {
			throw new RuntimeException("Could not start kafka test broker", ex);
		}
	}

	public void stopService() {
		try {
			kafka.shutdown();
			server.stop();
			server.close();
		} catch (IOException e) {
			logExceptionStackTrace(e, getClass());
		} finally {
			deleteKafkaDataDirectory();
		}

	}

	private void deleteKafkaDataDirectory() {
		try {
			String[] directoryPaths = kafkaDataDirectory.split("/");
			String kafkaDataDirectory = "";
			for (int directoryIndexPath = 0; directoryIndexPath < directoryPaths.length - 1; directoryIndexPath++)
				kafkaDataDirectory += directoryPaths[directoryIndexPath]+"/";
			File index = new File(kafkaDataDirectory);
			if (index.exists())
				delete(index);
			else
				logger.trace("Kafka Data Directory doesn't exists " + kafkaDataDirectory);
		} catch (IOException e) {
			logExceptionStackTrace(e, getClass());
		}
	}

	private void delete(File file) throws IOException {
		if (file.isDirectory()) {
			if (file.list().length == 0) {
				file.delete();
				logger.trace("Directory is deleted : " + file.getAbsolutePath());
			} else {
				String files[] = file.list();
				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
				if (file.list().length == 0) {
					file.delete();
					logger.trace("Directory is deleted : " + file.getAbsolutePath());
				}
			}
		} else {
			file.delete();
			logger.trace("File is deleted : " + file.getAbsolutePath());
		}
	}

}
