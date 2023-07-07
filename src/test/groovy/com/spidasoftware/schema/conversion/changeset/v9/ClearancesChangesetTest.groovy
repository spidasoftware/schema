/*
 * ©2009-2022 SPIDAWEB LLC
 */

package com.spidasoftware.schema.conversion.changeset.v9

import groovy.json.JsonSlurper
import spock.lang.Specification

class ClearancesChangesetTest extends Specification {

    def "revert client data"() {
        setup:
            def changeSet = new ClearancesChangeset()
            def stream = ClearancesChangeset.getResourceAsStream("/conversions/v9/clearances.v9.client.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.clearanceCases
            json.environments
            json.defaultEnvironment
            json.defaultWireClasses
            json.componentGroups
            json.wireClasses
            json.wireStates
            json.assemblies[0].assemblyStructure.wireEndPoints[0].calculateClearances
            json.assemblies[0].assemblyStructure.wireEndPoints[0].environmentRegions
        when:
            changeSet.revertClientData(json)
        then:
            !json.clearanceCases
            !json.environments
            !json.defaultEnvironment
            !json.defaultWireClasses
            !json.componentGroups
            !json.wireClasses
            !json.wireStates
            !json.assemblies[0].assemblyStructure.wireEndPoints[0].calculateClearances
            !json.assemblies[0].assemblyStructure.wireEndPoints[0].environmentRegions
    }

    def "revert project"() {
        setup:
            def changeSet = new ClearancesChangeset()
            def stream = ClearancesChangeset.getResourceAsStream("/conversions/v9/clearances.v9.project.json")
            Map json = new JsonSlurper().parse(stream) as Map
            stream.close()
        expect:
            json.defaultClearanceCases
            json.terrainLayers
            json.appliedTerrainLayers
            json.clientData.clearanceCases
            json.clientData.environments
            json.clientData.defaultEnvironment
            json.clientData.componentGroups
            json.clientData.wireClasses
            json.clientData.wireStates
            json.clientData.assemblies[0].assemblyStructure.wireEndPoints[0].calculateClearances
            ((Map)json.clientData.assemblies[0].assemblyStructure.wireEndPoints[0]).containsKey("environmentRegions")
            json.leads[0].locations[0].designs[0].clearanceCases
            json.leads[0].locations[0].designs[0].clearanceResults
        when:
            changeSet.revertProject(json)
        then:
            !json.defaultClearanceCases
            !json.terrainLayers
            !json.appliedTerrainLayers
            !json.clientData.clearanceCases
            !json.clientData.environments
            !json.clientData.defaultEnvironment
            !json.clientData.componentGroups
            !json.clientData.wireClasses
            !json.clientData.wireStates
            !json.clientData.assemblies[0].assemblyStructure.wireEndPoints[0].calculateClearances
            !((Map)json.clientData.assemblies[0].assemblyStructure.wireEndPoints[0]).containsKey("environmentRegions")
            !json.leads[0].locations[0].designs[0].clearanceCases
            !json.leads[0].locations[0].designs[0].clearanceResults
    }
}
