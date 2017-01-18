package com.spidasoftware.schema.conversion.changeset

import net.sf.json.JSONObject

class LocationConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/location.schema"
    }

    @Override
    void applyChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.applyToLocation(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.revertLocation(json)
    }
}
