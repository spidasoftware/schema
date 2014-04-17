package com.spidasoftware.schema.changesets

import net.sf.json.JSON

/**
 * changes zipCode to zip_code
 * changes houseNumber to number
 */
class CalcAddressChangeSet implements ChangeSet {

	String schemaVersion = "0.6"
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	@Override
	void convert(JSON calcProject) {
		calcProject.leads?.each { lead ->
			lead.locations?.each { loc ->
				if(loc.address?.zipCode){
					loc.address.zip_code = loc.address.zipCode
					loc.address.zipCode = null //net.sf.json lib removes the key if the value is null
				}
				if(loc.address?.houseNumber){
					loc.address.number = loc.address.houseNumber
					loc.address.houseNumber = null //net.sf.json lib removes the key if the value is null
				}
			}
		}
	}
}