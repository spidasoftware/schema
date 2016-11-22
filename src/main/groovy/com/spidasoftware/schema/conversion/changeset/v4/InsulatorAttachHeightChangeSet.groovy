package com.spidasoftware.schema.conversion.changeset.v4

import com.spidasoftware.schema.conversion.changeset.ChangeSet
import com.spidasoftware.schema.conversion.changeset.ConversionException
import net.sf.json.JSONObject
import net.sf.json.JSONArray

class InsulatorAttachHeightChangeSet extends ChangeSet {

    @Override
    String getSchemaPath() {
        return "/schema/spidacalc/calc/project.schema"
    }

    @Override
    void apply(JSONObject json) throws ConversionException {
		forEachStructure(json) { structure -> 
			structure.get('insulators')?.each({ JSONObject insulator ->
				def attachmentHeight = insulator.get('attachmentHeight')
				if (attachmentHeight) {
					insulator.remove('attachmentHeight')
				}
			})
		}
    }

    @Override
    void revert(JSONObject json) throws ConversionException {
		forEachStructure(json) { structure -> 
			structure.get('insulators')?.each({ JSONObject insulator ->
				def offset = insulator.get('offset')
				def attachmentHeight = insulator.get('attachmentHeight')
				if (offset && !attachmentHeight) {
					insulator.put('attachmentHeight', insulator.get('offset'))
				}
			})
		}
	}

}
