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

	static final float MAX_DIST_METERS = 1523.6952

	/**
	 * add in the tension group for max distance because older versions require it
	 * @param projectJSON
	 */
	void revertProject(Map projectJSON) throws ConversionException {
		projectJSON.clientData?.wires?.each { wire ->
			wire.tensionGroups.each { tg ->
				def highestDistGroup = tg.groups.max { g -> g.distance.value }
				if(highestDistGroup.distance.value < MAX_DIST_METERS){
					tg.groups.add([
						distance:[unit:"METRE", value:MAX_DIST_METERS],
						tension:[unit:"NEWTON", value:highestDistGroup.tension.value]
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
