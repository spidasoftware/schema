/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v13

import com.spidasoftware.schema.validation.Validator
import groovy.json.JsonSlurper
import spock.lang.Specification

class UrlFormFieldChangeSetTest extends Specification {

    static UrlFormFieldChangeSet changeSet = new UrlFormFieldChangeSet()

    def "revert client data"() {
        setup:
            InputStream stream = UrlFormFieldChangeSetTest.getResourceAsStream("/conversions/v13/urlFormFields-v13-clientfile.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.locationForms.size() == 3

            // form named "location url" with one text field and one url field
            json.locationForms[1].formDefinition.size() == 2
            json.locationForms[1].formDefinition.count { it.fieldType == "customUrlField" } == 1
            json.locationForms[1].fields.size() == 2

            json.projectForms.size() == 4

            // form named "hyperform single field" with one url field
            json.projectForms[0].formDefinition.size() == 1
            json.projectForms[0].formDefinition.count { it.fieldType == "customUrlField" } == 1
            json.projectForms[0].fields.size() == 1

            // form named "tab field containing url" with one tab field containing one text field and one url field
            json.projectForms[2].formDefinition.size() == 1
            json.projectForms[2].formDefinition[0].fieldDefinitions.size() == 2
            json.projectForms[2].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 1
            json.projectForms[2].fields.size() == 1
            json.projectForms[2].fields.tabby.size() == 2

            // form named ""table field containing url" with one table field containing one text field and one url field
            json.projectForms[3].formDefinition.size() == 1
            json.projectForms[3].formDefinition[0].fieldDefinitions.size() == 2
            json.projectForms[3].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 1
            json.projectForms[3].fields.size() == 1
            json.projectForms[3].fields.table.empty
        when:
            boolean reverted = changeSet.revertClientData(json)
        then:
            reverted
        and: "no forms deleted"
            json.locationForms.size() == 3
            json.projectForms.size() == 4
        and: "url fields removed"
            json.locationForms[1].formDefinition.size() == 1
            json.locationForms[1].formDefinition.count { it.fieldType == "customUrlField" } == 0
            json.locationForms[1].fields.size() == 1

            json.projectForms[0].formDefinition.size() == 0
            json.projectForms[0].formDefinition.count { it.fieldType == "customUrlField" } == 0
            json.projectForms[0].fields.size() == 0

            json.projectForms[2].formDefinition.size() == 1
            json.projectForms[2].formDefinition[0].fieldDefinitions.size() == 1
            json.projectForms[2].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 0
            json.projectForms[2].fields.size() == 1
            json.projectForms[2].fields.tabby.size() == 1

            json.projectForms[3].formDefinition.size() == 1
            json.projectForms[3].formDefinition[0].fieldDefinitions.size() == 1
            json.projectForms[3].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 0
            json.projectForms[3].fields.size() == 1
            json.projectForms[3].fields.table.empty
        and: "valid"
            new Validator().validateAndReport("/schema/spidacalc/client/data.schema", json).isSuccess()
    }

    def "revert project"() {
        setup:
        InputStream stream = UrlFormFieldChangeSetTest.getResourceAsStream("/conversions/v13/urlFormFields-v13-project.json")
        Map json = new JsonSlurper().parse(stream) as Map
        stream.close()
        expect:
            json.forms.size() == 4

            // form named "hyperform single field" with one url field
            json.forms[0].formDefinition.size() == 1
            json.forms[0].formDefinition.count { it.fieldType == "customUrlField" } == 1
            json.forms[0].fields.size() == 1

            // form named "tab field containing url" with one tab field containing one text field and one url field
            json.forms[2].formDefinition.size() == 1
            json.forms[2].formDefinition[0].fieldDefinitions.size() == 2
            json.forms[2].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 1
            json.forms[2].fields.size() == 1
            json.forms[2].fields.tabby.size() == 2

            // form named "table field containing url" with one table field containing one text field and one url field
            json.forms[3].formDefinition.size() == 1
            json.forms[3].formDefinition[0].fieldDefinitions.size() == 2
            json.forms[3].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 1
            json.forms[3].fields.size() == 1
            json.forms[3].fields.table.size() == 2
            json.forms[3].fields.table.every { it.size() == 2 }  // two rows with one text field and one url field per row

            json.defaultLocationForms.size() == 1
            // form named "location url" with one text field and one url field
            json.defaultLocationForms[0].formDefinition.size() == 2
            json.defaultLocationForms[0].formDefinition.count { it.fieldType == "customUrlField" } == 1
            json.defaultLocationForms[0].fields.size() == 2

            json.leads[0].locations[0].forms.size() == 1
            // form named "location url" with one text field and one url field
            json.leads[0].locations[0].forms[0].formDefinition.size() == 2
            json.leads[0].locations[0].forms[0].formDefinition.count { it.fieldType == "customUrlField" } == 1
            json.leads[0].locations[0].forms[0].fields.size() == 2
        when:
            changeSet.revertProject(json)
        then:  "no forms deleted"
            json.forms.size() == 4
            json.defaultLocationForms.size() == 1
            json.leads[0].locations[0].forms.size() == 1
        and: "url fields removed"
            json.forms[0].formDefinition.size() == 0
            json.forms[0].formDefinition.count { it.fieldType == "customUrlField" } == 0
            json.forms[0].fields.size() == 0

            json.forms[2].formDefinition.size() == 1
            json.forms[2].formDefinition[0].fieldDefinitions.size() == 1
            json.forms[2].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 0
            json.forms[2].fields.size() == 1
            json.forms[2].fields.tabby.size() == 1

            json.forms[3].formDefinition.size() == 1
            json.forms[3].formDefinition[0].fieldDefinitions.size() == 1
            json.forms[3].formDefinition[0].fieldDefinitions.count { it.fieldType == "customUrlField" } == 0
            json.forms[3].fields.size() == 1
            json.forms[3].fields.table.size() == 2  // no rows removed
            json.forms[3].fields.table.every { it.size() == 1 }  // url field removed from each row; text field remains

            json.defaultLocationForms[0].formDefinition.size() == 1
            json.defaultLocationForms[0].formDefinition.count { it.fieldType == "customUrlField" } == 0
            json.defaultLocationForms[0].fields.size() == 1

            json.leads[0].locations[0].forms[0].formDefinition.size() == 1
            json.leads[0].locations[0].forms[0].formDefinition.count { it.fieldType == "customUrlField" } == 0
            json.leads[0].locations[0].forms[0].fields.size() == 1
        and: "valid"
            new Validator().validateAndReport("/schema/spidacalc/calc/project.schema", json).isSuccess()
    }
}
