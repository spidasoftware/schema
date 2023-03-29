/*
 * ©2009-2015 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.util.logging.Slf4j


@Slf4j
class AssembliesChangeSet extends AbstractCalcDesignChangeset {

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
