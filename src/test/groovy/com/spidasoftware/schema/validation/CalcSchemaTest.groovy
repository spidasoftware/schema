package com.spidasoftware.schema.validation

import groovy.json.JsonSlurper

class CalcSchemaTest extends GroovyTestCase {

	def report
	Validator validator = new Validator()


	void validateFile(String schema, String path) {
		report= validator.validateAndReport(schema,
											Validator.getResourceAsStream(path).text)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

	}
	void testProjectWithForms(){
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/minimal_project_with_forms.json" )

	}

	void testClientPoleObject(){
		def instance = '{"species":"pine", "height":{"unit":"FOOT", "value":100}, "classOfPole":"h2"}'
		report = validator.validateAndReport("/schema/spidacalc/calc/client_references/pole.schema", instance)
		assertTrue ("this instance should be valid against the schema \n${report.toString()}", report.isSuccess())
	}

	void testBasicDesignObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/empty_pole.json" )
	}

	void testOneOfEverythingObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/one_of_everything.json" )
	}

	void testDamagesObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/damages.json" )
	}

	void testBisectorObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/bisector.json" )
	}
	void testInsulatorObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/insulator.json" )
	}

	void testWireObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/wire.json" )
	}

	void testXArmObject(){
		validateFile("/schema/spidacalc/calc/structure.schema", "/examples/spidacalc/designs/xarm.json" )
	}


	void testProjectWithGPSObject(){
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/minimal_project_with_gps.json" )
	}

	void testFullProjectObject(){
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/full_project.json" )
	}

	void testMinimalProjectObject(){
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/minimal_project_no_designs.json" )
	}

	void testMultipleProjects(){
		validateFile("/schema/spidacalc/calc/projects.schema", "/examples/spidacalc/projects/multiple_projects.json" )
	}

	void testProjectAdditionalProperties() {
		def schema = "/schema/spidacalc/calc/project.schema"
	    Map json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		json.put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		def testFailues = []
		def locationComponents = ["geographicCoordinate", "remedies", "poleTags", "images"]
		locationComponents.each { component ->
			json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
			if(component == "geographicCoordinate") {
				json.get("leads").get(0).get("locations").get(0).get(component).put("an additional property", "shouldn't validate")
			} else {
				json.get("leads").get(0).get("locations").get(0).get(component).get(0).put("an additional property", "shouldn't validate")
			}
			report = validator.validateAndReport(schema,json)
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${component}, it should not validate but did."
			}
		}
		assertEquals "The list should be empty, if it is not there are failures", [], testFailues

		json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).get("designs").get(0).put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		json.get("leads").get(0).get("locations").get(0).get("designs").get(0).get("analysis").get(0).put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testStructureAdditionalProperties() {
		def testFailues = []
		def allStructureComponents = ["anchors", "wireEndPoints", "spanPoints", "notePoints", "pointLoads", "damages", "wires", "spanGuys", "guys",
				  					  "equipments", "crossArms", "insulators",  "pushBraces"]
	    def schema = "/schema/spidacalc/calc/structure.schema"

	    // Test the structure object
	    Map json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/designs/one_of_everything.json"))
		json.put('strict', true)
	    json.put("an additional property", "shouldn't validate")
	    report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

	    // Test the pole object
	    json  = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/designs/one_of_everything.json"))
		json.put('strict', true)
		json.get("pole").put("an additional property", "shouldn't validate")
		report = validator.validateAndReport(schema,json)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()

		// Test all of the other properties of the structure
	    allStructureComponents.each { item ->
			json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/designs/one_of_everything.json"))
			json.put('strict', true)
			json.get(item).get(0).put("an additional property", "shouldn't validate")
			report = validator.validateAndReport(schema,json)
			if(report.isSuccess()) {
				testFailues << "added an additional property to ${item}, it should not validate but did."
			}
	    }
	    assertEquals "The list should be empty", [], testFailues
	}

	void testLocationImageURLAndLink() {
		def json = new JsonSlurper().parse(Validator.getResourceAsStream("/examples/spidacalc/projects/full_project.json"))
		def location = json.get("leads").get(0).get("locations").get(0)
		location.get("images").add(["url":"/some/url"])
		location.get("images").add(["url":"/some/url", "link":["source":"FFF","id":"3432432432"]])
		def schema = "/schema/spidacalc/calc/location.schema"
		report = validator.validateAndReport(schema,location)
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()

		location.get("images").add(["link":["source":"FFF","id":"3432432432"]])
		report = validator.validateAndReport(schema,location)
		assertFalse "this instance should NOT be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testProjectWithInputAssemblies() {
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/project-with-input-assemblies.json" )
	}

	void testProjectWithAssemblies() {
		validateFile("/schema/spidacalc/calc/project.schema", "/examples/spidacalc/projects/project-with-assemblies.json" )
	}

	void testNoResultsValidation() {
		def schema = "/schema/spidacalc/calc/design.schema"
		String json = """{
		        "analysis": [
						{"id": "Empty Results Array", "results": []},
						{"id": "No Results Array"}
						
		        ]
		}"""
		report = validator.validateAndReport(schema, json)
		report.each{ log.info "testNoResultsValidation validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}

	void testBundleWithNoComponents() {
		def schema = "/schema/spidacalc/client/bundle.schema"
		String json = """{
              "aliases" : [ ],
              "size" : "1/4\\" EHS",
              "diameter" : {
                "unit" : "METRE",
                "value" : 0.006350000000000001
              },
              "group" : "Quick Bundles",
              "autoCalculateDiameter" : true,
              "source" : "PROJECT",
              "messenger" : {
                "size" : "1/4\\" EHS",
                "conductorStrands" : 7,
                "coreStrands" : 0,
                "clientItemVersion" : "f298beaf2855b35370db33afcb214745"
              },
              "bundleComponents" : [ ]
            }"""
		report = validator.validateAndReport(schema, json)
		report.each{ log.info "'bundle with no components' validation report "+it.toString() }
		assertTrue "this instance should be valid against the schema \n${report.toString()}", report.isSuccess()
	}
}
