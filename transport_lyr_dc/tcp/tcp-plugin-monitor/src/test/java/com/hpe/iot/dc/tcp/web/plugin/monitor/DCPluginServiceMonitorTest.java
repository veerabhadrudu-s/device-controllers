package com.hpe.iot.dc.tcp.web.plugin.monitor;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration({ "/spring/tcp-plugin-service-monitor.xml", "/spring/test-tcp-plugin-service-monitor.xml" })
public class DCPluginServiceMonitorTest {

	private static final String TMP_SCRIPT_DIR = "target/tmp";
	private static final String TEST_DIR = "test";
	private static final String SRC_DIR = "src";
	private static final String JAVA_DIR = "java";

	private final JsonParser jsonParser = new JsonParser();
	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	@DisplayName("test Get Plugin Scripts")
	public void testGetPluginScripts() throws Exception {
		JsonObject expectedDeployedPlugins = getExpectedDeployedPlugins();
		MvcResult mvcResult = this.mockMvc
				.perform(request(HttpMethod.GET, "/getPluginScripts").accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		String responseContent = mvcResult.getResponse().getContentAsString();
		assertEquals(expectedDeployedPlugins, jsonParser.parse(responseContent).getAsJsonObject(),
				"Expected and Actual Plugin Scripts are not equal");
	}

	@Test
	@DisplayName("test upload plug-in script with Empty File")
	public void testHandleEmptyFileExcpetion() throws Exception {
		MockMultipartFile scriptFile = new MockMultipartFile("file", "sample.groovy", "text/plain", new byte[] {});
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.multipart("/uploadPluginScript").file(scriptFile).param("some-random", "4"))
				.andExpect(status().is(400)).andReturn();
		JsonObject actualResponse = jsonParser.parse(mvcResult.getResponse().getContentAsString()).getAsJsonObject();
		assertEquals(getExpectedFailureResponseForEmptyUploadedPluginScript(), actualResponse,
				"Expected anad actaual responses are not equal");
	}

	@Nested
	@ContextConfiguration({ "/spring/tcp-plugin-service-monitor.xml", "/spring/test-tcp-plugin-service-monitor.xml" })
	class PluginScriptUploadTest {

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
			MockMultipartFile scriptFile = new MockMultipartFile("file", "Sample.groovy", "text/plain",
					Files.readAllBytes(Paths.get(SRC_DIR + File.separator + TEST_DIR + File.separator + JAVA_DIR
							+ File.separator + "com/hpe/iot/dc/valid/Sample.groovy")));
			MvcResult mvcResult = mockMvc.perform(
					MockMvcRequestBuilders.multipart("/uploadPluginScript").file(scriptFile).param("some-random", "4"))
					.andExpect(status().is(202)).andReturn();
			JsonObject actualResponse = jsonParser.parse(mvcResult.getResponse().getContentAsString())
					.getAsJsonObject();
			assertEquals(getExpectedSuccessResponseForUploadPluginScript(), actualResponse,
					"Expected anad actaual responses are not equal");
		}

	}

	private JsonObject getExpectedDeployedPlugins() {
		return jsonParser.parse(
				"{\"data\":[{\"connectedDevices\":0,\"description\":\"\",\"manufacturer\":\"MMI\",\"modelId\":\"Safemate\",\"version\":\"1.0\"}]}")
				.getAsJsonObject();
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

}
