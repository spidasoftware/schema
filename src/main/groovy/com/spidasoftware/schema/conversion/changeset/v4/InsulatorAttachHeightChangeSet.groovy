/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException


class InsulatorAttachHeightChangeSet extends AbstractCalcDesignChangeset {

    @Override
    void applyToDesign(Map design) throws ConversionException {
		design.get("structure")?.get('insulators')?.each { Map insulator ->
			def attachmentHeight = insulator.get('attachmentHeight')
			if (attachmentHeight) {
				insulator.remove('attachmentHeight')
			}
		}
    }

    @Override
    void revertDesign(Map design) throws ConversionException {
		design.get("structure")?.get('insulators')?.each{ Map insulator ->
			def offset = insulator.get('offset')
			def attachmentHeight = insulator.get('attachmentHeight')
			if (offset && !attachmentHeight) {
				insulator.put('attachmentHeight', insulator.get('offset'))
			}
		}
	}

}
