package com.hpe.iot.dc.web.plugin.monitor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hpe.iot.dc.model.DeviceModel;
import com.hpe.iot.service.initializer.groovy.file.GroovyScriptFileToDeviceModelHolder;
import com.hpe.iot.service.initializer.groovy.file.impl.GroovyScriptFileToDeviceModelHolderImpl;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration("/spring/application-lyr-dc-plugin-moniter-service.xml")
public class DCPluginServiceMonitorTest {

	private static final String TMP_SCRIPT_DIR = "target/tmp";
	private static final String TEST_DIR = "test";
	private static final String SRC_DIR = "src";
	private static final String JAVA_DIR = "java";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final JsonParser jsonParser = new JsonParser();
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("test Get plug-in scripts")
	public final void testGetPluginScripts() throws Exception {
		MvcResult mvcResult = mockMvc.perform(get("/getPluginScripts").accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(mockHttpServletResponse.getContentAsString()).getAsJsonObject();
		logger.trace("Get Plugins REST response " + actualResponse.toString());
		assertEquals(getExpectedPluginScripts(), actualResponse, "Expected anad actaual responses are not equal");
	}

	@Nested
	@ContextConfiguration("/spring/application-lyr-dc-plugin-moniter-service.xml")
	class PluginScriptUplaodTest {

		@BeforeEach
		public void setUp() {
			File tmpFileDir = new File(TMP_SCRIPT_DIR);
			if (!tmpFileDir.exists())
				tmpFileDir.mkdirs();
		}

		@AfterEach
		public void tearDown() throws IOException {
			File tmpFileDir = new File(TMP_SCRIPT_DIR);
			if (tmpFileDir.exists())
				FileUtils.deleteDirectory(tmpFileDir);
		}

		@Test
		@DisplayName("test upload plug-in script")
		public final void testUploadPluginScript() throws Exception {
			MockMultipartFile scriptFile = new MockMultipartFile("file", "sample.groovy", "text/plain",
					Files.readAllBytes(Paths.get(SRC_DIR + File.separator + TEST_DIR + File.separator + JAVA_DIR
							+ File.separator + "com/hpe/iot/http/groovyscript/sample/model/sample.groovy")));
			MvcResult mvcResult = mockMvc.perform(
					MockMvcRequestBuilders.multipart("/uploadPluginScript").file(scriptFile).param("some-random", "4"))
					.andExpect(status().is(202)).andReturn();
			JsonObject actualResponse = jsonParser.parse(mvcResult.getResponse().getContentAsString())
					.getAsJsonObject();
			logger.trace("Upload Plugin REST response " + actualResponse.toString());
			assertEquals(getExpectedSuccessResponseForUploadPluginScript(), actualResponse,
					"Expected anad actaual responses are not equal");
		}

	}

	@Test
	@DisplayName("test upload plug-in script with Empty File")
	public final void testUploadPluginScriptWithEmptyFile() throws Exception {
		MockMultipartFile scriptFile = new MockMultipartFile("file", "sample.groovy", "text/plain", new byte[] {});
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.multipart("/uploadPluginScript").file(scriptFile).param("some-random", "4"))
				.andExpect(status().is(400)).andReturn();
		JsonObject actualResponse = jsonParser.parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		logger.trace("Upload Plugin REST response " + actualResponse.toString());
		assertEquals(getExpectedFailureResponseForEmptyUploadedPluginScript(), actualResponse,
				"Expected anad actaual responses are not equal");
	}

	private JsonObject getExpectedPluginScripts() {
		String expectedResponseString = "{\"data\":["
				+ "{\"pluginScriptFileName\":\"sample_v2.groovy\",\"manufacturer\":\"sample\",\"modelId\":\"model\",\"version\":\"2.0\"},"
				+ "{\"pluginScriptFileName\":\"sample_v1.groovy\",\"manufacturer\":\"sample\",\"modelId\":\"model\",\"version\":\"1.0\"}]}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedSuccessResponseForUploadPluginScript() {
		String expectedResponseString = "{\"status\":"
				+ "\"Plugin script uploaded successfully.Service for script will be started after validation of script.\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private JsonObject getExpectedFailureResponseForEmptyUploadedPluginScript() {
		String expectedResponseString = "{\"status\":" + "\"Uploaded File is Empty\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	@Configuration
	@ComponentScan("com.hpe.iot.dc.web.plugin.monitor")
	static class DCPluginServiceMonitorConfiguration {

		@Bean("pluginScriptsPath")
		public String getPluginScriptsPath() {
			return TMP_SCRIPT_DIR;
		}

		@Bean
		public GroovyScriptFileToDeviceModelHolder getGroovyScriptFileToDeviceModelHolder() {
			GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolder = new GroovyScriptFileToDeviceModelHolderImpl();
			loadWithSamplePluginVersion_1(groovyScriptFileToDeviceModelHolder);
			loadWithSamplePluginVersion_2(groovyScriptFileToDeviceModelHolder);
			return groovyScriptFileToDeviceModelHolder;
		}

		private void loadWithSamplePluginVersion_2(
				GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolder) {
			groovyScriptFileToDeviceModelHolder.addScriptDeviceModel("sample_v2.groovy", new DeviceModel() {
				@Override
				public String getManufacturer() {
					return "sample";
				}

				@Override
				public String getModelId() {
					return "model";
				}

				@Override
				public String getVersion() {
					return "2.0";
				}
			});
		}

		private void loadWithSamplePluginVersion_1(
				GroovyScriptFileToDeviceModelHolderImpl groovyScriptFileToDeviceModelHolder) {
			groovyScriptFileToDeviceModelHolder.addScriptDeviceModel("sample_v1.groovy", new DeviceModel() {
				@Override
				public String getManufacturer() {
					return "sample";
				}

				@Override
				public String getModelId() {
					return "model";
				}

				@Override
				public String getVersion() {
					return "1.0";
				}
			});
		}

	}

}
