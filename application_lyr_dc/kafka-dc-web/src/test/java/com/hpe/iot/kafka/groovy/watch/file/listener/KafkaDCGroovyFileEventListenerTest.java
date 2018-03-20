/**
 * 
 */
package com.hpe.iot.kafka.groovy.watch.file.listener;

import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_MODEL;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_VERSION;
import static com.hpe.iot.kafka.test.constants.TestConstants.SAMPLE_VERSION_MODIFIED;
import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.dc.model.DeviceModelImpl;
import com.hpe.iot.model.factory.DeviceModelFactory;
import com.hpe.iot.northbound.handler.outflow.DownlinkPayloadProcessor;
import com.hpe.iot.northbound.handler.outflow.PayloadCipher;
import com.hpe.iot.northbound.handler.outflow.factory.impl.NorthboundPayloadExtractorFactory;
import com.hpe.iot.southbound.handler.inflow.DeviceIdExtractor;
import com.hpe.iot.southbound.handler.inflow.MessageTypeExtractor;
import com.hpe.iot.southbound.handler.inflow.PayloadDecipher;
import com.hpe.iot.southbound.handler.inflow.UplinkPayloadProcessor;
import com.hpe.iot.southbound.handler.inflow.factory.impl.SouthboundPayloadExtractorFactory;

/**
 * @author sveera
 *
 */
public class KafkaDCGroovyFileEventListenerTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String SRC_DIR = "src";
	private static final String TEST_DIR = "test";
	private static final String JAVA_DIR = "java";
	private static final String RESOURCE_DIR = "resources";
	private static final String SCRIPT_FILE_DIR = SRC_DIR + separator + TEST_DIR + separator + JAVA_DIR + separator
			+ "com" + separator + "hpe" + separator + "iot" + separator + "kafka" + separator + "groovyscript"
			+ separator + "sample" + separator + "model";
	private static final String MODIFIED_GROOVY_DIR = SRC_DIR + separator + TEST_DIR + separator + RESOURCE_DIR
			+ separator + "backup_script";
	private static final String SAMPLE_GROOVY_BACKUP = "sample_Backup.groovy";
	private static final String SAMPLE_GROOVY = "sample.groovy";
	private static final String MODIFIED_SCRIPT_CLASS_TYPE = "com.hpe.iot.kafka.groovyscript.sample.model.modified.SampleDeviceModelHandler";
	private ClassPathXmlApplicationContext classPathXmlApplicationContext;
	private DeviceModelFactory deviceModelFactory;
	private SouthboundPayloadExtractorFactory southboundPayloadExtractorFactory;
	private NorthboundPayloadExtractorFactory northboundPayloadExtractorFactory;

	@BeforeEach
	public void setUp() throws InterruptedException {
		classPathXmlApplicationContext = new ClassPathXmlApplicationContext("/bean-config.xml");
		deviceModelFactory = classPathXmlApplicationContext.getBean(DeviceModelFactory.class);
		southboundPayloadExtractorFactory = classPathXmlApplicationContext
				.getBean(SouthboundPayloadExtractorFactory.class);
		northboundPayloadExtractorFactory = classPathXmlApplicationContext
				.getBean(NorthboundPayloadExtractorFactory.class);
	}

	@AfterEach
	public void tearDown() {
		classPathXmlApplicationContext.close();
	}

	@Test
	@DisplayName("test KAFKA DC after on the fly script modification")
	public void testKafkaDCForModifiedGroovyScriptFile() throws IOException, URISyntaxException, InterruptedException {
		DeviceModel sampleDeviceModelBeforeScriptModification = this.deviceModelFactory.findDeviceModel(SAMPLE,
				SAMPLE_MODEL, SAMPLE_VERSION);
		try {
			copyModifiedScript();
			waitForScriptLoading();
			DeviceModel sampleDeviceModelAfterScriptModification = this.deviceModelFactory.findDeviceModel(SAMPLE,
					SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED);
			PayloadDecipher payloadDecipher = southboundPayloadExtractorFactory.getPayloadDecipher(SAMPLE, SAMPLE_MODEL,
					SAMPLE_VERSION_MODIFIED);
			DeviceIdExtractor deviceIdExtractor = southboundPayloadExtractorFactory.getDeviceIdExtractor(SAMPLE,
					SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED);
			MessageTypeExtractor messageTypeExtractor = southboundPayloadExtractorFactory
					.getMessageTypeExtractor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED);
			UplinkPayloadProcessor uplinkPayloadProcessor = southboundPayloadExtractorFactory
					.getUplinkPayloadProcessor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED);
			PayloadCipher payloadCipher = northboundPayloadExtractorFactory.getPayloadCipher(SAMPLE, SAMPLE_MODEL,
					SAMPLE_VERSION_MODIFIED);
			DownlinkPayloadProcessor downlinkPayloadProcessor = northboundPayloadExtractorFactory
					.getDownlinkPayloadProcessor(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED);
			assertDeviceModels(new DeviceModelImpl(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION),
					sampleDeviceModelBeforeScriptModification);
			assertDeviceModels(new DeviceModelImpl(SAMPLE, SAMPLE_MODEL, SAMPLE_VERSION_MODIFIED),
					sampleDeviceModelAfterScriptModification);
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, payloadDecipher.getClass());
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, deviceIdExtractor.getClass());
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, messageTypeExtractor.getClass());
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, uplinkPayloadProcessor.getClass());
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, payloadCipher.getClass());
			assertPayloadHandlerClassType(MODIFIED_SCRIPT_CLASS_TYPE, downlinkPayloadProcessor.getClass());
		} finally {
			revertModifiedScript();
		}
	}

	private void waitForScriptLoading() throws InterruptedException {
		Thread.sleep(5000);
	}

	private void copyModifiedScript() throws IOException, URISyntaxException {
		Path sourcePath = findPath(MODIFIED_GROOVY_DIR, SAMPLE_GROOVY);
		Path destinationPath = findPath(SCRIPT_FILE_DIR, SAMPLE_GROOVY);
		copyFile(sourcePath, destinationPath);
	}

	private void revertModifiedScript() throws IOException {
		Path sourcePath = findPath(MODIFIED_GROOVY_DIR, SAMPLE_GROOVY_BACKUP);
		Path destinationPath = findPath(SCRIPT_FILE_DIR, SAMPLE_GROOVY);
		copyFile(sourcePath, destinationPath);
	}

	private void copyFile(Path sourcePath, Path destinationPath) throws IOException {
		Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		logger.debug("Completed copying modified groovy file from " + sourcePath.toString() + " to "
				+ destinationPath.toString());
	}

	private Path findPath(String directoryPath, String srcFileName) {
		return Paths.get(directoryPath + separator + srcFileName);
	}

	private void assertDeviceModels(final DeviceModel expectedDeviceModel, final DeviceModel actualDeviceModel) {
		assertNotNull(actualDeviceModel, "Actual device model cannot be null");
		assertEquals(expectedDeviceModel.getManufacturer(), actualDeviceModel.getManufacturer(),
				"Expected and actual manufacturers are not same");
		assertEquals(expectedDeviceModel.getModelId(), actualDeviceModel.getModelId(),
				"Expected and actual model id's are not same");
		assertEquals(expectedDeviceModel.getVersion(), actualDeviceModel.getVersion(),
				"Expected and actual version's are not same");
	}

	private void assertPayloadHandlerClassType(String expectedClassName, Class<?> actualClassType) {
		assertNotNull(actualClassType, "Actual Class Type cannot be null");
		assertEquals(expectedClassName, actualClassType.getName(), "Expected and actual class types are not equal");
	}

}
