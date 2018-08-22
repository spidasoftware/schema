package com.spidasoftware.schema.conversion.changeset.v5

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.util.logging.Log4j

@Log4j
class SummaryNoteObjectChangeset extends CalcProjectChangeSet {

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
		if(locationJSON.summaryNotes != null){
			def notes = locationJSON.summaryNotes.collect()
			locationJSON.summaryNotes.clear()
			notes.each {
				Map noteMap = [description:it]
				locationJSON.summaryNotes.add(noteMap)
			}
		}
	}

	@Override
	void revertLocation(Map locationJSON) throws ConversionException {
		if(locationJSON.summaryNotes != null){
			def notes = locationJSON.summaryNotes.collect()
			locationJSON.summaryNotes.clear()
			notes.each {
				locationJSON.summaryNotes.add(it.description)
			}
		}
	}

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		// Do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		// Do nothing
	}

}
