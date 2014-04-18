package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.utils.VersionUtils
import net.sf.json.JSONObject

class AmStationIdChangeSetTests extends GroovyTestCase {

	JsonUpdater jsonUpdater
	String schemaPath = "/v1/schema/spidamin/project/project.schema"

	void setUp(){
		jsonUpdater = new JsonUpdater()
		VersionUtils.metaClass.static.getJarFile = { new File("path/to/schema-0.6.jar") }
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(VersionUtils.class);
	}

	void testAmStationIdChange(){
		def oldJsonString = """{
		  "name": "Project4",
		  "flowId": 1,
		  "version": "0.5.1",
		  "draft": false,
		  "stations": [{
			  "deleted": false,
			  "spotted": false,
			  "amStationId": "12345",
			  "geometry": { "type": "Point", "coordinates": [ -1, 2 ] }
			}]
		}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)
		assert newJsonObject.version == "0.6" //version is changed
		assert newJsonObject.stations[0].assetServiceRefId == "12345" //key is changed but value is the same
		assert !newJsonObject.stations[0].containsKey("amStationId") // old key is removed
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAmStationIdChangeWhenNull(){
		def oldJsonString = """{
		  "name": "Project4",
		  "flowId": 1,
		  "draft": false,
		  "stations": [{
			  "deleted": false,
			  "spotted": true,
			  "amStationId": null,
			  "geometry": { "type": "Point", "coordinates": [ -1, 2 ] }
			}]
		}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)
		assert newJsonObject.version == "0.6" //version is added
		assert !newJsonObject.stations[0].containsKey("assetServiceRefId") // null so key is removed
		assert !newJsonObject.stations[0].containsKey("amStationId") // old key is removed
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAmStationIdChangeNotNeeded(){
		def oldJsonString = """{
		  "name": "Project4",
		  "version": "0.6",
		  "flowId": 1,
		  "draft": false,
		  "stations": [{
			  "deleted": false,
			  "spotted": false,
			  "assetServiceRefId": "12345",
			  "geometry": { "type": "Point", "coordinates": [ -1, 2 ] }
			}]
		}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)
		assert newJsonObject.version == "0.6" //version is unchanged
		assert newJsonObject.stations[0].assetServiceRefId == "12345" //no change to key or value
		assert !newJsonObject.stations[0].containsKey("amStationId") // old key is removed
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAmStationIdChangeIgnored(){
		def oldJsonString = """{
		  "name": "Project4",
		  "flowId": 1,
		  "draft": false,
		  "stations": [{
			  "deleted": false,
			  "spotted": false,
			  "amStationId_OTHER": "12345",
			  "geometry": { "type": "Point", "coordinates": [ -1, 2 ] }
			}]
		}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert !newJsonObject.containsKey("version") //not valid so not set
		assert newJsonObject.stations[0].amStationId_OTHER == "12345" //no change to key or value
		assert !newJsonObject.stations[0].containsKey("assetServiceRefId") //not added because amStationId not found
		assert !jsonUpdater.isValid(schemaPath, newJsonString)
	}

}
