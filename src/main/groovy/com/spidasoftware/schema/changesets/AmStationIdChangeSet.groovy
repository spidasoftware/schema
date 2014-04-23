package com.spidasoftware.schema.changesets

import net.sf.json.JSON
import net.sf.json.JSONNull

/**
 * changes amStationId to assetServiceRefId
 */
class AmStationIdChangeSet implements ChangeSet {

	String schemaVersion = "0.7"
	String schemaPath = "/v1/schema/spidamin/project/project.schema"

	@Override
	void convert(JSON minProject) {
		minProject.stations?.each { station ->
			if(station.containsKey("amStationId")){
				def oldVal = station.amStationId
				station.assetServiceRefId = oldVal && !(oldVal instanceof JSONNull) ? oldVal : null
				station.amStationId = null //net.sf.json lib removes the key if the value is null
			}
		}
	}
}