package org.junit5.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author sveera
 *
 */
public class Junit5DummyTest {

	@BeforeEach
	void init() {
	}

	@Test
	@DisplayName("Test Assert Equals")
	void testBasicAssertEquals() {
		String expectedValue = "Expected Value";
		assertEquals(expectedValue, "Expected Value", "Expected and Actual Values are not equal");

	}

	@Test
	@DisplayName("Test Assert Exception")
	void testBasicAssertThrows() {
		assertThrows(IllegalArgumentException.class, () -> {
			throw new IllegalArgumentException();
		});
	}

	@AfterEach
	void tearDown() {
	}
}