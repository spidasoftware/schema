/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class LocationWepChangeSet extends ChangeSet {
	/**
	 * Get the schema this changeset applies to
	 * @return
	 */
	@Override
	String getSchemaPath() {
		"/v1/schema/spidacalc/calc/project.schema"
	}

	@Override
	void apply(JSONObject json) throws ConversionException {
		//Do nothing
	}

	/**
	 * Reverse the changes to the json object in place
	 * @param json
	 */
	@Override
	void revert(JSONObject json) throws ConversionException {
		forEachStructure(json, {def structure->
			structure?.get("wireEndPoints")?.each { wireEndPoint ->
				wireEndPoint.remove("connectionId")
			}
		})
	}
}
