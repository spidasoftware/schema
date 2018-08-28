package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException

import java.util.Map.Entry

/**
 * This version adds wire point loads and simplifies all point load items
 */
class PointLoadItemChangeSet extends AbstractCalcDesignChangeset {

	static final double POUND_FORCE_PER_NEWTON = 0.224808943871D

	final static def itemsToRemoveMap = ["elevation":"DEGREE_ANGLE", "rotation":"DEGREE_ANGLE",
														 "x":"FOOT", "y":"FOOT", "z":"FOOT",
														 "mx":"POUND_FORCE_FOOT", "my":"POUND_FORCE_FOOT", "mz":"POUND_FORCE_FOOT"]

	final static def conversionMap = ["attachmentHeight":"attachHeight",
									  "fx":"XForce", "fy":"YForce", "fz":"ZForce"]

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {

		designJSON.get("structure")?.get("pointLoads")?.each { Map pointLoad ->
			itemsToRemoveMap.keySet().each { String itemToRemove ->
				pointLoad.remove(itemToRemove)
			}
			conversionMap.keySet().each { String oldKey ->
				def newKey = conversionMap[oldKey]
				def value = pointLoad.get(oldKey)
				pointLoad.put(newKey, value)
				pointLoad.remove(oldKey)
			}
		}
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		designJSON.get("structure")?.remove("wirePointLoads")
		//these fields always had values of zero. Put them back in with appropriate unit
		designJSON.get("structure")?.get("pointLoads")?.each { Map pointLoad ->
			itemsToRemoveMap.entrySet().each { Entry<String, String> entry ->
				def measurable = [:]
				measurable.put("value", 0)
				measurable.put("unit", entry.value)
				pointLoad.put(entry.key, measurable)
			}

			conversionMap.entrySet().each { Entry<String, String> entry ->
				//this is the reverse
				def from = entry.value
				def to = entry.key
				def object = pointLoad.get(from)
				pointLoad.remove(from)

				if (object.get("unit") == "NEWTON") {
					object.put("unit", "POUND_FORCE")
					object.put("value", object.get("value") * POUND_FORCE_PER_NEWTON)
				}

				pointLoad.put(to, object)
			}
			def attachHeight = pointLoad.attachmentHeight // value name has already changed

			// oh wait we actually used to use the z value. add that back in
			pointLoad.z = [value:attachHeight.value, unit:attachHeight.unit]

		}
	}
}
