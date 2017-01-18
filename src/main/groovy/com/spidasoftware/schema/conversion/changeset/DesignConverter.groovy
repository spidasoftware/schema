package com.spidasoftware.schema.conversion.changeset

import net.sf.json.JSONObject

class DesignConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/design.schema"
    }

    @Override
    void applyChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.applyToDesign(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.revertDesign(json)
    }
}

