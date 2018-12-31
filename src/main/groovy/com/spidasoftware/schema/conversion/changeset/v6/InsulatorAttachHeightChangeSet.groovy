/*
 * ©2009-2018 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v6

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import groovy.util.logging.Log4j

/**
 * In the schema v6 release we started exporting detailed results alongside summary results.  We also added the resultId
 * reference to detailed results.  When down converteing if we have detailed results in the design analysis we remove them,
 * then we still have the summary results because we always export summary results.
 * We also remove the analysis if it references detailed results as it isn't valid in previous versions.
 */
@Log4j
class InsulatorAttachHeightChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map design) throws ConversionException {
		def insulatorWithCrossArmParentId = []
		design.get("structure")?.get("crossArms")?.each { crossArm ->
			if (crossArm.containsKey("insulators")) {
				insulatorWithCrossArmParentId.addAll(crossArm.get("insulators"))
			}
		}
		design.get("structure")?.get("insulators")?.each { insulator ->
			if (!insulatorWithCrossArmParentId.contains(insulator.get("id"))) {
				insulator.put("attachmentHeight", insulator.get("offset"))
				insulator.remove("offset")
			}
		}
	}

	@Override
	void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get("insulators")?.each { insulator ->
			if (insulator.containsKey("attachmentHeight")) {
				insulator.remove("attachmentHeight")
				insulator.put("offset", insulator.get("attachmentHeight"))
			}
		}
	}
}
