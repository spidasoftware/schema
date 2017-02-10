package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class PointLoadElevationAndRotationChangeSet extends AbstractDesignChangeset {

    static final double DEGREES_PER_RADIAN = 57.29577951308232D
    static final String RADIAN = "RADIAN"
    static final String DEGREEE_ANGLE = "DEGREE_ANGLE"


    @Override
    void applyToDesign(JSONObject designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(JSONObject designJSON) throws ConversionException {
        designJSON.get("structure")?.get("pointLoads")?.each { JSONObject pointLoad ->
            ["elevation", "rotation"].each { String key ->
                JSONObject measureable = pointLoad.get(key)
                if (measureable?.get("unit") == RADIAN) {
                    double radianValue = measureable.get("value")
                    double degreeAngleValue = radianValue * DEGREES_PER_RADIAN

                    measureable.put("value", degreeAngleValue)
                    measureable.put("unit", DEGREEE_ANGLE)
                }
            }
        }
    }
}
