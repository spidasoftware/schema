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
	/***
	 * Apply the changes to the project json object in place
	 * @param json
	 * @return
	 */
	abstract void applyToProject(JSONObject projectJSON) throws ConversionException

	/**
	 * Reverse the changes to the project json object in place
	 * @param json
	 */
	abstract void revertProject(JSONObject projectJSON) throws ConversionException

	 /**
	 * Apply the changes to the location json object in place
	 * @param json
	 * @return
	 */
	abstract void applyToLocation(JSONObject locationJSON) throws ConversionException

	/**
	 * Reverse the changes to the location json object in place
	 * @param json
	 */
	abstract void revertLocation(JSONObject locationJSON) throws ConversionException

	/**
	 * Apply the changes to the design json object in place
	 * @param json
	 * @return
	 */
	abstract void applyToDesign(JSONObject designJSON) throws ConversionException

	 /**
	 * Reverse the changes to the design json object in place
	 * @param json
	 */
	 abstract void revertDesign(JSONObject designJSON) throws ConversionException

	public void forEachLocation(JSONObject json, Closure closure) {
		json.get("leads")?.each { JSONObject lead ->
			lead.get("locations")?.each(closure)
		}
	}
}
