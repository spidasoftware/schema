package com.spidasoftware.schema.conversion.changeset

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
/**
 * Convert a json object between versions
 * Should be stateless and reusable
 */
@Log4j
@CompileStatic
abstract class ChangeSet {

	/**
	 * Get the schema this changeset applies to
	 * @return
	 */
	abstract String getSchemaPath()

	/**
	 * Apply the changes to the json object in place
	 * @param json
	 * @return
	 */
	abstract void apply(JSONObject json) throws ConversionException

	/**
	 * Reverse the changes to the json object in place
	 * @param json
	 */
	abstract void revert(JSONObject json) throws ConversionException

	public void forEachLocation(JSONObject json, Closure closure) {
		json.get("leads")?.each { JSONObject lead ->
			lead.get("locations")?.each(closure)
		}
	}

	public void forEachImage(JSONObject json, Closure closure) {
		forEachLocation(json, { JSONObject location -> location.get('images')?.each(closure) })
	}

	public void forEachStructure(JSONObject json, Closure closure){
		forEachLocation(json, { JSONObject location ->
			location.get("designs")?.each { JSONObject design ->
				def structure = design.get("structure")
				closure(structure)
			}
		})
	}

}
