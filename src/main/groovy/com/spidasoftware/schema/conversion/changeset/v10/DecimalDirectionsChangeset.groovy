/*
 * Copyright (c) 2023 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CompileStatic
class DecimalDirectionsChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		// no need to do anything
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = false

		clientDataJSON.assemblies.each { Map assembly ->
			anyChanged |= revertStructure((Map) assembly.assemblyStructure)
		}

		return anyChanged
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		super.revertResults(resultsJSON)

		if(resultsJSON.analyzedStructure != null) {
			return revertStructure((Map) resultsJSON.analyzedStructure)
		}
		return false
	}

	/**
	 * If any item in a structure will snap to a fractional direction, set analysisCurrent to false.
	 */
	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		super.applyToDesign(designJSON)

		if(!designJSON.analysisCurrent || (designJSON.structure == null)) {
			return
		}

		Map structure = (Map) designJSON.structure
		if (structure.crossArms.any { Map crossArm -> crossarmWillMove(crossArm, structure) } ||
				structure.anchors.any { Map anchor -> anchorWillMove(anchor, structure) } ||
				structure.sidewalkBraces.any { Map brace -> braceWillMove(brace, structure) }) {
			designJSON.put("analysisCurrent", false)
		}
	}

	/**
	 * A crossarm will move if the bisector of its associated weps is fractional.
	 */
	protected boolean crossarmWillMove(Map crossarm, Map structure) {
		final supportedWEPs = (List<String>) crossarm.supportedWEPs
		if (supportedWEPs.size() != 2) {
			return false
		}
		Map wep1 = getWep(supportedWEPs[0], structure)
		Map wep2 = getWep(supportedWEPs[1], structure)
		return isBisectorFractional((int) wep1.direction, (int) wep2.direction)
	}

	/**
	 * An anchor will move if the bisector of its associated weps is fractional.
	 */
	protected boolean anchorWillMove(Map anchor, Map structure) {
		final supportedWEPs = (List<String>) anchor.supportedWEPs
		if (supportedWEPs.size() != 2) {
			return false
		}
		Map wep1 = getWep(supportedWEPs[0], structure)
		Map wep2 = getWep(supportedWEPs[1], structure)
		return isBisectorFractional((int) wep1.direction, (int) wep2.direction)
	}

	/**
	 * A brace will move if any of its connected weps will move or if the bisector of its anchor parents is fractional.
	 */
	protected boolean braceWillMove(Map brace, Map structure) {
		List<Map> guys = brace.guys.collect { String guyId -> getGuy(guyId, structure) }
		List<String> guyIds = guys.collect { (String) it.id }.unique()
		List<Map> anchors = guyIds.collect { getAnchorForGuy(it, structure) }
		anchors = anchors.unique { it.id }
		if(anchors.any { anchorWillMove(it, structure) }) {
			return true
		}
		if(anchors.size() == 2) {
			return isBisectorFractional((int) anchors[0].direction, (int) anchors[1].direction)
		}
		return false
	}

	protected Map getWep(String wepId, Map structure) {
		return ((List<Map>) structure.wireEndPoints).find { it.id == wepId }
	}

	protected Map getAnchorForGuy(String guyId, Map structure) {
		return ((List<Map>) structure.anchors).find { ((List<String>) it.guys).contains(guyId) }
	}

	protected Map getGuy(String guyId, Map structure) {
		return ((List<Map>) structure.guys).find { it.id == guyId }
	}

	protected boolean isBisectorFractional(int dir1, int dir2) {
		return ((dir1 + dir2) % 2) != 0
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		if(designJSON.structure != null) {
			revertStructure((Map) designJSON.structure)
		}
	}

	protected boolean revertStructure(Map structure) {
		boolean anyChanged = false

		if(structure.pole) {
			anyChanged |= revertDirection((Map) structure.pole, "leanDirection")
		}
		structure.anchors.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.damages.each {
			if(((Map) it).direction != null) {  // some types of damage have null directions
				anyChanged |= revertDirection((Map) it, "direction")
			}
		}
		structure.notePoints.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.sidewalkBraces.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.equipments.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.crossArms.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.insulators.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.pushBraces.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}
		structure.wireEndPoints.each {
			anyChanged |= revertDirection((Map) it, "direction")
		}

		return anyChanged
	}

	/**
	 * If map.key is a double, it is rounded to an integer.
	 * @return True if the value associated with map.key was changed.
	 */
	@CompileDynamic
	protected boolean revertDirection(Map map, String key) {
		if(map[key] == null) {
			return false
		}
		boolean isInteger = isInteger(map[key])
		map.put(key, Math.round(map[key]))
		return !isInteger
	}

	@CompileDynamic
	protected boolean isInteger(Number number) {
		if(number == null) {
			return true
		}

		if(number instanceof BigDecimal) {
			return number.remainder(1) == BigDecimal.ZERO
		}

		return number % 1 == 0
	}
}
