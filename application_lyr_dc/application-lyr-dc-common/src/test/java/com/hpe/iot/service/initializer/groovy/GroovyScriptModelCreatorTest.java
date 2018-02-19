package com.hpe.iot.service.initializer.groovy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.handson.iot.dc.util.DirectoryFileScanner;
import com.hpe.iot.bean.pool.ServerBeanPool;
import com.hpe.iot.northbound.service.inflow.IOTPublisherService;
import com.hpe.iot.service.initializer.groovy.GroovyScriptModelCreator.ScriptFileValidationError;
import com.hpe.iot.service.initializer.groovy.model.GroovyScriptModel;

public class GroovyScriptModelCreatorTest {

	private final DirectoryFileScanner directoryFileScanner = new DirectoryFileScanner();
	private GroovyScriptModelCreator groovyScriptModelCreator;

	@BeforeEach
	public void setUp() throws Exception {
		groovyScriptModelCreator = new GroovyScriptModelCreator(new IOTPublisherServiceServerBeanPoolProvider());
	}

	@Test
	@DisplayName("test GroovyScriptModelCreator")
	public void testGroovyScriptModelCreator() {
		assertNotNull(groovyScriptModelCreator, GroovyScriptModelCreator.class.getSimpleName() + " cannot be null");
	}

	@Test
	@DisplayName("test Create Groovy Script Model for empty script")
	public void testCreateGroovyScriptModelForInEmptyScript() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		List<String> groovyScriptFiles = directoryFileScanner
				.getDirectoryFileNames("src/test/java/com/hpe/iot/dc/groovyscript/empty/script");
		assertThrows(ScriptFileValidationError.class,
				() -> groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFiles.get(0)),
				"Expected " + ScriptFileValidationError.class.getSimpleName() + " for invalid script");
	}

	@Test
	@DisplayName("test Create Groovy Script Model for invalid script")
	public void testCreateGroovyScriptModelForInValidScript() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		List<String> groovyScriptFiles = directoryFileScanner
				.getDirectoryFileNames("src/test/java/com/hpe/iot/dc/groovyscript/invalid/model");
		assertThrows(ScriptFileValidationError.class,
				() -> groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFiles.get(0)),
				"Expected " + ScriptFileValidationError.class.getSimpleName() + " for invalid script");
	}

	@Test
	@DisplayName("test Create Groovy Script Model for live logger validation violated script")
	public void testCreateGroovyScriptModelForLiveLoggerValidationViolatedScript() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<String> groovyScriptFiles = directoryFileScanner
				.getDirectoryFileNames("src/test/java/com/hpe/iot/dc/groovyscript/validation/error/livelogger");
		assertThrows(ScriptFileValidationError.class,
				() -> groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFiles.get(0)),
				"Expected " + ScriptFileValidationError.class.getSimpleName() + " for invalid script");
	}

	@Test
	@DisplayName("test Create Groovy Script Model for deviceIdExtractor validation violated script")
	public void testCreateGroovyScriptModelForDeviceIdExtractorValidationViolatedScript() throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		List<String> groovyScriptFiles = directoryFileScanner
				.getDirectoryFileNames("src/test/java/com/hpe/iot/dc/groovyscript/validation/error/deviceIdExtractor");
		assertThrows(ScriptFileValidationError.class,
				() -> groovyScriptModelCreator.createGroovyScriptModel(groovyScriptFiles.get(0)),
				"Expected " + ScriptFileValidationError.class.getSimpleName() + " for invalid script");
	}

	@Test
	@DisplayName("test Create Groovy Script Model for valid script")
	public void testCreateGroovyScriptModelForValidScript() throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {
		List<String> groovyScriptFiles = directoryFileScanner
				.getDirectoryFileNames("src/test/java/com/hpe/iot/dc/groovyscript/sample/model");
		GroovyScriptModel groovyScriptModel = groovyScriptModelCreator
				.createGroovyScriptModel(groovyScriptFiles.get(0));
		assertNotNull(groovyScriptModel, GroovyScriptModel.class.getSimpleName() + " cannot be null");

	}

	private class IOTPublisherServiceServerBeanPoolProvider implements ServerBeanPool {

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getBean(Class<T> classType) {
			if (IOTPublisherService.class.isAssignableFrom(classType))
				return (T) (IOTPublisherService<?, ?>) (request, containerName) -> null;
			return null;
		}
	}

}
