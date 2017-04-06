package com.spidasoftware.schema.conversion

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

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
        assertEquals("project should return correct id", "53e3906d44ae3953e03b39ff", project.getCalcDBId())
        assertEquals("location should return correct id", "53e3906d44ae3953e03b39fd", location.getCalcDBId())
        assertEquals("design should return the correct _id", "53e3906d44ae3953e03b39fe", design.getCalcDBId())
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
            log.debug "${it} dateModified= " + this."$it".getDateModified() + ", dateLong= ${this."$it".getMap().get("dateModified")}"
            assertNotNull("getDateModified should return the Date", this."$it".getDateModified())
        }
    }

    @Test
    public void testGetPhotosFromLocation() {
        def otherLocation = loadLocation("/formats/calcdb/exampleProject/locations.json")
        def photoIds = otherLocation.getPhotoIds()
        assertEquals("Should be three photos loaded from the json", 3, photoIds.size())
        assertEquals("should have the correct ids", ['12345', '23456', '34567'] as Set, photoIds.toSet())
    }

    private CalcDBLocation loadLocation(String resourceString) {
        Map json = getResource(resourceString)
        return new CalcDBLocation(json.get("locations").get(0))
    }

    private CalcDBDesign loadDesign(String resourceString) {
        Map json = getResource(resourceString)
        return new CalcDBDesign(json.get('designs').get(0))
    }

    private CalcDBProject loadProject(String resourceString) {
        Map json = getResource(resourceString)
        List ray = json.get("projects")
        Map projectJson = ray.get(0)
        return new CalcDBProject(projectJson)
    }

    private Map getResource(String resourceString) {
        return new JsonSlurper().parse(getClass().getResourceAsStream(resourceString))
    }
}
