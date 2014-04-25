package com.spidasoftware.schema.utils

import org.junit.Test
/**
 * Created with IntelliJ IDEA.
 * User: mford
 * Date: 7/24/13
 * Time: 2:45 PM
 */
class MimeDetectorTest {

	@Test
	public void testSpida() throws Exception {
		File file = new File(getClass().getResource("/rest/mime/test.spida").toURI())
		assert MimeDetector.detectMimeType(file)=="application/x-spidacalc"
	}

}
