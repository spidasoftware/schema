/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class AssembliesChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(JSONObject design) {
		// TODO: Apply this after assemblies are finalized
		//	design.remove("framingPlan")
	}

	@Override
	void revertDesign(JSONObject design) {
		JSONObject structure = design.get("structure")
		structure?.remove("assemblies")
		structure?.remove("assemblyPlan")
	}
}
