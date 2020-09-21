/*
 * ©2009-2020 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.calc


import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j

/**
 * To avoid confusing users with the introduction of "Advance Wires"...
 * Rename existing tension group names from...
 * Advanced > Nonlinear Stress-Strain
 * Dynamic > Elastic (Dynamic)
 * Static > Static
 */
@Log4j
@CompileStatic
class TensionGroupRenameClientDataChangeSet extends ChangeSet {

	void applyToClientData(Map clientDataJSON) throws ConversionException {
		clientDataJSON?.wires?.each { Map wire ->
			wire?.tensionGroups?.each { Map tensionGroup ->
				String tensionGroupName = tensionGroup?.name
				if (tensionGroupName.toLowerCase() == "advanced") {
					tensionGroup?.name = "Nonlinear Stress-Strain"
				} else if (tensionGroupName.toLowerCase() == "dynamic") {
					tensionGroup?.name = "Elastic (Dynamic)"
				}
			}
		}
	}

	void revertClientData(Map clientDataJSON) throws ConversionException {
		clientDataJSON?.wires?.each { Map wire ->
			wire?.tensionGroups?.each { Map tensionGroup ->
				String tensionGroupName = tensionGroup?.name
				if (tensionGroupName.toLowerCase() == "nonlinear stress-strain") {
					tensionGroup?.name = "Advanced"
				} else if (tensionGroupName.toLowerCase() == "elastic (dynamic)") {
					tensionGroup?.name = "Dynamic"
				}
			}
		}
	}
}
