package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


class ClientItemVersionChangeSet extends AbstractDesignChangeset {

    @Override
    void applyToDesign(Map designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(Map designJSON) throws ConversionException {
        Map structure = designJSON.get("structure")
        structure?.get("pole")?.remove("clientItemVersion")
        ["anchors",
         "wires",
         "spanGuys",
         "guys",
         "equipments",
         "crossArms",
         "insulators",
         "pushBraces",
         "sidewalkBraces",
         "foundations",
         "assemblies"].each { key ->
            structure?.get(key)*.remove("clientItemVersion")
        }
    }
}
