/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v12

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractResultsChangeSet

class PushBraceHeightChangeSet extends AbstractResultsChangeSet {

	@Override
	void applyToDesign(Map design) throws ConversionException {
		design.get("structure")?.get("pushBraces")?.each { Map pushBrace ->
			def measurable = [:]
			measurable.put("value", 0)
			measurable.put("unit", "METRE")
			pushBrace.put("height", measurable)
		}
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("pushBraces")?.each { Map pushBrace ->
			def height = pushBrace.get("height")
			if (height) {
				pushBrace.remove("height")
			}
		}
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		resultsJSON.get("analyzedStructure")?.get("pushBraces")?.each { Map pushBrace ->
			pushBrace.remove("height")
		}
		return !resultsJSON.analyzedStructure?.get("pushBraces")?.last()?.height
	}

	@Override
	boolean applyToResults(Map resultsJSON) throws ConversionException {
		applyToStructure(resultsJSON)
		if (resultsJSON.containsKey("analysisDetails") && ((Map)resultsJSON.analysisDetails).containsKey("detailedResults")) {
			Map detailedResultsJson = ((Map)resultsJSON.analysisDetails).detailedResults as Map
			return applyToStructure(detailedResultsJson)
		}
		return false
	}

	protected boolean applyToStructure(Map structureJSON) {
		structureJSON.get("analyzedStructure")?.get("pushBraces")?.each { Map pushBrace ->
			def measurable = [:]
			measurable.put("value", 0)
			measurable.put("unit", "METRE")
			pushBrace.put("height", measurable)
		}
		List pushBraces = structureJSON.analyzedStructure?.get("pushBraces")
		return pushBraces ? pushBraces.last()?.height : false
	}
}
