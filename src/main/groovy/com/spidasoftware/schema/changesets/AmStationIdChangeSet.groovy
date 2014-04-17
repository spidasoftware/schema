package com.spidasoftware.schema.changesets

import net.sf.json.JSON

/**
 * changes amStationId to assetServiceRefId
 */
class AmStationIdChangeSet implements ChangeSet {

	String schemaVersion = "0.6"
	String schemaPath = "/v1/schema/spidamin/project/project.schema"

	@Override
	void convert(JSON minProject) {
		minProject.stations?.each { station ->
			if(station.amStationId){
				station.assetServiceRefId = station.amStationId
				station.amStationId = null //net.sf.json lib removes the key if the value is null
			}
		}
	}
}