package com.spidasoftware.schema.utils

/**
 * Created by jeremy on 4/15/14.
 */
class VersionUtilsTests extends GroovyTestCase {

	void testGetNumbers() {
		assert VersionUtils.getNumbers("1.2.3.4") == [1, 2, 3, 4]
		assert VersionUtils.getNumbers("1.2.3") == [1, 2, 3, 0]
		assert VersionUtils.getNumbers("1.2") == [1, 2, 0, 0]
		assert VersionUtils.getNumbers("1") == [1, 0, 0, 0]
	}

	void testIsNewer() {
		assert VersionUtils.isNewer("1.0.0.0", "0.1.0.0") //both same length
		assert VersionUtils.isNewer("0.1.0.0", "0.0.1.0") //both same length
		assert VersionUtils.isNewer("0.0.1.0", "0.0.0.1") //both same length
		assert VersionUtils.isNewer("1", "0.0.0.1") //different lengths
		assert !VersionUtils.isNewer("0.1", "1") //check false
	}

	void testIsOlder() {
		assert VersionUtils.isOlder("0.1.0.0", "1.0.0.0") //both same length
		assert VersionUtils.isOlder("0.0.1.0", "0.1.0.0") //both same length
		assert VersionUtils.isOlder("0.0.0.1", "0.0.1.0") //both same length
		assert VersionUtils.isOlder("0.0.0.1", "1") //different lengths
		assert !VersionUtils.isOlder("1", "0.1") //check false
	}
	
}
