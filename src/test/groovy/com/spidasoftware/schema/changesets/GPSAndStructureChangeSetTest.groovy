package com.spidasoftware.schema.changesets
import com.spidasoftware.schema.validation.Validator
import groovy.util.logging.Log4j
import net.sf.json.JSONObject
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

@Log4j
public class GPSAndStructureChangeSetTest {

	GPSAndStructureChangeSet changeSet

	@Before
	public void setUp() throws Exception {
		changeSet = new GPSAndStructureChangeSet()
	}

	@Test
	public void testConvertLocationGPS() throws Exception {

		def geoJson = new JSONObject()
		geoJson.put("latitude", 1)
		geoJson.put("longitude", 2)
		changeSet.convertGeoCoordinate(geoJson)

		assertNull("Should have removed latitude", geoJson.latitude)
		assertNull("Should have removed longitude", geoJson.longitude)


		def report = new Validator().validateAndReport("/v1/schema/general/geometry.schema", geoJson.toString())

		assertEquals("should have copied lat", 1, geoJson.coordinates[1])
		assertEquals("should have copied long", 2, geoJson.coordinates[0])

		log.debug("After:" + geoJson.toString(2))
		assertNull(geoJson.latitude)

		assertTrue("Should succeed", report.isSuccess())
	}

	@Test
	public void testConvertStructure() throws Exception {
		def json = new JSONObject()
		json.put("design", new JSONObject())
		json.design.put("pole", new JSONObject())
		changeSet.convertStructure(json)

		assertNull("Should not have a design", json.design)
		assertNotNull("Should have a structure", json.structure)
		assertNotNull("Should have the contents of the old design", json.structure.pole)
	}

	@Test
	public void testIntegration() throws Exception {

		def json = getClass().getResourceAsStream("/conversions/minimal_project_with_gps_4_4_2_schema.json").text
		log.debug("Before: " + json)
		def schema = "/v1/schema/spidacalc/calc/project.schema"
		def updated = new JsonUpdater().update(schema, json)

		log.debug("After: " + updated)

		def report  = new Validator().validateAndReport(schema, updated)
		assertTrue("Should be valid: ${report.toString()}", report.isSuccess())
	}
}