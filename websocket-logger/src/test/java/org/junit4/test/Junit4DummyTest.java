/**
 * 
 */
package org.junit4.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author sveera
 *
 */
public class Junit4DummyTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBasicAssertEquals() {
		Assert.assertEquals("Expected and Actual Values are not equal","","");
	}

}
