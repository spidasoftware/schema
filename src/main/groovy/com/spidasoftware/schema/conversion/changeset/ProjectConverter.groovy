package com.spidasoftware.schema.conversion.changeset

import net.sf.json.JSONObject

class ProjectConverter extends AbstractConverter {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void applyChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.applyToProject(json)
    }

    @Override
    void revertChangeset(ChangeSet changeSet, JSONObject json) {
        changeSet.revertProject(project)
    }
}
