package com.spidasoftware.schema.conversion.changeset.v10

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet

class DecimalDirectionsChangeset extends AbstractClientDataChangeSet {

	@Override
	boolean applyToClientData(Map clientDataJSON) throws ConversionException {
		// no need to do anything
		return false
	}

	@Override
	boolean revertClientData(Map clientDataJSON) throws ConversionException {
		boolean anyChanged = false

		clientDataJSON.assemblies.each {
			anyChanged |= revertStructure(it.assemblyStructure)
		}

		return anyChanged
	}

	@Override
	boolean revertResults(Map resultsJSON) throws ConversionException {
		super.revertResults(resultsJSON)

		if(resultsJSON.analyzedStructure != null) {
			return revertStructure(resultsJSON.analyzedStructure)
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

		Map structure = designJSON.structure
		if(structure.crossArms.any { crossarmWillMove(it, structure) } ||
				structure.anchors.any { anchorWillMove(it, structure) } ||
				structure.sidewalkBraces.any { braceWillMove(it, structure) }) {
			designJSON.analysisCurrent = false
		}
	}

	/**
	 * A crossarm will move if the bisector of its associated weps is fractional.
	 */
	protected boolean crossarmWillMove(Map crossarm, Map structure) {
		if(crossarm.supportedWEPs.size() != 2) {
			return false
		}
		Map wep1 = getWep(crossarm.supportedWEPs[0], structure)
		Map wep2 = getWep(crossarm.supportedWEPs[1], structure)
		return isBisectorFractional(wep1.direction, wep2.direction)
	}

	/**
	 * An anchor will move if the bisector of its associated weps is fractional.
	 */
	protected boolean anchorWillMove(Map anchor, Map structure) {
		if(anchor.supportedWEPs.size() != 2) {
			return false
		}
		Map wep1 = getWep(anchor.supportedWEPs[0], structure)
		Map wep2 = getWep(anchor.supportedWEPs[1], structure)
		return isBisectorFractional(wep1.direction, wep2.direction)
	}

	/**
	 * A brace will move if any of its connected weps will move or if the bisector of its anchor parents is fractional.
	 */
	protected boolean braceWillMove(Map brace, Map structure) {
		List<Map> guys = brace.guys.collect { getGuy(it, structure) }
		List<String> guyIds = guys*.id.unique()
		List<Map> anchors = guyIds.collect { getAnchorForGuy(it, structure) }
		anchors = anchors.unique { it.id }
		if(anchors.any { anchorWillMove(it, structure) }) {
			return true
		}
		if(anchors.size() == 2) {
			return isBisectorFractional(anchors[0].direction, anchors[1].direction)
		}
		return false
	}

	protected Map getWep(String wepId, Map structure) {
		return structure.wireEndPoints.find { it.id == wepId }
	}

	protected Map getAnchorForGuy(String guyId, Map structure) {
		return structure.anchors.find { it.guys.contains(guyId) }
	}

	protected Map getGuy(String guyId, Map structure) {
		return structure.guys.find { it.id == guyId }
	}

	protected boolean isBisectorFractional(int dir1, int dir2) {
		return ((dir1 + dir2) % 2) != 0
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		super.revertDesign(designJSON)

		if(designJSON.structure != null) {
			revertStructure(designJSON.structure)
		}
	}

	protected boolean revertStructure(Map structure) {
		boolean anyChanged = false

		if(structure.pole) {
			anyChanged |= revertDirection(structure.pole, "leanDirection")
		}
		structure.anchors.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.damages.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.notePoints.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.sidewalkBraces.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.equipments.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.crossArms.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.insulators.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.pushBraces.each {
			anyChanged |= revertDirection(it, "direction")
		}
		structure.wireEndPoints.each {
			anyChanged |= revertDirection(it, "direction")
		}

		return anyChanged
	}

	/**
	 * If map.key is a double, it is rounded to an integer.
	 * @return True if the value associated with map.key was changed.
	 */
	protected boolean revertDirection(Map map, String key) {
		if(!isInteger(map[key])) {
			map.put(key, Math.round(map[key]))
			return true
		}
		return false
	}

	protected boolean isInteger(Number number) {
		if(number instanceof BigDecimal) {
			return number.remainder(1) == BigDecimal.ZERO
		}
		return number % 1 == 0
	}
}
