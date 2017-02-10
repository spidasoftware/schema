package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class WEPInclinationChangeSet extends AbstractDesignChangeset {

    static final double DEGREES_PER_RADIAN = 57.29577951308232D
    static final String RADIAN = "RADIAN"
    static final String DEGREEE_ANGLE = "DEGREE_ANGLE"

    @Override
    void applyToDesign(JSONObject designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(JSONObject designJSON) throws ConversionException {
        designJSON.get("structure")?.get("wireEndPoints")?.each { JSONObject wireEndPoint ->
            JSONObject inclination = wireEndPoint.get("inclination")
            if (inclination.get("unit") == RADIAN) {
                double radianValue = inclination.get("value")
                double degreeAngleValue = radianValue * DEGREES_PER_RADIAN

                inclination.put("value", degreeAngleValue)
                inclination.put("unit", DEGREEE_ANGLE)
            }
        }
    }
}
