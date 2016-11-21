/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class PhotoDirectionChangeSet extends ChangeSet {
	/**
	 * Get the schema this changeset applies to
	 * @return
	 */
	@Override
	String getSchemaPath() {
		"/v1/schema/spidacalc/calc/project.schema"
	}

	/**
	 * Apply the changes to the json object in place
	 * @param json
	 * @return
	 */
	@Override
	void apply(JSONObject json) throws ConversionException {
		forEachImage(json, { image -> image.put('direction', 'N/A') })
	}

	/**
	 * Reverse the changes to the json object in place
	 * @param json
	 */
	@Override
	void revert(JSONObject json) throws ConversionException {
		forEachImage(json, { image -> image.remove('direction') })
	}
}
