package com.spidasoftware.schema.conversion

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
public class CalcDBComponentTest {
    CalcDBProject project
    CalcDBLocation location
    CalcDBDesign design

    @Before
    public void setUp() throws Exception {
        project = loadProject()
        location = loadLocation()
        design = loadDesign()
    }

    @Test
    public void testGetCalcDBId() throws Exception {
        assertEquals("project should return correct _id", "52d065f1e4b0576ebf7c3900", project.getCalcDBId())
        assertEquals("location should return correct _id", "52d065f1e4b0576ebf7c3901", location.getCalcDBId())
        assertEquals("design should return the correct _id", "52d065f1e4b0576ebf7c3902", design.getCalcDBId())
    }

    @Test
    public void testGetClientFileName() throws Exception {
        ["project", "location", "design"].each{
            assertEquals("${it} should return the correct client file", "Master_1.1.client", this."$it".getClientFileName())
        }
    }

    @Test
    public void testGetDateModified() throws Exception {
        ["project", "location", "design"].each{
            log.debug "${it} dateModified= " + this."$it".getDateModified() + ", dateString= ${this."$it".getJSON().getString("dateModified")}"
            assertNotNull("getDateModified should return the Date", this."$it".getDateModified())

        }
    }

    private CalcDBProject loadProject() {
        return new CalcDBProject(getFirstJSONForType("projects"))
    }

    private CalcDBLocation loadLocation() {
        return new CalcDBLocation(getFirstJSONForType("location"))
    }

    private CalcDBDesign loadDesign() {
        return new CalcDBDesign(getFirstJSONForType("designs"))
    }

    private JSONObject getFirstJSONForType(String type) {
        return getJSONForType(type).get(type)[0]
    }

    private JSONObject getJSONForType(String type) {
        String text = getClass().getResourceAsStream("/formats/calcdb/minimal-project-valid/${type}.json").text
        return JSONObject.fromObject(text)
    }
}
