package com.spidasoftware.schema.utils

import net.sf.json.JSONObject

/**
 * Created by jeremy on 4/15/14.
 */
class VersionUtilsTests extends GroovyTestCase {

	void setUp(){
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("path/to/schema-3.jar") }
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(VersionUtils.class);
	}

	void testGetNumbers() {
		assert VersionUtils.getNumbers("1.2.3.4") == [1, 2, 3, 4]
		assert VersionUtils.getNumbers("1.2.3") == [1, 2, 3, 0]
		assert VersionUtils.getNumbers("1.2") == [1, 2, 0, 0]
		assert VersionUtils.getNumbers("1") == [1, 0, 0, 0]
		shouldFail(IllegalArgumentException, { VersionUtils.getNumbers("1.2.3.4-SNAPSHOT") } )
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

	void testGetCurrentVersion() {
		assert VersionUtils.getVersionFromFileName(new File("path/to/schema-1.2.3.4.jar")) == "1.2.3.4"
		assert VersionUtils.getVersionFromFileName(new File("path/to/schema-1-SNAPSHOT.jar")) == "1"
		assert VersionUtils.getVersionFromFileName(new File("path/to/schema-1.jar")) == "1"
		assert VersionUtils.getVersionFromFileName(new File("path/to/1")) == "1"
		assert VersionUtils.getVersionFromFileName(new File("1")) == "1"
		assert VersionUtils.getVersionFromFileName(new File("path/to/.s.c.h.e.m.a-1.2.3.4-ABC.ABC.ABC.jar")) == "1.2.3.4"
		assert VersionUtils.getVersionFromFileName(new File("path/to/A....B")) == null
	}

	void testAddCurrentVersion() {
		assert VersionUtils.getSchemaJarVersion() == "3"
		
		def jsonObject = new JSONObject()
		VersionUtils.addSchemaJarVersion(jsonObject)
		assert jsonObject.version == "3"
		
		assert VersionUtils.addSchemaJarVersion("{}") == '{"version":"3"}'
	}

}
