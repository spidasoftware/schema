/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v8

import com.spidasoftware.schema.conversion.FormatConverter
import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet
import groovy.transform.CompileStatic

/**
 * v7.3 removed the requirement for always having a tension group for the max distance of 4999ft
 */
class MaxTensionGroupChangeset extends CalcProjectChangeSet {

	static final Map MAX_DIST = ["METRE":1523.6952, "FOOT":4999]

	/**
	 * add in the tension group for max distance because older versions require it
	 * @param projectJSON
	 */
	void revertProject(Map projectJSON) throws ConversionException {
		projectJSON.clientData?.wires?.each { wire ->
			wire.tensionGroups.each { tg ->
				def highestDistGroup = tg.groups.max { g -> g.distance.value }
				if(highestDistGroup.distance.value < MAX_DIST[highestDistGroup.distance.unit]){
					tg.groups.add([
						distance:[unit:highestDistGroup.distance.unit, value:MAX_DIST[highestDistGroup.distance.unit]],
						tension:[unit:highestDistGroup.tension.unit, value:highestDistGroup.tension.value]
					])
				}
			}
		}
	}

	void applyToProject(Map projectJSON) throws ConversionException {}
	void applyToLocation(Map locationJSON) throws ConversionException {}
	void revertLocation(Map locationJSON) throws ConversionException {}
	void applyToDesign(Map designJSON) throws ConversionException {}
	void revertDesign(Map designJSON) throws ConversionException {}

}
