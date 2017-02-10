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

	@Override
	void applyToProject(JSONObject projectJSON) throws ConversionException {
		forEachLocation(projectJSON, { JSONObject locationJSON ->
			applyToLocation(locationJSON)
		})
	}

	@Override
	void revertProject(JSONObject projectJSON) throws ConversionException {
		forEachLocation(projectJSON, { JSONObject locationJSON ->
			revertLocation(locationJSON)
		})
	}

	@Override
	void applyToLocation(JSONObject locationJSON) throws ConversionException {
		locationJSON.get("images")?.each { JSONObject imageJSON ->
			imageJSON.put('direction', 'N/A')
		}
	}

	@Override
	void revertLocation(JSONObject locationJSON) throws ConversionException {
		locationJSON.get("images")?.each { JSONObject imageJSON ->
			imageJSON.remove('direction')
		}
	}

	@Override
	void applyToDesign(JSONObject designJSON) throws ConversionException {

	}

	@Override
	void revertDesign(JSONObject designJSON) throws ConversionException {

	}
}
