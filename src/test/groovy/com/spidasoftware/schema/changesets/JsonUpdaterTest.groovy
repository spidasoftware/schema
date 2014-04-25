package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.utils.VersionUtils
import com.spidasoftware.schema.validation.Validator
import net.sf.json.*

/**
 * Created by jeremy on 4/14/14.
 */
class JsonUpdaterTest extends GroovyTestCase {

	JsonUpdater jsonUpdater

	void setUp(){
		jsonUpdater = new JsonUpdater()
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("path/to/schema-3.jar") }
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(JsonUpdater.class);
		GroovySystem.metaClassRegistry.removeMetaClass(Validator.class);
		GroovySystem.metaClassRegistry.removeMetaClass(VersionUtils.class);
	}

	void testCurrentVersionFromJarName(){
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("a-5.1.jar") }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3]
		assert jsonUpdater.getCurrentVersion() == "5.1" // version from jar name
	}

	void testCurrentVersionFromLatestChangeSet(){
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("a") }
		jsonUpdater.availableChangeSets = [TestChangeSet3, TestChangeSet2]
		assert jsonUpdater.getCurrentVersion() == "3" // latest changeset
	}

	void testUpdateJsonObjectWithOlderVersion() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		JSONObject jsonObject = JSONObject.fromObject('{"version":"1","key1":"123"}')

		jsonUpdater.update("/test", jsonObject)
		assert jsonObject.version == "3" //change sets run and version is now at current version 3
		assert jsonObject.key3 == "123" //new key name, same value
	}

	void testUpdateJsonStringWithOlderVersion() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		String oldString = '{"version":"1","key1":"123"}'

		JSONObject newObject = JSONObject.fromObject(jsonUpdater.update("/test", oldString))
		assert newObject.version == "3" //change sets run and version is now at current version 3
		assert newObject.key3 == "123" //new key name, same value
	}

	void testUpdateWithNoChangeNeeded() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		String oldString = '{"key3":"123","version":"3"}'

		JSONObject newObject = JSONObject.fromObject(jsonUpdater.update("/test", oldString))
		assert newObject.version == "3" //no change sets run
		assert newObject.key3 == "123" //same key name, same value
	}

	void testUpdateWithValidJsonNoVersion() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		String oldString = '{"key3":"123"}'

		JSONObject newObject = JSONObject.fromObject(jsonUpdater.update("/test", oldString))
		assert newObject.version == "3" //version added
		assert newObject.key3 == "123" //change sets run but no change
	}

	void testUpdateWithNoVersionFailValidation() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> false }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		String oldString = '{"key1":"123", "extra":"x"}'

		JSONObject newObject = JSONObject.fromObject(jsonUpdater.update("/test", oldString))
		assert newObject.key3 == "123" //all change sets are run, but it still doesn't pass validation, so no version is added
		assert newObject.extra == "x" //invalid json object so no change
	}

	void testGetChangeSetsToRun() {
		jsonUpdater.metaClass.isValid = { schemaPath, jsonString -> true }
		jsonUpdater.availableChangeSets = [TestChangeSet2, TestChangeSet3, NoChangeSet]
		def jsonObject = new JSONObject()

		assert jsonUpdater.getChangeSetsThatApply(jsonObject, "/test", "1", "3")[0] instanceof TestChangeSet2
		assert jsonUpdater.getChangeSetsThatApply(jsonObject, "/test", "1", "3")[1] instanceof TestChangeSet3

		assert jsonUpdater.getChangeSetsThatApply(jsonObject, "/test", "2", "3")[0] instanceof TestChangeSet3
		assert jsonUpdater.getChangeSetsThatApply(jsonObject, "/test", "3", "3").isEmpty() //no changeset apply since it is the current version
		assert jsonUpdater.getChangeSetsThatApply(jsonObject, "/none", "2", "4").isEmpty() //no changeset apply since no schema paths match
	}

	void testInstanceOrder() {
		jsonUpdater.availableChangeSets = [NoChangeSet, TestChangeSet3, TestChangeSet2]
		assert jsonUpdater.getChangeSetInstances()[0].schemaVersion == "2"
		assert jsonUpdater.getChangeSetInstances()[1].schemaVersion == "3"
		assert jsonUpdater.getChangeSetInstances()[2].schemaVersion == "4"
	}

	class TestChangeSet2 implements ChangeSet {
		String schemaVersion = "2"
		String schemaPath = "/test"

		@Override
		void convert(JSON jsonObject) {
			if(jsonObject.containsKey("key1")){
				jsonObject.key2 = jsonObject.key1
				jsonObject.key1 = null
			}
		}
	}

	class TestChangeSet3 implements ChangeSet {
		String schemaVersion = "3"
		String schemaPath = "/test"

		@Override
		void convert(JSON jsonObject) {
			if(jsonObject.containsKey("key2")){
				jsonObject.key3 = jsonObject.key2
				jsonObject.key2 = null
			}
		}
	}

	class NoChangeSet implements ChangeSet {
		String schemaVersion = "4"
		String schemaPath = "/test"

		@Override
		void convert(JSON jsonObject) {
			//Do Nothing
		}
	}

}
