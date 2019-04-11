/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

class RemoveSchemaAndVersionChangeSet extends CalcProjectChangeSet {

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        forEachLocation(projectJSON, { Map locationJSON ->
            revertLocation(locationJSON)
        })
    }

    @Override
    void applyToLocation(Map locationJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
        locationJSON.remove("schema")
        locationJSON.remove("version")
        locationJSON.get("designs")?.each { Map designJSON ->
            revertDesign(designJSON)
        }
    }

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {
        // Do nothing
    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        designJSON.remove("schema")
        designJSON.remove("version")
    }
}
