package com.hpe.iot.dc.tcp.groovy.watch.file.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.tcp.southbound.service.manager.TCPServerSocketServiceManager;

/**
 * @author sveera
 *
 */
public class GroovyFileEventListenerTest {

	private static final String RESOURCES_DIR = "resources";
	private static final String TEST_DIR = "test";
	private static final String SRC_DIR = "src";
	private static final String SAMPLE_GROOVY_BACKUP = "Sample_Backup.groovy";
	private static final String SAMPLE_GROOVY = "Sample.groovy";
	private static final String MODIFIED_GROOVY_DIR = "modifiedGroovy";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private ClassPathXmlApplicationContext applicationContext;

	@BeforeEach
	public void setUp() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("bean-config.xml");
	}

	@Test
	@DisplayName("test TCP DC after on the fly script modifications")
	public void testTCP_DC_AfterPluginScriptModification()
			throws IOException, URISyntaxException, InterruptedException {
		try {
			TCPServerSocketServiceManager tcpServerSocketServiceManager = applicationContext
					.getBean(TCPServerSocketServiceManager.class);
			copyModifiedScript();
			waitForProcessing();
			assertNotNull(tcpServerSocketServiceManager, "TCPDCInitializer Cannot be null");
			assertEquals(4, tcpServerSocketServiceManager.getRunningServerSocketServices().size(),
					"Expected Running services and actual running servcies are not same");
		} finally {
			applicationContext.close();
			revertModifiedScript();
		}
	}

	private void waitForProcessing() throws InterruptedException {
		Thread.sleep(5000);
	}

	private void copyModifiedScript() throws IOException, URISyntaxException {
		Path sourcePath = findPath(MODIFIED_GROOVY_DIR, SAMPLE_GROOVY);
		Path destinationPath = findPath("testGroovy", SAMPLE_GROOVY);
		copyFile(sourcePath, destinationPath);
	}

	private void revertModifiedScript() throws IOException {
		Path sourcePath = findPath(MODIFIED_GROOVY_DIR, SAMPLE_GROOVY_BACKUP);
		Path destinationPath = findPath("testGroovy", SAMPLE_GROOVY);
		copyFile(sourcePath, destinationPath);
	}

	private void copyFile(Path sourcePath, Path destinationPath) throws IOException {
		Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		logger.debug("Completed copying modified groovy file from " + sourcePath.toString() + " to "
				+ destinationPath.toString());
	}

	private Path findPath(String parentDirectory, String srcFileName) {
		return Paths.get(SRC_DIR + File.separator + TEST_DIR + File.separator + RESOURCES_DIR + File.separator
				+ parentDirectory + File.separator + srcFileName);

	}

}
