/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.util.logging.Log4j
import net.sf.json.JSONObject

@Log4j
class AssembliesChangeSet extends ChangeSet {

	@Override
	String getSchemaPath() {
		return "/schema/spidacalc/calc/project.schema"
	}

	@Override
	void apply(JSONObject json) throws ConversionException {
		// TODO: Apply this after assemblies are finalized
//		forEachLocation(json, {JSONObject location ->
//			location.designs?.each {JSONObject design ->
//				design.remove("framingPlan")
//			}
//		})
	}

	@Override
	void revert(JSONObject json) throws ConversionException {
		forEachStructure(json, { JSONObject structure ->
			structure.remove("assemblies")
			structure.remove("assemblyPlan")
		})
	}
}
