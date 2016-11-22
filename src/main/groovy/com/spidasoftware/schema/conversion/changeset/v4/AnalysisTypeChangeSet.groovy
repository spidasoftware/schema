package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject

class AnalysisTypeChangeSet extends ChangeSet{

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void apply(JSONObject json) throws ConversionException {
      json.get("leads")?.each { lead ->
        lead.get("locations")?.each { location ->
          location.get("designs")?.each { design ->
            def analyses = design.get("analysis")
            analyses?.each{ analysis ->
              analysis.get("results")?.each { result ->

              def component = result.get("component")
              def unit = result.get("unit")
              boolean isMoment = unit=="NEWTON_METRE" || unit == "POUND_FORCE_FOOT"
              boolean isPoleMoment = isMoment && component=="Pole"

              //Default for poles and cross arms
              def analysisType = "STRESS"
              if(component.contains("-Buckling")){
                analysisType="BUCKLING"
                result.put("component", "Pole")
              }else if(component.contains("-Strength")){
                analysisType="STRENGTH"
                result.put("component", "Pole")
              }else if(component.contains("-Deflection")){
                analysisType="DEFLECTION"
                result.put("component", "Pole")
              }else if(component.contains("Insulator") ||
                  component.contains("SidewalkBrace") ||
                  component.contains("Anchor") ||
                  component.contains("Guy") ){
                analysisType="FORCE"
              }else if(component.contains("Foundation") || isPoleMoment){
                analysisType="MOMENT"
              }
                result.put("analysisType", analysisType)
              }
            }
          }
        }
      }
    }

    @Override
    void revert(JSONObject json) throws ConversionException {

    json.get("leads")?.each { lead ->
      lead.get("locations")?.each { location ->
        location.get("designs")?.each { design ->
          def analyses = design.get("analysis")
          analyses?.each{ analysis ->
            analysis.get("results")?.each { result ->
              result.remove("analysisType")
          }
        }
      }
    }
}
}
}
