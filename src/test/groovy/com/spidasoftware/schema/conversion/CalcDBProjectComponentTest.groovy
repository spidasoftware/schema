package com.spidasoftware.schema.conversion

import groovy.util.logging.Log4j
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

@Log4j
public class CalcDBProjectComponentTest {
    JSONObject projectJSON

    @Before
    public void setUp() throws Exception {
        projectJSON = getProject("ImportTestProject.json")
    }

    @Test
    public void testCalcDBDesign() throws Exception {
        JSONObject location = projectJSON.getJSONArray('leads').getJSONObject(0).getJSONArray('locations').getJSONObject(0)
        JSONObject designJson = location.getJSONArray('designs').getJSONObject(0)
        designJson.put('_id', "ffaa00")
        designJson.put('locationName', 'ExistingLocation')
        designJson.put('locationId', '12345')
        designJson.put('projectName', 'ImportTestProject')
        designJson.put('projectId', '678910')

        CalcDBDesign design = new CalcDBDesign(designJson)

        assertEquals("the name should be right", "Measured Design", design.getName())
        assertEquals("the location name should be right", "ExistingLocation", design.getParentLocationName())
        assertEquals("the location id should be right", "12345", design.getParentLocationId())
        assertEquals("the project name should be right", "ImportTestProject", design.getParentProjectName())
        assertEquals("the project id should be right", "678910", design.getParentProjectId())
    }

    @Test
    public void testCalcDBLocation() throws Exception {
        JSONObject locationJson = projectJSON.getJSONArray('leads').getJSONObject(0).getJSONArray('locations').getJSONObject(0)

        def designIds = ['12345', '678910']
        locationJson.put('designs', JSONArray.fromObject(designIds))
        locationJson.put("projectName", "ImportTestProject")
        locationJson.put("projectId", "00ff00")
        String dateMod = "2014-03-26T12:42:19.852Z"
        Date equivalentDate = new Date(1395837739000)
        locationJson.put('dateModified', dateMod)


        CalcDBLocation location = new CalcDBLocation(locationJson)

        assertEquals("the name should be right", "ExistingLocation", location.getName())
        assertEquals("the project id should be right", "00ff00", location.getParentProjectId())
        assertEquals("the project name should be right", "ImportTestProject", location.getParentProjectName())
        assertEquals("the designs should be right", designIds, location.getDesignIds())
        log.debug "**** Date= ${location.getDateModified().getTime()}"

        assertEquals("the date should be right", equivalentDate, location.getDateModified())
    }

    @Test
    void testCalcDBProject() throws Exception {
        JSONArray locs = JSONArray.fromObject(['1234', '5678'])
        projectJSON.remove('leads')
        projectJSON.put('locations', locs)
        projectJSON.put("_id", "12345")

        CalcDBProject calcDBProject = new CalcDBProject(projectJSON)

        assertEquals("the name should be right", 'ImportTestProject', calcDBProject.getName())
        assertEquals("the client file should be right", "Master_1.1.client", calcDBProject.getClientFileName())
        assertEquals("the _id should be right", "12345", calcDBProject.getCalcDBId())
        assertEquals("the child location ids should be right", ['1234', '5678'], calcDBProject.getChildLocationIds())
    }

    private JSONObject getProject(String name) {
        def projectString = getClass().getResourceAsStream("/formats/calc/projects/"+name).text
        return JSONObject.fromObject(projectString)
    }
}