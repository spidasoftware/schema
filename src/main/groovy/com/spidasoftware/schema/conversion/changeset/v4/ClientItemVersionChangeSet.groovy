package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class ClientItemVersionChangeSet extends AbstractDesignChangeset {

    @Override
    void applyToDesign(JSONObject designJSON) throws ConversionException {

    }

    @Override
    void revertDesign(JSONObject designJSON) throws ConversionException {
        JSONObject structure = designJSON.get("structure")
        structure?.getJSONObject("pole")?.remove("clientItemVersion")
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
            structure?.getJSONArray(key)*.remove("clientItemVersion")
        }
    }
}
