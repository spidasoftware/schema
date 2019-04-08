package com.spidasoftware.schema.conversion.changeset.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Log4j
@CompileStatic
/**
 * put something wise here
 */
abstract class AbstractClientDataChangeSet implements ChangeSet {

	abstract applyToClientData(Map projectJSON) throws ConversionException
	abstract revertClientData(Map projectJSON) throws ConversionException

	public static Map duplicateAsJson(Map map){
		ObjectMapper mapper = new ObjectMapper()
		return mapper.readValue(mapper.writeValueAsString(map), Map)
	}

}
