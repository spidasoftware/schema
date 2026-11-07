/*
 * Copyright (c) 2024 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.validation

class ClientSchemaTest extends GroovyTestCase {
	def report

	Validator validator = new Validator()

	private test(String name){
		report = validator.validateAndReport("/schema/spidacalc/client/${name}.schema".toString(), new File("resources/examples/spidacalc/client/client_${name}_example.json").text)
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

	void testClientTruss() {
		test("truss")
	}

	void testClientTrussStructural() {
		report = validator.validateAndReport("/schema/spidacalc/client/truss.schema", new File("resources/examples/spidacalc/client/client_truss_structural_example.json").text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testClientTrussRejectsUnknownModelingType() {
		Map json = new groovy.json.JsonSlurper().parse(new File("resources/examples/spidacalc/client/client_truss_example.json")) as Map
		json.modelingType = "PRISMATIC"
		report = validator.validateAndReport("/schema/spidacalc/client/truss.schema", json)
		assertFalse "an unknown modelingType should be rejected", report.isSuccess()
	}

	void testClientTrussCapacityTableRowRequiresBothValues() {
		Map json = new groovy.json.JsonSlurper().parse(new File("resources/examples/spidacalc/client/client_truss_structural_example.json")) as Map
		(json.momentCapacityAtHeights as List<Map>)[0].remove("maximumAllowableMoment")
		report = validator.validateAndReport("/schema/spidacalc/client/truss.schema", json)
		assertFalse "a capacity table row without a moment should be rejected", report.isSuccess()
	}
}
