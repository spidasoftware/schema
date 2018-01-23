/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j


@Log4j
class PhotoDirectionChangeSet extends CalcProjectChangeSet {

	@Override
	void applyToProject(Map projectJSON) throws ConversionException {
		forEachLocation(projectJSON, { Map locationJSON ->
			applyToLocation(locationJSON)
		})
	}

	@Override
	void revertProject(Map projectJSON) throws ConversionException {
		forEachLocation(projectJSON, { Map locationJSON ->
			revertLocation(locationJSON)
		})
	}

	@Override
	void applyToLocation(Map locationJSON) throws ConversionException {
		locationJSON.get("images")?.each { Map imageJSON ->
			imageJSON.put('direction', 'N/A')
		}
	}

	@Override
	void revertLocation(Map locationJSON) throws ConversionException {
		locationJSON.get("images")?.each { Map imageJSON ->
			imageJSON.remove('direction')
		}
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {

	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {

	}
}
