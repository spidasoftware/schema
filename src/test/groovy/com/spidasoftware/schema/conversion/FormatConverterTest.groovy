package com.spidasoftware.schema.conversion

import com.github.fge.jsonschema.report.ProcessingReport
import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import spock.lang.Specification
import spock.lang.Unroll

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class FormatConverterTest extends Specification {

	double epsilon = 0.000001
	FormatConverter converter = new FormatConverter()

	@Unroll("#currentProject")
	void "referenced components should be valid against the schema"(){
		setup: "load calc project json"
			def calcProject = getCalcProject(currentProject)
			Validator validator = new Validator()

		when: "convert to a list of referenced components"
			def refCompList = converter.convertCalcProject(calcProject)

		then: "the project should validate against the schema"
			def refProject = refCompList.find{it instanceof CalcDBProject}
			def projectReport = validator.validateAndReport("/schema/spidamin/spidadb/referenced_project.schema", JsonOutput.toJson(refProject.getMap()))
			projectReport.isSuccess()

		then: "the locations should validate against the schema"
			def refLocations = refCompList.findAll{ it instanceof CalcDBLocation}
			refLocations.each{
				def locationReport = validator.validateAndReport("/schema/spidamin/spidadb/referenced_location.schema", JsonOutput.toJson(it.getMap()))
				assert locationReport.isSuccess(), "location: ${it.getName()} failed validation: \n${locationReport}"
			}

		then: "the designs should validate against the schema"
			def refDesigns = refCompList.findAll{it instanceof CalcDBDesign}
			refDesigns.each{CalcDBDesign it->
				def designReport = validator.validateAndReport("/schema/spidamin/spidadb/referenced_design.schema", JsonOutput.toJson(it.getMap()))
				assert designReport.isSuccess(), "Design: ${it.getName()} at ${it.getParentLocationName()} in invalid \n${designReport}"
			}

		where:
			currentProject << ["four-locations-one-lead-project.json", "18-locations-analyzed.json", "single-full-pole.json"]

	}

	void "test converting calc to referenced and back again"(){
		setup: "start with a calc project"
			def calcProject = getCalcProject("four-locations-one-lead-project.json")

		and: "validate json to make sure it's valid before format conversion"
			assert jsonIsValid(calcProject), "Starting project JSON is valid against schema"

		when: "convert the project to calcdb components"
			def calcdbComponents = converter.convertCalcProject(calcProject)

		then: "just a quick check to make sure it converted all of the components"
			calcdbComponents.size() == 17

		when: "convert the project back to calc format"
			CalcDBProject p = calcdbComponents.find{it instanceof CalcDBProject}
			List calcdbLocations = calcdbComponents.findAll{ it instanceof CalcDBLocation }
			List calcdbDesigns = calcdbComponents.findAll{ it instanceof CalcDBDesign }
		    Map reconstitutedCalcProject = converter.convertCalcDBProject(p, calcdbLocations, calcdbDesigns)

		then: "the reconstituted project should be valid against the schema"
			jsonIsValid(reconstitutedCalcProject)

	}

	void "analysis results should get collected properly by component"(){
		setup: "load the results from a design"
			def design = getCalcProject("18-locations-analyzed.json").leads[0].locations[4].designs[0]
			def analysisResultsList = design.analysis

		when: "collect the results for the pole"
			def poleResults = converter.collectPoleResults(analysisResultsList)

		then: "the list should contain the correct results"
			notThrown(Exception)
			poleResults.size() == 2
			def loadingResult = poleResults.find{ it.component == "Pole" }
			assertEquals("the pole loading result shoul be correct", 4.028504221186, loadingResult.actual, epsilon)


			def buckling = poleResults.find{ it.component == "Pole-Buckling" }
			assertEquals("the buckling result should be correct", 3.8975996880018, buckling.actual, epsilon)

		when: "collect results for a guy"
			def guy1Results = converter.getResultsForComponent("Guy#1", analysisResultsList)

		then: "the guy results should be correct"
			notThrown(Exception)
			guy1Results.size() == 1
			def guy1 = guy1Results[0]
			assertEquals("guy#1 result should be correct", 0.86576092650, guy1.actual, epsilon)

	}

	void "analysis results should get converted properly"(){
		setup: "load a project with analysis results"
			def project = getCalcProject("18-locations-analyzed.json")
			def components = converter.convertCalcProject(project)

		when: "convert the project"
			def designs = components.findAll {it instanceof CalcDBDesign}*.getMap()

		then: "all the worst*Results should be correct and the results should be attached to the correct components"
			designs.size() == 18
			def d1 = designs[0]
			assertEquals("the worstPoleResult should be correct", 2.3004643640057, d1.worstCaseAnalysisResults.pole.actual, epsilon)
			assertEquals("the worstGuyResult should be correct", 1.59366362255, d1.worstCaseAnalysisResults.guys.actual, epsilon)

			//check all the designs to make sure they have a worstPoleResult
			designs.each{
				def right = (it.worstCaseAnalysisResults.pole.actual > 0.0)
				if (!right){
					// it just ain't right
					println "***** Incorrect worstPoleResult: design: ${it.locationLabel} - ${it.label}, result: ${it.worstPoleResult}"
				}
				assertTrue("design: ${it.id} should have a proper worstCaseAnalysisResult for the pole", right)
			}
	}

	void "format converter should not barf when 'clientFile' and 'clientFileVersion' fields are missing"(){
		setup: "get calc project json and remove those fields"
			def projectJson = getCalcProject("18-locations-analyzed.json")
			projectJson.remove('clientFile')
			projectJson.remove('clientFileVersion')

		when: "convert the project json into calcdb components"
			converter.convertCalcProject(projectJson)

		then: "shouldn't throw any exceptions"
			notThrown(Exception)

	}

	def "designs should be converted by themselves"(){
		when:
			def design = converter.convertCalcDesign(current, null, null)?.getMap()

		then:
			design.get('dateModified')
			design.worstCaseAnalysisResults instanceof Map
			design.worstCaseAnalysisResults.containsKey('pole')

		where:
			current << getCalcDesignsList("busy-trans-with-results", 4)
	}

	void "Converting to calcDB objects should not throw any exceptions"(){
		when:
			def fourPoles = getCalcProject("four-locations-one-lead-project.json")
		then:
			notThrown(Exception)
			fourPoles != null
			def components = converter.convertCalcProject(fourPoles)
			def project = components.find {it instanceof CalcDBProject}.getMap()
			def locations = components.findAll {it instanceof CalcDBLocation}*.getMap()
			def designs = components.findAll {it instanceof CalcDBDesign}*.getMap()
			project != null
			locations != null
			designs != null
			project.calcProject.leads.size() == 1
			project.calcProject.leads[0].locations.size() == 4
			locations.size() == 4
			designs.size() == 12
			def projectId = project["id"]

			projectId == locations.get(0)["projectId"]
			projectId == designs.get(0)["projectId"]
	}

	@Unroll("The design: #currentDesignName should have the correct worst case loading results")
	void "Designs have the worstResults set to the correct result object"(){
		setup: "load designs with a variety of load cases"
			def project = getCalcProject("LoadCaseTest.json")
			def designs = project.leads[0].locations[0].designs

		when: "add analysisResults to the current design"
			def currentDesign = designs.find{ it.label == currentDesignName }
			def design = converter.convertCalcDesign(currentDesign, null, null).getMap()

		then: "the correct worst results should be set"
			assertEquals("Worst Pole result should be correct", worstPole, design.worstCaseAnalysisResults.pole.actual, e)
			assertEquals("worst anchor result is correct", worstAnchor, design.worstCaseAnalysisResults.anchors.actual, e)
			assertEquals("worst guy result is correct", worstGuy, design.worstCaseAnalysisResults.guys.actual, e)
			assertEquals("worst pushbrace is correct", worstPushbrace, design.worstCaseAnalysisResults.pushBraces.actual, e)
			assertEquals("worst xarm is correct", worstCrossarm, design.worstCaseAnalysisResults.crossArms.actual, e)
			assertEquals("worst insulator is correct", worstInsulator, design.worstCaseAnalysisResults.insulators.actual, e)

		where:
			currentDesignName     | worstPole | worstAnchor | worstGuy | worstPushbrace | worstCrossarm | worstInsulator | e
			"go95-strength"       | 1.28 	  | 0.36 	    | 0.34 	   | 2.20			| 0.40			| 0.46 			 | 0.01
			"nesc-strength"	      | 149		  | 305 		| 352 	   | 68				| 379			| 280 			 | 1.0
			"nesc-lowered-stress" | 188       | 305 		| 352 	   | 68				| 379			| 280 			 | 1.0
			"strength-is-worst"   | 0.4 	  | 0.36 		| 0.34 	   | 2.20 			| 0.40 		    | 0.46 			 | 0.1
	}

	@Unroll
	void "test detailed results get converted correctly to worstCaseAnalysisResults jsonKey=#jsonKey"() {
		setup:
			def project = getCalcProject("project-with-detailed-results.json")
		when:
			def design = converter.convertCalcDesign(project.leads[0].locations[0].designs[0], null, null).getMap()
		then:
			Math.abs(actual - design.worstCaseAnalysisResults.get(jsonKey).actual) < 0.01
			Math.abs(allowable - design.worstCaseAnalysisResults.get(jsonKey).allowable) < 0.01
			design.worstCaseAnalysisResults.get(jsonKey).unit == unit
			design.worstCaseAnalysisResults.get(jsonKey).analysisDate == analysisDate
			design.worstCaseAnalysisResults.get(jsonKey).component == component
			design.worstCaseAnalysisResults.get(jsonKey).loadInfo == loadInfo
			design.worstCaseAnalysisResults.get(jsonKey).passes == passes
			design.worstCaseAnalysisResults.get(jsonKey).analysisType == analysisType
		where:
			jsonKey      | actual | allowable | unit      | analysisDate  | component     | loadInfo | passes | analysisType
			"pole"       | 4.47   | 100.0     | "PERCENT" | 1484845969502 | "Pole"        | "Medium" | true   | "STRESS"
			"anchors"    | 1.45   | 100.0     | "PERCENT" | 1484845969502 | "Anchor#2"    | "Medium" | true   | "FORCE"
			"guys"       | 0.48   | 100.0     | "PERCENT" | 1484845969502 | "Guy#2"       | "Medium" | true   | "FORCE"
			"pushBraces" | 0.16   | 100.0     | "PERCENT" | 1484845969502 | "PushBrace#1" | "Medium" | true   | "STRESS"
			"crossArms"  | 6.88   | 100.0     | "PERCENT" | 1484845969502 | "CrossArm#2"  | "Medium" | true   | "STRESS"
			"insulators" | 4.17   | 100.0     | "PERCENT" | 1484845969502 | "Insulator#1" | "Medium" | true   | "FORCE"
	}

	void "Project data should get copied to locations and designs"(){
		setup:
			def project
			def locations
			def designs

		when:
			def components = converter.convertCalcProject(current)
			project = components.find {it instanceof CalcDBProject}.getMap()
			locations = components.findAll {it instanceof CalcDBLocation}*.getMap()
			designs = components.findAll {it instanceof CalcDBDesign}*.getMap()

		then:
			notThrown(Exception)
			def projectId = project.id
			projectId == project.calcProject.id //make sure they are the same
			projectId == locations[0]."projectId"
			project.calcProject.label == locations[0]."projectLabel"
			projectId == designs[0]."projectId"
			project.calcProject.label == designs[0]."projectLabel"
			project.calcProject.leads[0].locations[0].id == locations[0]."id"

		where:
			current << [getCalcProject("single-full-pole.json"), getCalcProject("four-locations-one-lead-project.json"), getCalcProject("minimal-project-valid.json")]
	}

	void "CalcDB components should be converted into calc-ready JSON"() {
		/* Just recreate a CalcDB project from it's component parts and make sure it goes together okay */
		when:
			CalcDBProject calcDBProject = new CalcDBProject(loadJson("exampleProject", "projects")[0] as Map)
			Map calcDBLocations = [:]
			Map calcDBDesigns = [:]
			loadJson("exampleProject", "locations").each { location ->
				calcDBLocations.put(location."_id", new CalcDBLocation(location as Map))
			}
			loadJson("exampleProject", "designs").each { design ->
				calcDBDesigns.put(design."_id", new CalcDBDesign(design as Map))
			}

		    Map projectJson = converter.convertCalcDBProject(calcDBProject, calcDBLocations, calcDBDesigns)
			def schema = "/schema/spidacalc/calc/project.schema"
			def report = new Validator().validateAndReport(schema, JsonOutput.toJson(projectJson))

		then:
			notThrown(Exception)
			// the projectJson should contain the correct label
			"exampleProject" == projectJson.get("label")

			// each JSON location should have 3 designs
			projectJson.leads[0].locations.each {
				3 == it.designs.size()
			}
			// and the json output should validate successfully
			report.isSuccess()
	}

	private Map getCalcObject(String name, String type) {
		return new JsonSlurper().parse(getClass().getResourceAsStream("/formats/calc/${type}s/${name}"))
	}

	private Map getCalcDesign(String name) {
		return getCalcObject(name, "design")
	}

	private List<Map> getCalcDesignsList(String name, int num) {
		return (0..num).collect { getCalcDesign(name + '-'+ it + '.json') }
	}

	private Map getCalcProject(String name) {
		return getCalcObject(name, "project")
	}

	private List loadJson(String project, String type) {
	    Map json = new JsonSlurper().parse(getClass().getResourceAsStream("/formats/calcdb/${project}/${type}.json"))
		return json.get(type)
	}

	boolean jsonIsValid(Map projectJson) {
		ProcessingReport report = new Validator().validateAndReport("/schema/spidacalc/calc/project.schema", JsonOutput.toJson(projectJson))
		if (!report.isSuccess()) {
			println "JSON does not validate against schema:"
			println report.toString()
		}
		return report.isSuccess()
	}

}
