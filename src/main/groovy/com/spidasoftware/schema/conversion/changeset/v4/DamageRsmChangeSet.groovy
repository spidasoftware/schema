/*
 * ©2009-2019 SPIDAWEB LLC
 */
package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.calc.AbstractCalcDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException

import java.util.Map.Entry

/**
 * This version adds wire point loads and simplifies all point load items
 */
class DamageRsmChangeSet extends AbstractCalcDesignChangeset {

	@Override
	void applyToDesign(Map designJSON) throws ConversionException {
		//do nothing
	}

	@Override
	void revertDesign(Map designJSON) throws ConversionException {
		if(designJSON.get('structure')){
			for(damage in designJSON.get("structure").damages){
				if(damage.get('remainingSectionModulus')!=null){
					damage.remove('remainingSectionModulus')
				}
			}
		}
	}
}
