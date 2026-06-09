/*
 * Copyright (c) 2026 Bentley Systems, Incorporated. All rights reserved.
 */
package com.spidasoftware.schema.conversion.changeset.v13

import com.spidasoftware.schema.conversion.changeset.ConversionException
import com.spidasoftware.schema.conversion.changeset.client.AbstractClientDataChangeSet
import groovy.transform.CompileStatic

@CompileStatic
class UrlFormFieldChangeSet extends AbstractClientDataChangeSet {

    @Override
    boolean applyToClientData(Map clientDataJSON) throws ConversionException {
        // do nothing
        return false
    }

    @Override
    boolean revertClientData(Map clientDataJSON) throws ConversionException {
        boolean changed = false

        clientDataJSON.locationForms?.each { Map form ->
            changed |= revertForm(form)
        }
        clientDataJSON.projectForms?.each { Map form ->
            changed |= revertForm(form)
        }

        return changed
    }

    @Override
    void revertProject(Map projectJSON) throws ConversionException {
        super.revertProject(projectJSON)

        projectJSON.forms?.each { Map form ->
            revertForm(form)
        }
        projectJSON.defaultLocationForms?.each { Map form ->
            revertForm(form)
        }
    }

    @Override
    void revertLocation(Map locationJSON) throws ConversionException {
        super.revertLocation(locationJSON)
        locationJSON.forms?.each { Map form ->
            revertForm(form)
        }
    }

    protected boolean revertForm(Map form) {
        boolean changed = false

        List<Map> formDefinition = form.formDefinition as List<Map>
        if (!formDefinition) {
            return false
        }

        Map<String, ?> fields = form.fields as Map<String, ?>

        // simple fields
        List<String> urlFieldNames = revertDefinition(formDefinition)
        if (fields) {
            revertFields(fields, urlFieldNames)
        }
        changed |= !urlFieldNames.isEmpty()

        // tab containers
        List<Map> tabFields = formDefinition.findAll { it.fieldType == "customTabbedPane" }
        tabFields.each { Map tabField ->
            urlFieldNames = revertDefinition(tabField.fieldDefinitions as List<Map>)
            Map<String, ?> tabValues = fields?.get(tabField.fieldName as String) as Map<String, ?>
            if (tabValues) {
                revertFields(tabValues, urlFieldNames)
            }
            changed |= !urlFieldNames.isEmpty()
        }

        // table containers
        List<Map> tableFields = formDefinition.findAll { it.fieldType == "customTable" }
        tableFields.each { Map tableField ->
            urlFieldNames = revertDefinition(tableField.fieldDefinitions as List<Map>)
            List<Map> rows = fields?.get(tableField.fieldName as String) as List<Map>
            rows?.each { Map row ->
                revertFields(row as Map<String, ?>, urlFieldNames)
            }
            changed |= !urlFieldNames.isEmpty()
        }

        return changed
    }

    /**
     * Remove any url fields from the form definition, and return a list of the field names of the removed url fields.
     */
    protected List<String> revertDefinition(List<Map<String, String>> definition) {
        List<Map> fieldsToRevert = definition.findAll { it.fieldType == "customUrlField" }
        fieldsToRevert.each { definition.remove(it) }
        return fieldsToRevert*.fieldName
    }

    /**
     * Remove any fields with the given field names from the given fields map.
     */
    protected void revertFields(Map<String, ?> fields, List<String> fieldNamesToRevert) {
        fieldNamesToRevert.each { String fieldName ->
            fields.removeAll {it.key == fieldName }
        }
    }
}
