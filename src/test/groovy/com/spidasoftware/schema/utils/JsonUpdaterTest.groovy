package com.spidasoftware.schema.utils

import com.spidasoftware.schema.changesets.ChangeSet
import com.spidasoftware.schema.validation.Validator
import net.sf.json.JSONObject

/**
 * Created by jeremy on 4/14/14.
 */
class JsonUpdaterTest extends GroovyTestCase {

	JsonUpdater jsonUpdater

	void setUp(){
		jsonUpdater = new JsonUpdater()
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(JsonUpdater.class);
		GroovySystem.metaClassRegistry.removeMetaClass(Validator.class);
	}

	void testUpdateWithJsonVersion() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.metaClass.getJarFile = { new File("path/to/schema-2.jar") }
		jsonUpdater.versionToChangeSetsMap = ["1":[NoChangeSet], "2":[TestChangeSet1], "3":[TestChangeSet2]]
		String oldString = '{"version":"1","key1":"123"}'
		String newString = jsonUpdater.update("/test", oldString)
		assert newString == '{"version":"2","key2":"123"}' //TestChangeSet1 is run and version is now at current version 2
	}

	void testUpdateWithoutJsonVersionPassValidation() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.metaClass.getJarFile = { new File("path/to/schema-2.jar") }
		jsonUpdater.versionToChangeSetsMap = ["1":[NoChangeSet], "2":[TestChangeSet1], "3":[TestChangeSet2]]
		String oldString = '{"key1":"123"}'
		String newString = jsonUpdater.update("/test", oldString)
		assert newString == '{"key1":"123","version":"2"}' //no changesets are run, but it is valid, so version is added
	}

	void testUpdateWithoutJsonVersionFailValidation() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> false }
		jsonUpdater.metaClass.getJarFile = { new File("path/to/schema-2.jar") }
		jsonUpdater.versionToChangeSetsMap = ["1":[NoChangeSet], "2":[TestChangeSet1], "3":[TestChangeSet2]]
		String oldString = '{"key1":"123"}'
		String newString = jsonUpdater.update("/test", oldString)
		assert newString == '{"key3":"123"}' //all changesets are run, but it still doesn't pass validation, so no version is added
	}

	void testGetChangeSetsToRun() {
		jsonUpdater.versionToChangeSetsMap = ["1":[], "2":[NoChangeSet], "3":[TestChangeSet1], "4":[TestChangeSet2]]
		jsonUpdater.getChangeSetsToApply("/test", "2", "4") == ["3":[TestChangeSet1], "4":[TestChangeSet2]]
		jsonUpdater.getChangeSetsToApply("/test", "3", "4") == ["4":[TestChangeSet2]]
		jsonUpdater.getChangeSetsToApply("/test", "4", "4") == [:] //no changeset apply since it is the current version
		jsonUpdater.getChangeSetsToApply("/none", "2", "4") == [:] //no changeset apply since no schema paths match
	}

	void testSortMapByVersionKey() {
		def unsorted = ["1.0.0":[], "0.0.1":[], "0.1.0":[]]
		def sorted = ["0.0.1":[], "0.1.0":[], "1.0.0":[]]
		assert jsonUpdater.sortMapByVersionKey(unsorted) == sorted
	}

	void testGetCurrentVersion() {
		assert jsonUpdater.getCurrentVersion(new File("path/to/schema-1.2.3.4.jar")) == "1.2.3.4"
		assert jsonUpdater.getCurrentVersion(new File("path/to/schema-1.jar")) == "1"
	}

	class TestChangeSet1 implements ChangeSet {
		String schemaPath = "/test"

		@Override
		void convert(JSONObject jsonObject) {
			if(jsonObject.containsKey("key1")){
				jsonObject.key2 = jsonObject.key1
				jsonObject.key1 = null
			}
		}
	}

	class TestChangeSet2 implements ChangeSet {
		String schemaPath = "/test"

		@Override
		void convert(JSONObject jsonObject) {
			if(jsonObject.containsKey("key2")){
				jsonObject.key3 = jsonObject.key2
				jsonObject.key2 = null
			}
		}
	}

	class NoChangeSet implements ChangeSet {
		String schemaPath = "/test"

		@Override
		void convert(JSONObject jsonObject) {
		}
	}

}
