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
	String schemaPath = "/v1/schema/spidacalc/calc/location.schema"

	@Before
	void setUp(){
		jsonUpdater = new JsonUpdater()
		VersionUtils.metaClass.static.getSchemaJarFile = { new File("path/to/schema-0.8.jar") }
	}

	@Test
	void testChangeSet() {
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
                    "userDefinedValues": [{
                        "key": "some_other_system_id",
                        "value": "123-987"
                    },{
                        "key": "testKey",
                        "value": "testValue"
                    }],
			}"""
		def newJsonString = jsonUpdater.update(schemaPath, oldJsonString)
		def newJsonObj = JSONObject.fromObject(newJsonString)

		Assert.assertNotNull(newJsonObj.userDefinedValues)
		Assert.assertEquals("testValue", newJsonObj.userDefinedValues.testKey)

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
