package com.spidasoftware.schema.changesets

import net.sf.json.JSON
import net.sf.json.JSONArray
import net.sf.json.JSONNull
import net.sf.json.JSONObject

/**
 * For locations, updates the structure of user defined values from array of key/values to object.
 *
 * Created by esmith on 5/27/14.
 */
class UserDefinedValueChangeSet implements ChangeSet {

	String schemaVersion = "0.8"
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	@Override
	void convert(JSON project) {
		project.leads?.each { lead ->
			lead.locations?.each { location ->
				if (location.userDefinedValues && location.userDefinedValues instanceof JSONArray) {
					JSONObject newJsonObject = new JSONObject()

					location.userDefinedValues.each {JSONObject keyValuePair ->
						newJsonObject.put(keyValuePair.get("key"), keyValuePair.get("value"))
					}

					location.put("userDefinedValues", newJsonObject)
				}
			}
		}
	}
}
