package com.spidasoftware.schema.validation

import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration
import com.github.fge.jsonschema.main.JsonSchemaFactory
import com.github.fge.jsonschema.uri.*
import org.apache.log4j.Logger;

class ClientSchemaTest extends GroovyTestCase {


	def log = Logger.getLogger(this.class)
	def report

	LoadingConfiguration cfg
	JsonSchemaFactory factory

	void setUp() {
		 cfg = LoadingConfiguration.newBuilder().setNamespace(new File("resources").toURI().toString()).freeze();
		 factory = JsonSchemaFactory.newBuilder().setLoadingConfiguration(cfg).freeze();
	}


	private test(String name){
		def schema = factory.getJsonSchema("schema/spidacalc/client/${name}.schema")
		report = schema.validate(JsonLoader.fromString(new File("resources/examples/spidacalc/client/client_${name}_example.json").text))
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testClientPoleObject(){
		test("pole")
	}

	void testClientWireObject(){
		test("wire")
	}

	void testLoadCaseObject(){
		test("wire")
	}

	void testClientInsulatorObject(){
		test("insulator")
	}

	void testClientAnchorObject(){
		test("anchor")
	}

	void testClientEquipmentObject(){
		test("equipment")
	}

	void testClientCrossArmObject(){
		test("crossarm")
	}

	void testClientDataObject(){
		test('data')
	}



}
