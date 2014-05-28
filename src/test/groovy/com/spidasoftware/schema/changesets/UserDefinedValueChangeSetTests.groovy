package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.utils.VersionUtils
import junit.framework.Assert
import net.sf.json.JSONObject
import org.junit.Before
import org.junit.Test

/**
 * Created by esmith on 5/27/14.
 */
class UserDefinedValueChangeSetTests {


	JsonUpdater jsonUpdater
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	@Before
	void setUp(){
		jsonUpdater = new JsonUpdater()
	}

	@Test
	void testChangeSet() {
		def oldJsonString = """

			{
    			"schema": "https://raw.github.com/spidasoftware/schema/master/resources/v1/schema/spidacalc/calc/project.schema",
				"version": "0.7",
				"name": "project-1",
				"clientFile": "Demo.client",
				"date": "2011-02-21",
				"leads": [
					{
						"id": "Lead",
						"uuid": "624002fa-4ce2-4636-9625-a8b2823e9a34",
						"locations": [{
							"id": "41811012B10001",
							"uuid": "78997104-905a-4236-aa13-1765deed5321",
							"mapNumber": "",
							"comments": "",
							"address": {
								"number": "9018",
								"street": "Sandyville Rd NE",
								"city": "Mineral City",
								"county": "Tuscarawas",
								"state": "OH",
								"zip_code": "44656"
							},
							"geographicCoordinate": {
								"type":"Point",
								"coordinates":[-82.876, 40.683]
							},
							"remedies": [{"description": "Remedy"}],
							"poleTags": [{"type":"FIELD", "value":"poleTag"}],
							"userDefinedValues": [{
								"key": "some_other_system_id",
								"value": "123-987"
							},{
								"key": "testKey",
								"value": "testValue"
							}]
						}]
					}
				]
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObj = JSONObject.fromObject(newJsonString)

		Assert.assertNotNull(newJsonObj.leads[0].locations[0].userDefinedValues)
		Assert.assertEquals("testValue", newJsonObj.leads[0].locations[0].userDefinedValues.testKey)

	}

	@Test
	void testChangeSetNoChange() {
		def oldJsonString = """
			{
                    "id": "41811012B10001",
                    "uuid": "78997104-905a-4236-aa13-1765deed5321",
                    "mapNumber": "",
                    "comments": "",
                    "address": {
                        "number": "9018",
                        "street": "Sandyville Rd NE",
                        "city": "Mineral City",
                        "county": "Tuscarawas",
                        "state": "OH",
                        "zip_code": "44656"
                    },
                    "geographicCoordinate": {
                        "type":"Point",
                        "coordinates":[-82.876, 40.683]
                    },
                    "remedies": [{"description": "Remedy"}],
                    "poleTags": [{"type":"FIELD", "value":"poleTag"}],
                    "userDefinedValues": {
                        "testKey": "testValue"
                    },
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObj = JSONObject.fromObject(newJsonString)

		Assert.assertNotNull(newJsonObj.userDefinedValues)
		Assert.assertEquals("testValue", newJsonObj.userDefinedValues.testKey)

	}
}
