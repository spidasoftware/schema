package com.spidasoftware.schema.validation

import groovy.json.JsonOutput
import org.apache.log4j.Logger

class MinSchemaTest extends GroovyTestCase {

	def log = Logger.getLogger(this.class)
	def report
	Validator validator = new Validator()

	void setUp() {
	}

	void testValidDateFields() {
		def schemaToJson = [
			"/schema/spidamin/asset/standard_details/analysis_asset.schema":'{"analysisDate":432432432}',
			"/schema/spidamin/asset/standard_details/pole_asset.schema":'{"createdDate":432432432,"dateInspected":43243245435332,"modifiedDate":4324324432432}',
			"/schema/spidamin/asset/standard_details/file_asset.schema":'{"dateCreated":432432432, "dateUpdated":43243245435332}',
		]

		def failures = []
		schemaToJson.each { schemaId, json ->
			
			report = validator.validateAndReport(schemaId, json)
			report.each{ log.info "validation report for ${schemaId} ||| ${json}"+it.toString() }
			if(!report.isSuccess()) {
				failures << "${json} did not validate against ${schemaId}"
			}
		}
		assert "None of the json should fail validation so the failures list should be empty ${failures}", failures.isEmpty()
	}

	void testInvalidDateFields() {
		def analysisDateJSON = [:]
		analysisDateJSON.put("analysisDate", new Date())
		def schemaToJson = [
			"/schema/spidamin/asset/standard_details/analysis_asset.schema":JsonOutput.toJson(analysisDateJSON.toString()),
			"/schema/spidamin/asset/standard_details/pole_asset.schema":'{"createdDate":"Thu Feb 13 12:04:50 EST 2014","dateInspected":"Wed Feb 26 13:28:19 EST 2014","modifiedDate":"Thu Feb 13 13:04:07 EST 2014"}',
			"/schema/spidamin/asset/standard_details/file_asset.schema":'{"dateCreated":"Thu Feb 13 13:04:07 EST 2014", "dateUpdated":"Thu Feb 13 12:04:50 EST 2014"}'
		]
		def successes = []
		schemaToJson.each { schemaId, json ->
			report = validator.validateAndReport(schemaId, json)
			report.each{ log.info "validation report for ${schemaId} ||| ${json}"+it.toString() }
			if(report.isSuccess()) {
				successes << "${json} did validate against ${schemaId}"
			}
		}
		assert "None of the json should have validated so the successes list should be empty ${successes}", successes.isEmpty()
	}

	void testAttachment(){
		def instance = '{"name":"blah","companyId": 42,"uuid":"blahblah", "bytes":"abc","associations":[{"level":"COMPANY","product": "SPIDA_DB","sourceId": "42", "latitude": 7, "longitude": 8}]}'
		def schema = "/schema/spidamin/asset/attachment.schema"
		report = validator.validateAndReport(schema, instance)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testProjectRequestBadStations(){
		def schema = "/schema/spidamin/project/project.schema"
		report = validator.validateAndReport(schema, """{ "strict": true, "name": "Project4", "flowId": 3359184, "draft": false, "deleted": false, "stations": [ { "other": "val" } ]	}""")
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleGetStations(){
		def schema = "/schema/spidamin/asset/stations.schema"
		report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/asset/getStations_response.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleProjectRequest(){
		def schema = "/schema/spidamin/project/project.schema"
		report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/project/createOrUpdate_request.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleProjectResponse(){
		log.info "testExampleProjectResponse()"
		def schema = "/schema/spidamin/project/projects.schema"
		report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/project/getProjects_response.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleGetLinksResponse(){
		log.info "testExampleGetLinksResponse()"
		def schema = "/schema/spidamin/project/station_links.schema"
		report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/project/getLinks_response.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleGetFlows(){
		def schema = "/schema/spidamin/project/flows.schema"
		report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/project/getFlows_response.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testExampleUser(){
		["user_response.json", "createOrUpdate_create_request.json", "createOrUpdate_update_request.json"].each {
			def schema = "/schema/spidamin/user/user.schema"
			report = validator.validateAndReport(schema, Validator.getResourceAsStream("/examples/spidamin/user/${it}").text)
			assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
		}
	}

}
