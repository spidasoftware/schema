/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc

import com.spidasoftware.schema.conversion.changeset.*

abstract class AbstractCalcDesignChangeset extends CalcProjectChangeSet {

    @Override
    void applyToProject(Map projectJSON) throws ConversionException {
        projectJSON.get("leads")?.each { Map leadJSON ->
            leadJSON.get("locations")?.each { Map locationJSON ->
                applyToLocation(locationJSON)
            }
         }
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        projectJSON.get("leads")?.each { Map leadJSON ->
            leadJSON.get("locations")?.each { Map locationJSON ->
                revertLocation(locationJSON)
             }
         }
    }

    @Override
    void applyToLocation(Map locationJSON) throws ConversionException {
        locationJSON.get("designs")?.each { Map designJSON ->
            applyToDesign(designJSON)
        }
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
        locationJSON.get("designs")?.each { Map designJSON ->
            revertDesign(designJSON)
        }
    }

}
