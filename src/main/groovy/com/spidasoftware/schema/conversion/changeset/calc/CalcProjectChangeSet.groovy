/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Convert a json object between versions
 * Should be stateless and reusable
 */
@Slf4j
@CompileStatic
abstract class CalcProjectChangeSet extends ChangeSet {

	/***
	 * Apply the changes to the project json object in place
	 * @param projectJSON
	 * @return
	 */
	abstract void applyToProject(Map projectJSON) throws ConversionException

	/**
	 * Reverse the changes to the project json object in place
	 * @param projectJSON
	 */
	abstract void revertProject(Map projectJSON) throws ConversionException

	/**
	 * Apply the changes to the location json object in place
	 * @param locationJSON
	 * @return
	 */
	abstract void applyToLocation(Map locationJSON) throws ConversionException

	/**
	 * Reverse the changes to the location json object in place
	 * @param locationJSON
	 */
	abstract void revertLocation(Map locationJSON) throws ConversionException

	/**
	 * Apply the changes to the design json object in place
	 * @param designJSON
	 * @return
	 */
	abstract void applyToDesign(Map designJSON) throws ConversionException

	/**
	 * Reverse the changes to the design json object in place
	 * @param designJSON
	 */
	abstract void revertDesign(Map designJSON) throws ConversionException

	public void forEachLocation(Map json, Closure closure) {
		json.get("leads")?.each { Map lead ->
			lead.get("locations")?.each(closure)
		}
	}

}
