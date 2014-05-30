package com.spidasoftware.schema.changesets

import groovy.util.logging.Log4j
import net.sf.json.JSON
import net.sf.json.JSONArray
import net.sf.json.JSONObject

/**
 * Does the conversion from the 4.4.2 version of calc project to the current
 * We didn't have changesets at that point, so this just tries to do everything from
 * the old version at once.
 * User: mford
 * Date: 5/22/14
 * Time: 8:28 AM
 * Copyright SPIDAWeb
 */
@Log4j
class GPSAndStructureChangeSet implements ChangeSet {

	String schemaVersion = "0.7"
	String schemaPath = "/v1/schema/spidacalc/calc/project.schema"


	@Override
	void convert(JSON jsonObject) {
		jsonObject.leads?.each {lead ->
			lead.locations?.each { location ->
				if (location.geographicCoordinate) {
					convertGeoCoordinate(location.geographicCoordinate)
				}
				location.designs?.each {design ->
					convertStructure(design)
				}
			}
		}
		log.trace(jsonObject.toString(2))
	}

	void convertGeoCoordinate(JSONObject geoCoordinate) {
		def lat = geoCoordinate.latitude
		def lon = geoCoordinate.longitude
		if (lat && lon) {
			geoCoordinate.type = "Point"
			geoCoordinate.coordinates = new JSONArray()
			geoCoordinate.coordinates[0] = lon
			geoCoordinate.coordinates[1] = lat
		}
		geoCoordinate.remove("latitude")
		geoCoordinate.remove("longitude")
	}

	void convertStructure(JSONObject design) {
		if (design.design) {
			design.structure = design.design
			design.remove("design")
		}
	}


}
