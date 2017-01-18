package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject
import net.sf.json.JSONArray

class SupportTypeChangeSet extends AbstractDesignChangeset {

    @Override
    void applyToDesign(JSONObject design) throws ConversionException {
		JSONObject structure = design.get("structure")
		structure?.get('anchors')?.each({ JSONObject anchor ->
			def supportType = anchor.get('supportType')
			if (supportType) {
				anchor.remove('supportType')
				anchor.put('supportedWEPs', getSupportedWEPs(supportType, structure))
			}
		})
		structure?.get('crossArms')?.each({ JSONObject crossArm ->
			def associatedBacking = crossArm.get('associatedBacking')
			if (associatedBacking) {
				crossArm.remove('associatedBacking')
				crossArm.put('supportedWEPs', getSupportedWEPs(associatedBacking, structure))
			}
		})
    }

    @Override
    void revertDesign(JSONObject design) throws ConversionException {
		JSONObject structure = design.get("structure")
		structure?.get('anchors')?.each({ JSONObject anchor ->
			anchor.put('supportType', getSupportType(anchor.get('supportedWEPs')))
			anchor.remove('supportedWEPs')
		})
		structure?.get('crossArms')?.each({ JSONObject crossArm ->
			crossArm.put('associatedBacking', getSupportType(crossArm.get('supportedWEPs')))
			crossArm.remove('supportedWEPs')
		})
	}

	private String getSupportType(JSONArray supportedWEPs) {
		if (supportedWEPs) {
			if (supportedWEPs.size() > 1) {
				return 'Bisector'
			} else {
				return supportedWEPs.first()
			}
		} else {
			return 'Other'
		}
	}

	private JSONArray getSupportedWEPs(String supportType, JSONObject structure) {
		def wireEndPoints = structure.get('wireEndPoints')
		if (supportType != 'Other' && wireEndPoints) {
			if (supportType == 'Bisector') {
				def nextWEP = getWEPFor('NEXT_POLE', wireEndPoints)
				def previousWEP = getWEPFor('PREVIOUS_POLE', wireEndPoints)


				if (nextWEP && previousWEP) {
					return JSONArray.fromObject([nextWEP, previousWEP])
				}
			} else {
				return JSONArray.fromObject([supportType])
			}
		}

		return new JSONArray()
	}

	private String getWEPFor(String type, JSONArray wireEndPoints) {
		wireEndPoints.find({ JSONObject wep -> wep.get('type') == type})?.get('id')
	}

}
