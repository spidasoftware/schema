package com.spidasoftware.schema.conversion.changeset.v5

import com.spidasoftware.schema.conversion.changeset.calc.CalcProjectChangeSet

import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import groovy.util.logging.Log4j
import spock.lang.Shared
import spock.lang.Specification

@Log4j
class RemoveAdditionalPropertiesChangesetTest extends Specification {

    @Shared Map projectJSON, locationJSON, designJSON
    RemoveAdditionalPropertiesChangeset changeset = new RemoveAdditionalPropertiesChangeset()

    void setupSpec() {
        def leanStream = getClass().getResourceAsStream("/conversions/v5/remove-additional-properties-project.json")
        projectJSON = new JsonSlurper().parse(leanStream)
        locationJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0])
        designJSON = CalcProjectChangeSet.duplicateAsJson(projectJSON.leads[0].locations[0].designs[0])

        assert projectJSON.address.projectAddressAdditionalProperty != null
        assert projectJSON.leads[0].leadAdditionalProperty != null
        assert projectJSON.leads[0].locations[0].locationAdditionalProperty != null
        assert projectJSON.leads[0].locations[0].designs[0].designAdditionalProperty != null
    }

    void testRevertProject() {
        when:
            projectJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
        when:
            changeset.revertProject(projectJSON)
            projectJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/project-v4.schema", projectJSON).isSuccess()
            projectJSON.forms[0].title == "Project Info"
            projectJSON.forms[0].template == "9193d9104fabd741dcc2092c29a2efb9-Project Info"
            projectJSON.forms[0].fields.size() == 5
            projectJSON.forms[0].fields."Year Installed" == "2018"
            projectJSON.forms[0].fields."Location" == "Ohio"
            projectJSON.forms[0].fields."Technician/Planner" == "Someone"
            projectJSON.forms[0].fields."TD #" == "123456789"
            projectJSON.forms[0].fields."District" == "22- Montebello"
            projectJSON.leads[0].locations[0].forms[0].title == "SAP"
            projectJSON.leads[0].locations[0].forms[0].template == "9193d9104fabd741dcc2092c29a2efb9-SAP"
            projectJSON.leads[0].locations[0].forms[0].fields.size() == 18
            projectJSON.leads[0].locations[0].forms[0].fields."Field Inspection Date" == "08/08/2017"
            projectJSON.leads[0].locations[0].forms[0].fields."Grandfather" == "No"
    }

    void testRevertLocation() {
        when:
            locationJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/location-v4.schema", locationJSON).isSuccess()
        when:
            changeset.revertLocation(locationJSON)
            locationJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/location-v4.schema", locationJSON).isSuccess()
            locationJSON.forms[0].title == "SAP"
            locationJSON.forms[0].template == "9193d9104fabd741dcc2092c29a2efb9-SAP"
            locationJSON.forms[0].fields.size() == 18
            locationJSON.forms[0].fields."Field Inspection Date" == "08/08/2017"
            locationJSON.forms[0].fields."Grandfather" == "No"
    }

    void testRevertDesign() {
        when:
            designJSON.put("strict", true)
        then:
            !new Validator().validateAndReport("/schema/spidacalc/calc/design-v4.schema", designJSON).isSuccess()
        when:
            changeset.revertDesign(designJSON)
            designJSON.put("strict", true)
        then:
            new Validator().validateAndReport("/schema/spidacalc/calc/design-v4.schema", designJSON).isSuccess()
    }

    // address should be an object, leads should be an array
    void "test invalid values"() {
        setup:
            Map project = [schema: "/schema/spidacalc/calc/project.schema", clientFile: "Demo.client", address: [[test: "test"]], leads: ["test": "test"]]
            Map copiedProject = project.clone()
        when:
            changeset.revertProject(project)
            projectJSON.put("strict", true)
        then:
            copiedProject == project
    }

    void testJunkJSON() {
        setup:
            Map badProjectJSON = new JsonSlurper().parseText("""{
    "glossary": {
        "title": "example glossary",
        "GlossDiv": {
            "title": "S",
            "GlossList": {
                "GlossEntry": {
                    "ID": "SGML",
                    "SortAs": "SGML",
                    "GlossTerm": "Standard Generalized Markup Language",
                    "Acronym": "SGML",
                    "Abbrev": "ISO 8879:1986",
                    "GlossDef": {
                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
                        "GlossSeeAlso": ["GML", "XML"]
                    },
                    "GlossSee": "markup"
                }
            }
        }
    }
}""")
            Map badLocationJSON = CalcProjectChangeSet.duplicateAsJson(badProjectJSON)
            Map badDesignJSON = CalcProjectChangeSet.duplicateAsJson(badProjectJSON)
        when:
            changeset.revertProject(badProjectJSON)
        then:
            badProjectJSON.isEmpty()
        when:
            changeset.revertLocation(badLocationJSON)
        then:
            badLocationJSON.isEmpty()
        when:
            changeset.revertDesign(badDesignJSON)
        then:
            badDesignJSON.isEmpty()
    }
}