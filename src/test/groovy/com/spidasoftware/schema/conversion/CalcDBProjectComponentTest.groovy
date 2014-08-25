package com.spidasoftware.schema.conversion

import groovy.util.logging.Log4j
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import spock.lang.Specification
import static org.junit.Assert.*

@Log4j
public class CalcDBProjectComponentTest extends Specification {
    JSONObject projectJSON

    def setup() {
        projectJSON = getProject("ImportTestProject.json")
    }

	void "components should get and set users"(){
		setup:
		JSONObject json = new JSONObject()
		CalcDBProject project = new CalcDBProject(json)
		String id = "12345"
		String email = "test@test.com"

		when:
		project.setUser(id, email)

		then:
		project.getUser().id == id
		project.getUser().email == email
	}

    public void testCalcDBDesign() throws Exception {
	    setup:
        JSONObject location = projectJSON.getJSONArray('leads').getJSONObject(0).getJSONArray('locations').getJSONObject(0)
        JSONObject calcJson = location.getJSONArray('designs').getJSONObject(0)
	    JSONObject designJson = new JSONObject()
	    designJson.put('calcDesign', calcJson)
	    designJson.put('id', "ffaa00")
        designJson.put('locationLabel', 'ExistingLocation')
        designJson.put('locationId', '12345')
        designJson.put('projectLabel', 'ImportTestProject')
        designJson.put('projectId', '678910')

	    when:
        CalcDBDesign design = new CalcDBDesign(designJson)

	    then:
        assertEquals("the name should be right", "Measured Design", design.getName())
        assertEquals("the location name should be right", "ExistingLocation", design.getParentLocationName())
        assertEquals("the location id should be right", "12345", design.getParentLocationId())
        assertEquals("the project name should be right", "ImportTestProject", design.getParentProjectName())
        assertEquals("the project id should be right", "678910", design.getParentProjectId())
    }

    public void testCalcDBLocation() throws Exception {
	    setup:
        JSONObject calcJson = projectJSON.getJSONArray('leads').getJSONObject(0).getJSONArray('locations').getJSONObject(0)
	    calcJson.designs = new JSONArray()
	    calcJson.designs.add([id: '12345', label: 'measured'] as JSONObject)

		JSONObject locationJson = new JSONObject()
	    locationJson.put('calcLocation', calcJson)

        locationJson.put("projectLabel", "ImportTestProject")
        locationJson.put("projectId", "00ff00")
        long dateMod = 1395837739000
        Date equivalentDate = new Date(dateMod)
        locationJson.put('dateModified', dateMod)

		when:
        CalcDBLocation location = new CalcDBLocation(locationJson)

		then:
        "ExistingLocation" == location.getName()
        "00ff00" == location.getParentProjectId()
        "ImportTestProject" == location.getParentProjectName()
        ['12345'] == location.getDesignIds()
        log.debug "**** Date= ${location.getDateModified().getTime()}"

        equivalentDate == location.getDateModified()
    }

    void testCalcDBProject() throws Exception {
	    setup:
        projectJSON.leads = new JSONArray()
	    projectJSON.leads.add(['locations': [ ['id': '1234', 'label': 'testPole'], ['id': '5678', 'label': 'test2'] ]] as JSONObject)
        projectJSON.put("id", "12345")

	    JSONObject refJson = new JSONObject()
	    refJson.put('calcProject', projectJSON)
	    refJson.put('dateModified', new Date().time)
	    refJson.put('id', '12345')

	    when:
        CalcDBProject calcDBProject = new CalcDBProject(refJson)

	    then:
        assertEquals("the name should be right", 'ImportTestProject', calcDBProject.getName())
        assertEquals("the client file should be right", "Master_1.1.client", calcDBProject.getClientFileName())
        assertEquals("the id should be right", "12345", calcDBProject.getCalcDBId())
        assertEquals("the child location ids should be right", ['1234', '5678'], calcDBProject.getChildLocationIds())
    }

    private JSONObject getProject(String name) {
        def projectString = getClass().getResourceAsStream("/formats/calc/projects/"+name).text
        return JSONObject.fromObject(projectString)
    }
}