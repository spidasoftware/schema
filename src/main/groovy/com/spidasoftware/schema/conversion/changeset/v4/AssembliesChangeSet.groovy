/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import groovy.util.logging.Log4j


@Log4j
class AssembliesChangeSet extends AbstractDesignChangeset {

	@Override
	void applyToDesign(Map design) {
		design.remove("framingPlan")
	}

	@Override
	void revertDesign(Map design) {
		Map structure = design.get("structure")
		structure?.remove("assemblies")
		structure?.remove("assemblyPlan")
	}
}
