package com.spidasoftware.schema.changesets

import net.sf.json.JSON
import net.sf.json.JSONNull

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
				["zipCode":"zip_code", "houseNumber":"number"].each{ oldKey, newKey ->
					if(loc.address?.containsKey(oldKey)){
						def oldVal = loc.address.get(oldKey)
						loc.address.put(newKey, oldVal && !(oldVal instanceof JSONNull) ? oldVal : null)
						loc.address.put(oldKey, null) //net.sf.json lib removes the key if the value is null
					}
				}
			}
		}
	}
}