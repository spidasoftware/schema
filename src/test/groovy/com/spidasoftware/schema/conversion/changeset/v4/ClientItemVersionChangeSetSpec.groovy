package com.spidasoftware.schema.conversion.changeset.v4

import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification

class ClientItemVersionChangeSetSpec extends Specification {

    def "test revert"() {
        setup:
            def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v4/project-v4.json")
            JSONObject projectJSON = new JsonSlurper().parse(leanStream)
            JSONObject locationJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0])
            JSONObject designJSON = JSONObject.fromObject(projectJSON.leads[0].locations[0].designs[0])

            ClientItemVersionChangeSet clientItemVersionChangeSet = new ClientItemVersionChangeSet()
        when: "revertProject"
            clientItemVersionChangeSet.revertProject(projectJSON)
            JSONObject structure = projectJSON.get("leads")[0].get("locations")[0].get("designs")[0].get("structure")
        then:
            structure.get("pole").clientItemVersion == null
            structure.get("anchors")[0].clientItemVersion == null
            structure.get("wires")[0].clientItemVersion == null
            structure.get("spanGuys")[0].clientItemVersion == null
            structure.get("guys")[0].clientItemVersion == null
            structure.get("equipments")[0].clientItemVersion == null
            structure.get("crossArms")[0].clientItemVersion == null
            structure.get("insulators")[0].clientItemVersion == null
            structure.get("pushBraces")[0].clientItemVersion == null
            structure.get("sidewalkBraces")[0].clientItemVersion == null
            structure.get("foundations")[0].clientItemVersion == null
            structure.get("assemblies")[0].clientItemVersion == null
        when: "revertLocation"
            clientItemVersionChangeSet.revertLocation(locationJSON)
            structure = locationJSON.get("designs")[0].get("structure")
        then:
            structure.get("pole").clientItemVersion == null
            structure.get("anchors")[0].clientItemVersion == null
            structure.get("wires")[0].clientItemVersion == null
            structure.get("spanGuys")[0].clientItemVersion == null
            structure.get("guys")[0].clientItemVersion == null
            structure.get("equipments")[0].clientItemVersion == null
            structure.get("crossArms")[0].clientItemVersion == null
            structure.get("insulators")[0].clientItemVersion == null
            structure.get("pushBraces")[0].clientItemVersion == null
            structure.get("sidewalkBraces")[0].clientItemVersion == null
            structure.get("foundations")[0].clientItemVersion == null
            structure.get("assemblies")[0].clientItemVersion == null
        when: "revertDesign"
            clientItemVersionChangeSet.revertDesign(designJSON)
            structure = designJSON.get("structure")
        then:
            structure.get("pole").clientItemVersion == null
            structure.get("anchors")[0].clientItemVersion == null
            structure.get("wires")[0].clientItemVersion == null
            structure.get("spanGuys")[0].clientItemVersion == null
            structure.get("guys")[0].clientItemVersion == null
            structure.get("equipments")[0].clientItemVersion == null
            structure.get("crossArms")[0].clientItemVersion == null
            structure.get("insulators")[0].clientItemVersion == null
            structure.get("pushBraces")[0].clientItemVersion == null
            structure.get("sidewalkBraces")[0].clientItemVersion == null
            structure.get("foundations")[0].clientItemVersion == null
            structure.get("assemblies")[0].clientItemVersion == null
    }
}
