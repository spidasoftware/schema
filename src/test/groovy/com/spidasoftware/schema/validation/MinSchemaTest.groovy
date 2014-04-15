package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.uri.*
import net.sf.json.JSONObject
import org.apache.log4j.Logger;

class MinSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	def fileUri = new File("resources").toURI().toString()
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(fileUri).freeze();
	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();

	void setUp() {
	}

	void testValidDateFields() {
		def schemaToJson = [
			"v1/schema/spidamin/asset/standard_details/analysis_asset.schema":'{"analysisDate":432432432}',
			"v1/schema/spidamin/asset/standard_details/pole_asset.schema":'{"createdDate":432432432,"dateInspected":43243245435332,"modifiedDate":4324324432432}',
			"v1/schema/spidamin/asset/standard_details/file_asset.schema":'{"dateCreated":432432432, "dateUpdated":43243245435332}',	
		]

		def failures = []
		schemaToJson.each { schemaId, json ->
			def schema = factory.getJsonSchema(schemaId)
			report = schema.validate(JsonLoader.fromString(json))
			report.each{ log.info "validation report for ${schemaId} ||| ${json}"+it.toString() }
			if(!report.isSuccess()) {
				failures << "${json} did not validate against ${schemaId}"
			}
		}
		assert "None of the json should fail validation so the failures list should be empty ${failures}", failures.isEmpty()
	}

	void testInvalidDateFields() {
		def analysisDateJSON = new JSONObject()
		analysisDateJSON.put("analysisDate", new Date())
		def schemaToJson = [
			"v1/schema/spidamin/asset/standard_details/analysis_asset.schema":analysisDateJSON.toString(),
			"v1/schema/spidamin/asset/standard_details/pole_asset.schema":'{"createdDate":"Thu Feb 13 12:04:50 EST 2014","dateInspected":"Wed Feb 26 13:28:19 EST 2014","modifiedDate":"Thu Feb 13 13:04:07 EST 2014"}',
			"v1/schema/spidamin/asset/standard_details/file_asset.schema":'{"dateCreated":"Thu Feb 13 13:04:07 EST 2014"}, "dateUpdated":"Thu Feb 13 12:04:50 EST 2014"}',	
		]
		def successes = []
		schemaToJson.each { schemaId, json ->
			def schema = factory.getJsonSchema(schemaId)
			report = schema.validate(JsonLoader.fromString(json))
			report.each{ log.info "validation report for ${schemaId} ||| ${json}"+it.toString() }
			if(report.isSuccess()) {
				successes << "${json} did validate against ${schemaId}"
			}
		}
		assert "None of the json should have validated so the successes list should be empty ${successes}", successes.isEmpty()
	}

	void testUser(){
		def instance = '{"id":1, "firstName":"bob", "lastName":"smith", "email":"bob@test.com", "company":{"id":1, "name":"test"}, "foreignCompanies":[{"id":2, "name":"SPIDA"}]}'				
		def schema = factory.getJsonSchema("v1/schema/spidamin/user/user.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}	

	void testAttachment(){
		def instance = '{"name":"blah","companyId": 42,"uuid":"blahblah", "bytes":"abc","associations":[{"level":"COMPANY","product": "CALC_DB","sourceId": "42", "latitude": 7, "longitude": 8}]}'				
		def schema = factory.getJsonSchema("v1/schema/spidamin/asset/attachment.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}	

	void testBasicProject(){
		def instance = '{"id":1,"flowId":1}'				
		def schema = factory.getJsonSchema("v1/schema/spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}	

	void testExampleProjectRequest(){
		def schema = factory.getJsonSchema("v1/schema/spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidamin/project_request.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}		

	void testExampleProjectResponse(){
		def schema = factory.getJsonSchema("v1/schema/spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/v1/examples/spidamin/project_response.json").text))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}

	void testBadExampleProjectRequest(){
		def schema = factory.getJsonSchema("v1/schema/spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString("""{ "name": "Project4", "flowId": 3359184, "draft": false, "deleted": false, "stations": [ { "other": "val" } ]	}"""))
		report.each{ log.info "validation report "+it.toString() }
		assertFalse "should be invalid instance against the schema", report.isSuccess()
	}

}
