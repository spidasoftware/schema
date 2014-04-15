package com.spidasoftware.schema.changesets

import net.sf.json.JSONObject

/**
 * changes amStationId to assetServiceRefId
 */
class AmStationIdChangeSet implements ChangeSet {

	String schemaPath = "/v1/schema/spidamin/project/project.schema"

	@Override
	void convert(JSONObject minProject) {
		minProject.stations?.each { station ->
			if(station.amStationId){
				station.assetServiceRefId = station.amStationId
				station.amStationId = null
			}
		}
	}
}