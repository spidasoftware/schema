package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class MapLocationChangeSet extends AbstractDesignChangeset {

    @Override
    void applyToDesign(JSONObject json) throws ConversionException {
    }

    @Override
    void revertDesign(JSONObject design) throws ConversionException {
        design.remove("mapLocation")
    }
}
