package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class MapLocationChangeSet extends ChangeSet {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void apply(JSONObject json) throws ConversionException {
    }

    @Override
    void revert(JSONObject json) throws ConversionException {
        forEachLocation(json, { JSONObject location ->
            location.get("designs")?.each { JSONObject design ->
                design.remove("mapLocation")
            }
        })
    }
}
