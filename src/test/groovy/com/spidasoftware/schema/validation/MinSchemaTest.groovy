package com.spidasoftware.schema.validation

import groovy.json.*
import com.github.fge.jsonschema.util.*
import com.github.fge.jsonschema.main.*
import com.github.fge.jsonschema.uri.*
import com.github.fge.jsonschema.cfg.*
import org.apache.log4j.*
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.*
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonSchemaFactoryBuilder;
import com.github.fge.jsonschema.report.ProcessingReport;

class MinSchemaTest extends GroovyTestCase { 


	def log = Logger.getLogger(this.class);
	def report
	def fileUri = new File("resources").toURI().toString()
	final LoadingConfiguration cfg = LoadingConfiguration.newBuilder().setNamespace(fileUri).freeze();
	final JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();

	void setUp() {
		// org.apache.log4j.BasicConfigurator.configure();
		// log.setLevel(Level.INFO);
	}

	void testUser(){
		def instance = '{"id":1, "firstName":"bob", "lastName":"smith", "email":"bob@test.com", "company":{"id":1, "name":"test"}, "foreignCompanies":[{"id":2, "name":"SPIDA"}]}'				
		def schema = factory.getJsonSchema("v1/schema/spidamin/user/user.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "should be a valid instance against the schema", report.isSuccess()
	}	

	void testBasicProject(){
		def instance = '{"id":1,"flowId":1}'				
		def schema = factory.getJsonSchema("v1/schema/spidamin/project/project.schema")
		report = schema.validate(JsonLoader.fromString(instance))
		report.each{ log.info "validation report "+it.toString() }
		assertTrue "the intance itself should be true against a file namespace", report.isSuccess()
	}	

	void testFullProject(){
		//TODO
	}
}