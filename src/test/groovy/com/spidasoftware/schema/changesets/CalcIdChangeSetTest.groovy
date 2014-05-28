package com.spidasoftware.schema.changesets

import com.spidasoftware.schema.validation.Validator
import net.sf.json.JSONObject
import net.sf.json.JSONSerializer

/**
 * Unit Tests for CalcIdChangeSet
 * User: mford
 * Date: 5/14/14
 * Time: 4:07 PM
 * Copyright SPIDAWeb
 */
class CalcIdChangeSetTest extends GroovyTestCase {

	CalcIdChangeSet changeSet

	@Override
	void setUp() {
		changeSet = new CalcIdChangeSet()
	}

	void testReplaceProjectComponent() {

		def projectComponent = """

{
	"id": "Location#1",
	"uuid": "00c8e690-b2af-4422-b3dc-456edcd9502c"
}
"""
		JSONObject jsonObject = JSONObject.fromObject(projectComponent)

		changeSet.replaceProjectComponent(jsonObject)

		assertEquals("Id is now primary key", "00c8e690-b2af-4422-b3dc-456edcd9502c", jsonObject.id)
		assertEquals("label is display", "Location#1", jsonObject.label)
		assertNull("Should not have uuid", jsonObject.uuid)
	}

	void testReplacePoleComponent() {
		def poleComponent = """

{
	"id": "WEP#1",
	"uuid": "00c8e690-b2af-4422-b3dc-456edcd9502c",
	"owner": {
		"name": "ACME",
		"uuid": "00c8e690-b2af-4422-b3dc-456edcd9502d"
	}
}
"""
		JSONObject jsonObject = JSONObject.fromObject(poleComponent)

		changeSet.replacePoleComponent(jsonObject)

		assertEquals("Id is now primary key", "WEP#1", jsonObject.id)
		assertEquals("uuid is now externalId", "00c8e690-b2af-4422-b3dc-456edcd9502c", jsonObject.externalId)
		assertNull("Should not have uuid", jsonObject.uuid)
		assertNull("Should not have label", jsonObject.label)

		def owner = jsonObject.owner
		assertEquals("Id is now primary key", "ACME", owner.id)
		assertEquals("uuid is now externalId", "00c8e690-b2af-4422-b3dc-456edcd9502d", owner.externalId)
		assertNull("Should not have uuid", owner.uuid)
		assertNull("Should not have name", owner.name)
	}

	void testConvertProject() {
		def project = """

{
	"name": "My Awesome Project"
}
"""
		JSONObject jsonObject = JSONObject.fromObject(project)

		changeSet.replaceProject(jsonObject)

		assertEquals("Id was pulled from name", "My Awesome Project", jsonObject.id )
		assertEquals("label is also from name","My Awesome Project", jsonObject.label)
		assertNull("Should not have uuid", jsonObject.uuid)
	}

	void testIntegrationConvert() {
		def resource = getClass().getResourceAsStream("/conversions/full_projectv0.7.json")
		JSONObject oldJson = new JSONSerializer().toJSON(resource.text)

		JsonUpdater updater = new JsonUpdater()
		String schema = "/v1/schema/spidacalc/calc/project.schema"
		updater.update(schema, oldJson)

		log.info("converted: ${oldJson.toString(2)}")

		Validator validator = new Validator()
		def report = validator.validateAndReport(schema, oldJson.toString(2))
		log.info(report.toString())
		assertTrue("Should validate with current schema after update", report.success)
	}
}
