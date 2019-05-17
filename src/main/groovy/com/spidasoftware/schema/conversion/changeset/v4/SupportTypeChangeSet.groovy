/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset

class SupportTypeChangeSet extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map design) throws ConversionException {
		Map structure = design.get("structure") as Map
		structure?.get('anchors')?.each({ Map anchor ->
			def supportType = anchor.get('supportType')
			if (supportType) {
				anchor.remove('supportType')
				anchor.put('supportedWEPs', getSupportedWEPs(supportType, structure))
			}
		})
		structure?.get('crossArms')?.each({ Map crossArm ->
			def associatedBacking = crossArm.get('associatedBacking')
			if (associatedBacking) {
				crossArm.remove('associatedBacking')
				crossArm.put('supportedWEPs', getSupportedWEPs(associatedBacking, structure))
			}
		})
    }

    @Override
    void revertDesign(Map design) throws ConversionException {
		Map structure = design.get("structure") as Map
		structure?.get('anchors')?.each({ Map anchor ->
			anchor.put('supportType', getSupportType(anchor.get('supportedWEPs'), structure))
			anchor.remove('supportedWEPs')
		})
		structure?.get('crossArms')?.each({ Map crossArm ->
			crossArm.put('associatedBacking', getSupportType(crossArm.get('supportedWEPs'), structure))
			crossArm.remove('supportedWEPs')
		})
	}

	private String getSupportType(List<String> supportedWEPs, Map structure) {
		if (supportedWEPs) {
			if (supportedWEPs.size() > 1) {
				def wireEndPoints = structure.get('wireEndPoints')
				boolean previousAndNext = supportedWEPs.every { String supportedWepId ->
					def type = wireEndPoints.find { it.id == supportedWepId }.type
					return type in ["NEXT_POLE", "PREVIOUS_POLE"]
				}
				if (previousAndNext) {
					return "Bisector"
				} else {
					return "Other"
				}
			} else {
				return supportedWEPs.first()
			}
		} else {
			return "Other"
		}
	}

	private List getSupportedWEPs(String supportType, Map structure) {
		def wireEndPoints = structure.get('wireEndPoints')
		if (supportType != 'Other' && wireEndPoints) {
			if (supportType == 'Bisector') {
				def nextWEP = getWEPFor('NEXT_POLE', wireEndPoints)
				def previousWEP = getWEPFor('PREVIOUS_POLE', wireEndPoints)


				if (nextWEP && previousWEP) {
					return [nextWEP, previousWEP]
				}
			} else {
				return [supportType]
			}
		}

		return []
	}

	private String getWEPFor(String type, List wireEndPoints) {
		wireEndPoints.find({ Map wep -> wep.get('type') == type})?.get('id')
	}

}
