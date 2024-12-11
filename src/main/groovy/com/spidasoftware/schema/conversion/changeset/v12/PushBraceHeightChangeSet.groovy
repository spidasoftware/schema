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
		// do nothing
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
		// do nothing
		return false
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
