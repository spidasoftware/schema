/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc


import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

/**
 * Convert a json object between versions
 * Should be stateless and reusable
 */
@Log4j
@CompileStatic
abstract class ClientDataChangeSet extends ChangeSet {

	/***
	 * Apply the changes to the client data json object in place
	 * @param projectJSON
	 * @return
	 */
	abstract void applyToClientData(Map clientDataJSON) throws ConversionException

	/**
	 * Reverse the changes to the client data json object in place
	 * @param projectJSON
	 */
	abstract void revertClientData(Map clientDataJSON) throws ConversionException
}
