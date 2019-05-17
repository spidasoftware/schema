/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.client

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

@Log4j
@CompileStatic
/**
 * Convert a client data json object between versions. Should be forwards and backwards compatible.
 */
abstract class AbstractClientDataChangeSet extends ChangeSet {

	abstract applyToClientData(Map projectJSON) throws ConversionException
	abstract revertClientData(Map projectJSON) throws ConversionException

}
