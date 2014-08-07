package com.spidasoftware.schema.conversion

import com.github.fge.jsonschema.report.ProcessingReport
import com.spidasoftware.schema.validation.Validator
import groovy.util.logging.Log4j
import net.sf.json.JSONArray
import spock.lang.*
import net.sf.json.JSONObject
import java.text.SimpleDateFormat
import static org.junit.Assert.*

@Log4j
class FormatConverterTest extends Specification {
    double epsilon = 0.000001

	void "Referenced components should still be valid against the schema"(){
		setup: "load calc project json"
		def calcProject = getCalcProject("four-locations-one-lead-project.json")
		Validator validator = new Validator()

		when: "convert to a list of referenced components"
		def refCompList = FormatConverter.convertCalcProject(calcProject)

		then: "the project should validate against the schema"
		def refProject = refCompList.find{it instanceof CalcDBProject}
		def projectReport = validator.validateAndReport("/v1/schema/calcdb/referencedProject.schema", refProject.getJSON().toString())
		projectReport.isSuccess()

		then: "the locations should validate against the schema"
		def refLocations = refCompList.findAll{ it instanceof CalcDBLocation}
		refLocations.each{
			def locationReport = validator.validateAndReport("/v1/schema/calcdb/referencedLocation.schema", it.getJSON().toString())
			assert locationReport.isSuccess(), "location: ${it.getName()} failed validation: \n${locationReport}"
		}

		then: "the designs should validate against the schema"
		def refDesigns = refCompList.findAll{it instanceof CalcDBDesign}
		refDesigns.each{CalcDBDesign it->
			def designReport = validator.validateAndReport("/v1/schema/calcdb/referencedDesign.schema", it.getJSON().toString())
			assert designReport.isSuccess(), "Design: ${it.getName()} at ${it.getParentLocationName()} in invalid \n${designReport}"
		}

	}

	void "test converting calc to referenced and back again"(){
		setup: "start with a calc project"
		def calcProject = getCalcProject("four-locations-one-lead-project.json")

		and: "validate json to make sure it's valid before format conversion"
		assert jsonIsValid(calcProject), "Starting project JSON is valid against schema"

		when: "convert the project to calcdb components"
		def calcdbComponents = FormatConverter.convertCalcProject(calcProject)

		then: "just a quick check to make sure it converted all of the components"
		calcdbComponents.size() == 17

		when: "convert the project back to calc format"
		CalcDBProject p = calcdbComponents.find{it instanceof CalcDBProject}
		List calcdbLocations = calcdbComponents.findAll{ it instanceof CalcDBLocation }
		List calcdbDesigns = calcdbComponents.findAll{ it instanceof CalcDBDesign }
		JSONObject reconstitutedCalcProject = FormatConverter.convertCalcDBProject(p, calcdbLocations, calcdbDesigns)

		then: "the reconstituted project should be valid against the schema"
		jsonIsValid(reconstitutedCalcProject)

	}

	boolean jsonIsValid(JSONObject projectJson) {
		ProcessingReport report = new Validator().validateAndReport("/v1/schema/spidacalc/calc/project.schema", projectJson.toString())
		if (!report.isSuccess()) {
			println "JSON does not validate against schema:"
			println report.toString()
		}
		return report.isSuccess()
	}

    void "analysis results should get collected properly by component"(){
        setup: "load the results from a design"
        def design = getCalcProject("18-locations-analyzed.json").leads[0].locations[4].designs[0]
        def analysisResultsList = design.analysis

        when: "collect the results for the pole"
        def poleResults = FormatConverter.collectPoleResults(analysisResultsList)

        then: "the list should contain the correct results"
        poleResults.size() == 2
        def loadingResult = poleResults.find{ it.component == "Pole" }
        assertEquals("the pole loading result shoul be correct", 4.028504221186, loadingResult.actual, epsilon)


        def buckling = poleResults.find{ it.component == "Pole-Buckling" }
        assertEquals("the buckling result should be correct", 3.8975996880018, buckling.actual, epsilon)

        when: "collect results for a guy"
        def guy1Results = FormatConverter.getResultsForComponent("Guy#1", analysisResultsList)

        then: "the guy results should be correct"
        guy1Results.size() == 1
        def guy1 = guy1Results[0]
        assertEquals("guy#1 result should be correct", 0.86576092650, guy1.actual, epsilon)

    }

    void "analysis results should get converted properly"(){
        setup: "load a project with analysis results"
        def project = getCalcProject("18-locations-analyzed.json")
        def components = FormatConverter.convertCalcProject(project)

        when: "convert the project"
        def designs = components.findAll {it instanceof CalcDBDesign}*.getJSON()

        then: "all the worst*Results should be correct and the results should be attached to the correct components"
        designs.size() == 18
        def d1 = designs[0]
        assertEquals("the worstPoleResult should be correct", 2.3004643640057, d1.worstPoleResult.actual, epsilon)
        assertEquals("the worstGuyResult should be correct", 1.59366362255, d1.worstGuyResult.actual, epsilon)

        def guy1 = d1.structure.guys.find{ it.id == "Guy#1" }
        assertEquals("the guy result should be part of the Guy in the structure", 1.59366362255, guy1.analysisResults[0].actual, epsilon)

        def pole = d1.structure.pole
        def poleLoading = pole.analysisResults.find{ it.component == "Pole" }
        assertEquals("the pole result should be part of the Pole in the structure", 2.3004643640057, poleLoading.actual, epsilon)
        pole.analysisResults.size() == 2 //Should have poleBuckling as well as loading

        //check all the designs to make sure they have a worstPoleResult
        designs.each{
            def right = (it.worstPoleResult.actual > 0.0)
            if (!right){
                // it just ain't right
                log.debug "***** Incorrect worstPoleResult: design: ${it.locationLabel} - ${it.label}, result: ${it.worstPoleResult}"
            }
            assertTrue("every design should have a proper worstPoleResult", right)
        }
    }

	void "format converter should not barf when 'clientFile' and 'clientFileVersion' fields are missing"(){
		setup: "get calc project json and remove those fields"
		def projectJson = getCalcProject("18-locations-analyzed.json")
		projectJson.remove('clientFile')
		projectJson.remove('clientFileVersion')

		when: "convert the project json into calcdb components"
        FormatConverter.convertCalcProject(projectJson)

		then: "shouldn't throw any exceptions"
		notThrown(Exception)

	}

    def "designs should be converted by themselves"(){
        when:
        def design = FormatConverter.convertCalcDesign(current, null, null)?.getJSON()

        then:
        design.structure.pole.analysisResults.size() > 0
        design.getLong('dateModified')
        design.worstPoleResult instanceof JSONObject

        where:
        current << getCalcDesignsList("busy-trans-with-results", 4)
    }

    void "Converting to calcDB objects should not throw any exceptions"(){

        expect:
        def fourPoles = getCalcProject("four-locations-one-lead-project.json")
        fourPoles != null
        def components = FormatConverter.convertCalcProject(fourPoles)
        def project = components.find {it instanceof CalcDBProject}.getJSON()
        def locations = components.findAll {it instanceof CalcDBLocation}*.getJSON()
        def designs = components.findAll {it instanceof CalcDBDesign}*.getJSON()
        project != null
        locations != null
        designs != null
        project.locations.size() == 4
        locations.size() == 4
        designs.size() == 12
        def projectId = project["_id"]

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
        def design = FormatConverter.convertCalcDesign(currentDesign, null, null).getJSON()

        then: "the correct worst results should be set"
        assertEquals("Worst Pole result should be correct", worstPole, design.worstPoleResult.actual, e)
        assertEquals("worst anchor result is correct", worstAnchor, design.worstAnchorResult.actual, e)
        assertEquals("worst guy result is correct", worstGuy, design.worstGuyResult.actual, e)
        assertEquals("worst pushbrace is correct", worstPushbrace, design.worstPushBraceResult.actual, e)
        assertEquals("worst xarm is correct", worstCrossarm, design.worstCrossArmResult.actual, e)
        assertEquals("worst insulator is correct", worstInsulator, design.worstInsulatorResult.actual, e)

        where:
        currentDesignName     | worstPole | worstAnchor | worstGuy | worstPushbrace | worstCrossarm | worstInsulator | e
        "go95-strength"       | 1.28 	  | 0.36 	    | 0.34 	   | 2.20			| 0.40			| 0.46 			 | 0.01
        "nesc-strength"	      | 149		  | 305 		| 352 	   | 68				| 379			| 280 			 | 1.0
        "nesc-lowered-stress" | 188       | 305 		| 352 	   | 68				| 379			| 280 			 | 1.0
        "strength-is-worst"   | 0.4 	  | 0.36 		| 0.34 	   | 2.20 			| 0.40 		    | 0.46 			 | 0.1

    }

    void "Project data should get copied to locations and designs"(){
        setup:
        def project
        def locations
        def designs


        when:
        def components = FormatConverter.convertCalcProject(current)
        project = components.find {it instanceof CalcDBProject}.getJSON()
        locations = components.findAll {it instanceof CalcDBLocation}*.getJSON()
        designs = components.findAll {it instanceof CalcDBDesign}*.getJSON()

        then:
        def projectId = project."_id"
        projectId == locations[0]."projectId"
        project.label == locations[0]."projectLabel"
        projectId == designs[0]."projectId"
        project.label == designs[0]."projectLabel"
        project.locations.contains(locations[0]."_id")

        where:
        current << [getCalcProject("single-full-pole.json"), getCalcProject("four-locations-one-lead-project.json"), getCalcProject("minimal-project-valid.json")]
    }

    void "CalcDB components should be converted into calc-ready JSON"() {
        /*
        Just recreate a CalcDB project from it's component parts and make sure it goes together okay
         */
        when:
        CalcDBProject calcDBProject = new CalcDBProject(loadJson("exampleProject", "projects")[0] as JSONObject)
        Map calcDBLocations = [:]
        Map calcDBDesigns = [:]
        loadJson("exampleProject", "locations").each { location ->
            calcDBLocations.put(location."_id", new CalcDBLocation(location as JSONObject))
        }
        loadJson("exampleProject", "designs").each { design ->
            calcDBDesigns.put(design."_id", new CalcDBDesign(design as JSONObject))
        }

        JSONObject projectJson = FormatConverter.convertCalcDBProject(calcDBProject, calcDBLocations, calcDBDesigns)
        def schema = "/v1/schema/spidacalc/calc/project.schema"
        def report = new Validator().validateAndReport(schema, projectJson.toString())

        then:
        // the projectJson should contain the correct label
        "exampleProject" == projectJson.getString("label")

        // each JSON location should have 3 designs
        projectJson.leads[0].locations.each {
            3 == it.designs.size()
        }
        // and the json output should validate successfully
        report.isSuccess()
    }

    private JSONObject getCalcObject(String name, String type) {
        String designString = getClass().getResourceAsStream("/formats/calc/${type}s/${name}").text
        return JSONObject.fromObject(designString)
    }

    private JSONObject getCalcDesign(String name) {
        return getCalcObject(name, "design")
    }

    private List<JSONObject> getCalcDesignsList(String name, int num) {
        return (0..num).collect { getCalcDesign(name + '-'+ it + '.json') }
    }

    private JSONObject getCalcProject(String name) {
        return getCalcObject(name, "project")
    }

    private JSONArray loadJson(String project, String type) {
        String text = getClass().getResourceAsStream("/formats/calcdb/${project}/${type}.json").text
        JSONObject json = JSONObject.fromObject(text)
        return json.getJSONArray(type)
    }
}