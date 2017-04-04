package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class PointLoadNewtonToPoundForceChangeset extends AbstractDesignChangeset {

    static final double POUND_FORCE_PER_NEWTON = 0.224808943871D
    static final double FOOT_PER_METER = 3.280839895013123D
    @Override
    void applyToDesign(JSONObject designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(JSONObject designJSON) throws ConversionException {
        designJSON.get("structure")?.get("pointLoads")?.each { JSONObject pointLoad ->
            ["fx", "fy", "fz"].each { String f ->
                JSONObject force = pointLoad.get(f)
                if(force?.get("unit") == "NEWTON") {
                    force.put("unit", "POUND_FORCE")
                    force.put("value", force.getDouble("value")*POUND_FORCE_PER_NEWTON)
                }
            }
            ["mx", "my", "mz"].each { String m ->
                JSONObject moment = pointLoad.get(m)
                if(moment?.get("unit") == "NEWTON_METRE") {
                    moment.put("unit", "POUND_FORCE_FOOT")
                    moment.put("value", moment.getDouble("value") * POUND_FORCE_PER_NEWTON * FOOT_PER_METER)
                }
            }
        }
    }
}
