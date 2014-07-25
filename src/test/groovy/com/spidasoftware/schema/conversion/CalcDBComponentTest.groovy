package com.spidasoftware.schema.conversion

import groovy.util.logging.Log4j
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

/**
 * Created with IntelliJ IDEA.
 * User: pfried
 * Date: 2/10/14
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
@Log4j
public class CalcDBComponentTest {
    CalcDBProject project
    CalcDBLocation location
    CalcDBDesign design

    @Before
    public void setUp() throws Exception {
        project = loadProject("/formats/calcdb/minimal-project-valid/projects.json")
        location = loadLocation("/formats/calcdb/minimal-project-valid/locations.json")
        design = loadDesign("/formats/calcdb/minimal-project-valid/designs.json")
    }

    @Test
    public void testGetCalcDBId() throws Exception {
        assertEquals("project should return correct _id", "52d065f1e4b0576ebf7c3900", project.getCalcDBId())
        assertEquals("location should return correct _id", "52d065f1e4b0576ebf7c3901", location.getCalcDBId())
        assertEquals("design should return the correct _id", "52d065f1e4b0576ebf7c3902", design.getCalcDBId())
    }

    @Test
    public void testGetClientFileName() throws Exception {
        ["project", "location", "design"].each {
            assertEquals("${it} should return the correct client file", "Master_1.1.client", this."$it".getClientFileName())
        }
    }

    @Test
    public void testGetDateModified() throws Exception {
        ["project", "location", "design"].each {
            log.debug "${it} dateModified= " + this."$it".getDateModified() + ", dateString= ${this."$it".getJSON().getString("dateModified")}"
            assertNotNull("getDateModified should return the Date", this."$it".getDateModified())
        }
    }

    @Test
    public void testGetPhotosFromLocation() {
        def otherLocation = loadLocation("/formats/calcdb/exampleProject/locations.json")
        def photoIds = otherLocation.getPhotoIds()
        assertEquals("Should be one photo loaded from the json", 1, photoIds.size())
        assertNotNull("Should return the correct photo", photoIds.find { it == "5c6bd2e1-4cd9-4c45-9c49-07c93ca231c9" })
    }

    private CalcDBLocation loadLocation(String resourceString) {
        JSONObject json = getResource(resourceString)
        return new CalcDBLocation(json.getJSONArray("locations").getJSONObject(0))
    }

    private CalcDBDesign loadDesign(String resourceString) {
        JSONObject json = getResource(resourceString)
        return new CalcDBDesign(json.getJSONArray('designs').getJSONObject(0))
    }

    private CalcDBProject loadProject(String resourceString) {
        JSONObject json = getResource(resourceString)
        JSONArray ray = json.getJSONArray("projects")
        JSONObject projectJson = ray.getJSONObject(0)
        return new CalcDBProject(projectJson)
    }

    private JSONObject getResource(String resourceString) {
        String text = getClass().getResourceAsStream(resourceString).text
        return JSONObject.fromObject(text)
    }
}
