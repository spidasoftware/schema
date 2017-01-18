package com.spidasoftware.schema.conversion.changeset

import net.sf.json.JSONObject

abstract class AbstractDesignChangeset extends ChangeSet {

    @Override
    void applyToProject(JSONObject projectJSON) throws ConversionException {
        projectJSON.get("leads")?.each { JSONObject leadJSON ->
            leadJSON.get("locations")?.each { JSONObject locationJSON ->
                applyToLocation(locationJSON)
            }
         }
    }

    @Override
    void revertProject(JSONObject projectJSON) throws ConversionException {
        projectJSON.get("leads")?.each { JSONObject leadJSON ->
            leadJSON.get("locations")?.each { JSONObject locationJSON ->
                revertLocation(locationJSON)
             }
         }
    }

    @Override
    void applyToLocation(JSONObject locationJSON) throws ConversionException {
        locationJSON.get("designs")?.each { JSONObject designJSON ->
            applyToDesign(designJSON)
        }
    }

    @Override
    void revertLocation(JSONObject locationJSON) throws ConversionException {
        locationJSON.get("designs")?.each { JSONObject designJSON ->
            revertDesign(designJSON)
        }
    }
}
