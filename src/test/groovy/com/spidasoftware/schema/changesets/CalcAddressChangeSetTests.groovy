package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.utils.JsonUpdater
import net.sf.json.JSONObject

class CalcAddressChangeSetTests extends GroovyTestCase {

	JsonUpdater jsonUpdater

	void setUp(){
		jsonUpdater = new JsonUpdater()
		jsonUpdater.metaClass.getJarFile = { new File("path/to/schema-0.6.jar") }
	}

	void tearDown(){
		GroovySystem.metaClassRegistry.removeMetaClass(JsonUpdater.class);
	}

	void testAddressChange(){
		def oldJsonString = """{
			  "name": "project-1",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"houseNumber":"123", "street":"a", "city":"b", "county":"c", "state":"d", "zipCode":"12345"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update("/v1/schema/spidacalc/calc/project.schema", oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert newJsonObject.version == "0.6"
		assert newJsonObject.leads[0].locations[0].address.zip_code == "12345"
		assert newJsonObject.leads[0].locations[0].address.number == "123"

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zipCode")
		assert !newJsonObject.leads[0].locations[0].address.containsKey("houseNumber")
	}

	void testAddressChangeNotNeeded(){
		def oldJsonString = """{
			  "name": "project-1",
			  "version": "0.6",
			  "clientFile": "Demo.client",
			  "schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
			  "leads": [
				{
				  "locations": [
					{
					  "address": {"number":"123", "street":"a", "city":"b", "county":"c", "state":"d", "zip_code":"12345"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update("/v1/schema/spidacalc/calc/project.schema", oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert newJsonObject.version == "0.6"
		assert newJsonObject.leads[0].locations[0].address.zip_code == "12345"
		assert newJsonObject.leads[0].locations[0].address.number == "123"

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zipCode")
		assert !newJsonObject.leads[0].locations[0].address.containsKey("houseNumber")
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
					  "address": {"houseNumber_OTHER":"123", "street":"a", "city":"b", "county":"c", "state":"d", "zipCode_OTHER":"12345"},
					  "geographicCoordinate": { "type":"Point", "coordinates":[1, 2] },
					  "id": "41811012B10002"
					}
				  ]
				}
			  ]
			}"""
		def newJsonString = jsonUpdater.update("/v1/schema/spidacalc/calc/project.schema", oldJsonString)
		def newJsonObject = JSONObject.fromObject(newJsonString)

		assert !newJsonObject.containsKey("version") //not valid so not set
		assert newJsonObject.leads[0].locations[0].address.houseNumber_OTHER == "123" //no change to key or value
		assert newJsonObject.leads[0].locations[0].address.zipCode_OTHER == "12345" //no change to key or value

		assert !newJsonObject.leads[0].locations[0].address.containsKey("zip_code") //not added because zipCode not found
		assert !newJsonObject.leads[0].locations[0].address.containsKey("number") //not added because houseNumber not found

	}

}
