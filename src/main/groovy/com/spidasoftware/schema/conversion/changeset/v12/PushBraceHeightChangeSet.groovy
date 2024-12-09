/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@CompileStatic
@Slf4j
class PushBraceHeightChangeSet extends AbstractResultsChangeSet {

	@Override
	void applyToDesign(Map design) throws ConversionException {
		applyToStructure(design.structure as Map)
		if (design.containsKey("analysisDetails") && ((Map)design.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)design.analysisDetails).detailedResults as Map
			applyToResults(detailedResultsJSON)
		}
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		revertStructure(design.structure as Map)
		if (design.containsKey("analysisDetails") && ((Map)design.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJSON = ((Map)design.analysisDetails).detailedResults as Map
			revertResults(detailedResultsJSON)
		}
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		return revertStructure(resultsJSON.analyzedStructure as Map)
	}

	@Override
	boolean applyToResults(Map resultsJSON) throws ConversionException {
		return applyToStructure(resultsJSON.analyzedStructure as Map)
	}

	protected boolean applyToStructure(Map structureJSON) {
		structureJSON?.get("pushBraces")?.each { Map pushBrace ->
			def measurable = [:]
			measurable.put("value", 0)
			measurable.put("unit", "METRE")
			pushBrace.put("height", measurable)
		}
		return false  // the height was already implied to be zero
	}

	protected boolean revertStructure(Map structureJSON) {
		boolean changed = false
		structureJSON?.get("pushBraces")?.each { Map pushBrace ->
			pushBrace.remove("height")
			changed = true
		}
		return changed
	}
}
