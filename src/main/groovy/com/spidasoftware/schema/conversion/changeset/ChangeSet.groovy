package com.spidasoftware.schema.conversion.changeset

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
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
	abstract void applyToProject(Map projectJSON) throws ConversionException

	/**
	 * Reverse the changes to the project json object in place
	 * @param json
	 */
	abstract void revertProject(Map projectJSON) throws ConversionException

	 /**
	 * Apply the changes to the location json object in place
	 * @param json
	 * @return
	 */
	abstract void applyToLocation(Map locationJSON) throws ConversionException

	/**
	 * Reverse the changes to the location json object in place
	 * @param json
	 */
	abstract void revertLocation(Map locationJSON) throws ConversionException

	/**
	 * Apply the changes to the design json object in place
	 * @param json
	 * @return
	 */
	abstract void applyToDesign(Map designJSON) throws ConversionException

	 /**
	 * Reverse the changes to the design json object in place
	 * @param json
	 */
	 abstract void revertDesign(Map designJSON) throws ConversionException

	public void forEachLocation(Map json, Closure closure) {
		json.get("leads")?.each { Map lead ->
			lead.get("locations")?.each(closure)
		}
	}

	public static Map duplicateAsJson(Map map){
		ObjectMapper mapper = new ObjectMapper()
		return mapper.readValue(mapper.writeValueAsString(map), Map)
	}
}
