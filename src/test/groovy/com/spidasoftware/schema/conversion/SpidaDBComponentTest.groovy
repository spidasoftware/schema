package com.spidasoftware.schema.conversion

import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@Log4j
public class SpidaDBComponentTest {
    SpidaDBProject project
    SpidaDBLocation location
    SpidaDBDesign design

    @Before
    public void setUp() throws Exception {
        project = loadProject("/formats/spidadb/minimal-project-valid/projects.json")
        location = loadLocation("/formats/spidadb/minimal-project-valid/locations.json")
        design = loadDesign("/formats/spidadb/minimal-project-valid/designs.json")
    }

    @Test
    public void testGetSpidaDBId() throws Exception {
        assertEquals("project should return correct id", "53e3906d44ae3953e03b39ff", project.getSpidaDBId())
        assertEquals("location should return correct id", "53e3906d44ae3953e03b39fd", location.getSpidaDBId())
        assertEquals("design should return the correct _id", "53e3906d44ae3953e03b39fe", design.getSpidaDBId())
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
        def otherLocation = loadLocation("/formats/spidadb/exampleProject/locations.json")
        def photoIds = otherLocation.getPhotoIds()
        assertEquals("Should be three photos loaded from the json", 3, photoIds.size())
        assertEquals("should have the correct ids", ['12345', '23456', '34567'] as Set, photoIds.toSet())
    }

    private SpidaDBLocation loadLocation(String resourceString) {
        Map json = getResource(resourceString)
        return new SpidaDBLocation(json.get("locations").get(0))
    }

    private SpidaDBDesign loadDesign(String resourceString) {
        Map json = getResource(resourceString)
        return new SpidaDBDesign(json.get('designs').get(0))
    }

    private SpidaDBProject loadProject(String resourceString) {
        Map json = getResource(resourceString)
        List ray = json.get("projects")
        Map projectJson = ray.get(0)
        return new SpidaDBProject(projectJson)
    }

    private Map getResource(String resourceString) {
        return new JsonSlurper().parse(getClass().getResourceAsStream(resourceString))
    }
}
