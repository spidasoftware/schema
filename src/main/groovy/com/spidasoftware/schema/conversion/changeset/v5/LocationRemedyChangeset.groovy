package com.spidasoftware.schema.conversion.changeset.v5

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.util.logging.Log4j

@Log4j
class RemoveAdditionalPropertiesChangeset extends CalcProjectChangeSet {

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
    	// Do nothing
    }

    @Override
    void applyToLocation(Map locationJSON) throws ConversionException {
    	if(locationJSON.remedies){
    		locationJSON.remedy = []
    		locationJSON.remedies.each{
				locationJSON.remedy.add(it)
    		}
    	}
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
    	if(locationJSON.remedy){
    		locationJSON.remedies = []
    		locationJSON.remedy.each{
				locationJSON.remedies.add([description:it.description])
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
