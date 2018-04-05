package com.spidasoftware.schema.conversion.changeset.v5

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.util.logging.Log4j

@Log4j
class LocationRemedyChangeset extends CalcProjectChangeSet {

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
		if(locationJSON.remedies != null){
			locationJSON.remedy = [statements:[]]
			locationJSON.remedies.each { Map oldRemedyObj ->
				if(oldRemedyObj && oldRemedyObj.description){
					locationJSON.remedy.statements.add(oldRemedyObj)
				}
			}
			locationJSON.remove("remedies")
		}
	}

	@Override
	void revertLocation(Map locationJSON) throws ConversionException {
		if(locationJSON.remedy != null){
			locationJSON.remedies = []
			if(locationJSON.remedy.statements != null){
				locationJSON.remedy.statements.each { Map stmtObj ->
					if(stmtObj && stmtObj.description){
						locationJSON.remedies.add([description:stmtObj.description])
					}
				}
			}
			locationJSON.remove("remedy")
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
