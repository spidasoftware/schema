package com.spidasoftware.schema.changesets

import net.sf.json.JSONObject

/**
 * changes zipCode to zip_code
 * changes houseNumber to number
 */
class CalcAddressChangeSet implements ChangeSet {

	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"

	@Override
	void convert(JSONObject calcProject) {
		calcProject.leads?.each { lead ->
			lead.locations?.each { loc ->
				if(loc.address?.zipCode){
					loc.address.zip_code = loc.address.zipCode
					loc.address.zipCode = null
				}
				if(loc.address?.houseNumber){
					loc.address.number = loc.address.houseNumber
					loc.address.houseNumber = null
				}
			}
		}
	}
}