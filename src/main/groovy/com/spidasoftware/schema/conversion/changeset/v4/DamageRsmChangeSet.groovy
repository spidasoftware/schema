package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.AbstractDesignChangeset
import com.spidasoftware.schema.conversion.changeset.ConversionException

import java.util.Map.Entry

/**
 * This version adds wire point loads and simplifies all point load items
 */
class DamageRsmChangeSet extends AbstractDesignChangeset {

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
