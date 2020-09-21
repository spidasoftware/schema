/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset

/**
 * To avoid confusing users with the introduction of "Advance Wires"...
 * Rename existing tension group names from...
 * Advanced > Nonlinear Stress-Strain
 * Dynamic > Elastic (Dynamic)
 * Static > Static
 */
class TensionGroupRenameDesignChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		designJSON.structure?.wires?.each { Map wire ->
			String tensionGroupName = wire?.tensionGroup
			if (tensionGroupName.toLowerCase() == "advanced") {
				wire?.tensionGroup = "Nonlinear Stress-Strain"
			} else if (tensionGroupName.toLowerCase() == "dynamic") {
				wire?.tensionGroup = "Elastic (Dynamic)"
			}
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.structure?.wires?.each { Map wire ->
			String tensionGroupName = wire?.tensionGroup
			if (tensionGroupName.toLowerCase() == "nonlinear stress-strain") {
				wire?.tensionGroup = "Advanced"
			} else if (tensionGroupName.toLowerCase() == "elastic (dynamic)") {
				wire?.tensionGroup = "Dynamic"
			}
		}
	}
}
