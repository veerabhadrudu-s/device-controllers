/**
 * 
 */
package com.hpe.iot.http.test.base;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author sveera
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "/bean-servlet-context.xml", "/bean-config.xml" })
public abstract class HttpPluginTestBaseTemplate {

	protected static final String SOUTHBOUND = "/southbound";
	protected final JsonParser jsonParser = new JsonParser();

	@Autowired
	protected WebApplicationContext wac;
	protected MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		waitForDCInitialization();
	}

	@Test
	public void testGetDCInfo() throws Exception {
		JsonObject expectedResponse = getExpectedDCInfo();
		MvcResult mvcResult = mockMvc.perform(get(SOUTHBOUND + "/dcinfo").accept("application/json"))
				.andExpect(status().isOk()).andReturn();
		MockHttpServletResponse servletResponse = mvcResult.getResponse();
		JsonObject actualResponse = jsonParser.parse(servletResponse.getContentAsString()).getAsJsonObject();
		Assert.assertEquals("Expected and Actual Responses are not same.", expectedResponse, actualResponse);

	}

	private JsonObject getExpectedDCInfo() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"This is a Generic HTTP DC \",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

	private void waitForDCInitialization() throws InterruptedException {
		Thread.sleep(1000);
	}

	protected JsonObject getExpectedSuccessResponse() {
		String expectedResponseString = "{\"processingStatus\":\"SUCCESS\",\"otherInformation\":\"\",\"exceptionReason\":\"\"}";
		return jsonParser.parse(expectedResponseString).getAsJsonObject();
	}

}
