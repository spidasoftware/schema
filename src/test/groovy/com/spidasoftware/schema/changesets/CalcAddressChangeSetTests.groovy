package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.utils.VersionUtils
import net.sf.json.JSONObject

class CalcAddressChangeSetTests extends GroovyTestCase {

	JsonUpdater jsonUpdater
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	void setUp(){
		jsonUpdater = new JsonUpdater()
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("path/to/schema-0.7.jar") }
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(VersionUtils.class);
	}

	void testAddressChange(){
		def oldJsonString = """{
			  "id": "project-1",
			  "version": "0.5.1",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"houseNumber":"123", "zipCode":"12345", "street":"a", "city":"b", "county":"c", "state":"d"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert newJsonObject.leads[0].locations[0].address.zip_code == "12345" // key is renamed and value is unchanged
		assert newJsonObject.leads[0].locations[0].address.number == "123" // key is renamed and value is unchanged

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zipCode") // old key is removed
		assert !newJsonObject.leads[0].locations[0].address.containsKey("houseNumber") // old key is removed
		assert newJsonObject.version == "0.7" //version is changed
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAddressChangeWithNullAndEmptyStrings(){
		def oldJsonString = """{
			  "id": "project-1",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"houseNumber":"", "zipCode":null, "street":"a", "city":"b", "county":"c", "state":"d"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert !newJsonObject.leads[0].locations[0].address.containsKey("number") // key is removed because it is null or empty
		assert !newJsonObject.leads[0].locations[0].address.containsKey("zip_code") // key is removed because it is null or empty
		assert !newJsonObject.leads[0].locations[0].address.containsKey("zipCode") // old key is removed
		assert !newJsonObject.leads[0].locations[0].address.containsKey("houseNumber") // old key is removed
		assert newJsonObject.version == "0.7" //version is added
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAddressChangeNotNeeded(){
		def oldJsonString = """{
			  "id": "project-1",
			  "version": "0.7",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"number":"123", "zip_code":"12345", "street":"a", "city":"b", "county":"c", "state":"d"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert newJsonObject.leads[0].locations[0].address.zip_code == "12345" // key is renamed and value is unchanged
		assert newJsonObject.leads[0].locations[0].address.number == "123" // key is renamed and value is unchanged

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zipCode") // old key is removed
		assert !newJsonObject.leads[0].locations[0].address.containsKey("houseNumber") // old key is removed
		assert newJsonObject.version == "0.7" //version is unchanged
		assert jsonUpdater.isValid(schemaPath, newJsonString)
	}

	void testAddressChangeIgnored(){
		def oldJsonString = """{
			  "name": "project-1",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"houseNumber_OTHER":"123", "zipCode_OTHER":"12345", "street":"a", "city":"b", "county":"c", "state":"d"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert newJsonObject.leads[0].locations[0].address.houseNumber_OTHER == "123" //no change to key or value
		assert newJsonObject.leads[0].locations[0].address.zipCode_OTHER == "12345" //no change to key or value

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zip_code") //not added because zipCode not found
		assert !newJsonObject.leads[0].locations[0].address.containsKey("number") //not added because houseNumber not found
		assert !newJsonObject.containsKey("version") //not valid so not set
		assert !jsonUpdater.isValid(schemaPath, newJsonString)
	}

}
