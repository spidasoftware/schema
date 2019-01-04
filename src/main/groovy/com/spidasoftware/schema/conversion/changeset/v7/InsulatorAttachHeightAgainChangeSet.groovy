/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v7

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.util.logging.Log4j

/**
 * In the schema v7 we added attach height to insulators as a class variable. In addition we made schema,
 * require an insulator to have either an offset or attach height. If a insulator is attached to a crossArm,
 * then the offset has a value and the attach height is clamped to the crossArm. If the insulator is connected
 * to a pole, then the offset is null and the attach height should have a value.
 *
 */
@Log4j
class InsulatorAttachHeightAgainChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map design) throws ConversionException {
		def insulatorIdsWithCrossArmParent = []
		design.get("structure")?.get("crossArms")?.each { crossArm ->
			if (crossArm.containsKey("insulators")) {
				insulatorIdsWithCrossArmParent.addAll(crossArm.get("insulators"))
				insulatorIdsWithCrossArmParent.findAll()
			}
		}
		design.get("structure")?.get("insulators")?.findAll{!insulatorIdsWithCrossArmParent.contains(it.get("id"))}?.each { insulator ->
			if (insulator.containsKey("offset")) {
				insulator.put("attachmentHeight", insulator.get("offset"))
				insulator.remove("offset")
			}
		}
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("insulators")?.each { insulator ->
			if (insulator.containsKey("attachmentHeight")) {
				insulator.put("offset", insulator.get("attachmentHeight"))
				insulator.remove("attachmentHeight")
			}
		}
	}
}
