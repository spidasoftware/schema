package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import groovy.json.JsonSlurper
import spock.lang.Specification

class DamageRsmChangeSetSpec extends Specification {

    def "test change set"() {
        setup:
            Map design = [structure:
                                [damages: [["remainingSectionModulus": 1]]]
                              ]
            DamageRsmChangeSet changeSet = new DamageRsmChangeSet()

        when: "apply to design"
            changeSet.applyToDesign(design)
        then: "nothing happens"
            design.structure.damages[0].get('remainingSectionModulus')==1

        when: "revert design"
            changeSet.revertDesign(design)
        then: "property should be gone"
            !design.structure.damages[0].containsKey('remainingSectionModulus')
    }
}
