package com.spidasoftware.schema.conversion.changeset.v4

import spock.lang.Specification
import groovy.util.logging.Log4j
import net.sf.json.groovy.JsonSlurper

class AnalysisTypeChangeSetSpec extends Specification {

    AnalysisTypeChangeSet analysisTypeChangeSet

    def "apply and revert"() {
      when:
        def leanStream = AnalysisTypeChangeSet.getResourceAsStream("/conversions/v4/analysis-type.json")
        def json = new JsonSlurper().parse(leanStream)
        analysisTypeChangeSet = new AnalysisTypeChangeSet()
        analysisTypeChangeSet.apply(json)
      then:
        json.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType=="STRESS"
      when:
        analysisTypeChangeSet.revert(json)
      then:
        json.get("leads")[0].get("locations")[0].get("designs")[0].get("analysis")[0].get("results")[0].analysisType==null
    }


}
